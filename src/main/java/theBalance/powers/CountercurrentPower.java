package theBalance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import java.util.ArrayList;

import static theBalance.BalanceMod.makePowerPath;

public class CountercurrentPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("CountercurrentPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    // 用来记录玩家具体转换了哪些正面效果，以便双倍送给敌人
    private ArrayList<ConvertedBuffData> buffList = new ArrayList<>();
    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("SpecialPower84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("SpecialPower32.png"));

    public CountercurrentPower(final AbstractCreature owner) {
        this.name = powerStrings.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.BUFF; // 这是一个对自己有利的转化，所以分类是BUFF
        this.isTurnBased = false;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        this.flash();
        this.convertAndStore();
    }

    /**
     * 第一阶段：清除负面，转化正面
     */
    private void convertAndStore() {
        // 处理虚弱 -> 转化给玩家 EmpoweredPower
        if (owner.hasPower(WeakPower.POWER_ID)) {
            int amt = owner.getPower(WeakPower.POWER_ID).amount;
            buffList.add(new ConvertedBuffData("EMPOWERED", amt));
            addToTop(new ApplyPowerAction(owner, owner, new EmpoweredPower(owner, amt), amt));
            addToTop(new RemoveSpecificPowerAction(owner, owner, WeakPower.POWER_ID));
        }

        // 处理易伤 -> 转化给玩家 ResilientPower
        if (owner.hasPower(VulnerablePower.POWER_ID)) {
            int amt = owner.getPower(VulnerablePower.POWER_ID).amount;
            buffList.add(new ConvertedBuffData("RESILIENT", amt));
            addToTop(new ApplyPowerAction(owner, owner, new ResilientPower(owner, amt), amt));
            addToTop(new RemoveSpecificPowerAction(owner, owner, VulnerablePower.POWER_ID));
        }

        // 处理负力量 -> 转化为正力量
        // 注意逻辑：如果玩家有 -3 力量，我们需要 Apply (+6) 力量才能变成 +3 力量
        if (owner.hasPower(StrengthPower.POWER_ID)) {
            int strAmt = owner.getPower(StrengthPower.POWER_ID).amount;
            if (strAmt < 0) {
                int positiveValue = -strAmt; // 比如 3
                buffList.add(new ConvertedBuffData(StrengthPower.POWER_ID, positiveValue));
                // 让玩家从 -3 变成 +3，需要增加 2倍的绝对值
                addToTop(new ApplyPowerAction(owner, owner, new StrengthPower(owner, positiveValue * 2), positiveValue * 2));
            }
        }

        // 处理负敏捷 -> 转化为正敏捷
        if (owner.hasPower(DexterityPower.POWER_ID)) {
            int dexAmt = owner.getPower(DexterityPower.POWER_ID).amount;
            if (dexAmt < 0) {
                int positiveValue = -dexAmt;
                buffList.add(new ConvertedBuffData(DexterityPower.POWER_ID, positiveValue));
                addToTop(new ApplyPowerAction(owner, owner, new DexterityPower(owner, positiveValue * 2), positiveValue * 2));
            }
        }

        this.updateDescription();
    }

    /**
     * 第二阶段：回合结束，敌人获得双倍玩家刚才得到的正面 Buff
     */
    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer && !buffList.isEmpty()) {
            this.flash();

            // 遍历所有怪物
            for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                if (!m.isDeadOrEscaped()) {
                    for (ConvertedBuffData data : buffList) {
                        int enemyAmount = data.amount * 2; // 敌人获得双倍玩家转化后的量

                        switch (data.typeID) {
                            case "EMPOWERED":
                                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, owner, new EmpoweredPower(m, enemyAmount), enemyAmount));
                                break;
                            case "RESILIENT":
                                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, owner, new ResilientPower(m, enemyAmount), enemyAmount));
                                break;
                            case StrengthPower.POWER_ID:
                                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, owner, new StrengthPower(m, enemyAmount), enemyAmount));
                                break;
                            case DexterityPower.POWER_ID:
                                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, owner, new DefensiveStancePower(m, enemyAmount), enemyAmount));
                                break;
                        }
                    }
                }
            }

            // 效果完成后，逆流能力消失
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(owner, owner, this.ID));
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new CountercurrentPower(owner);
    }

    private static class ConvertedBuffData {
        String typeID;
        int amount;
        ConvertedBuffData(String id, int amt) { this.typeID = id; this.amount = amt; }
    }

    @Override
    public void updateDescription() {
        // 如果列表为空，说明还没有触发转换，或者身上没有负面效果
        if (buffList.isEmpty()) {
            description = DESCRIPTIONS[0]; // "没有负面效果需要转换。"
        } else {
            // 计算转换的总层数 (例如：3层虚弱 + 2层易伤 = 5)
            int totalConverted = 0;
            for (ConvertedBuffData data : buffList) {
                totalConverted += data.amount;
            }

            // 拼接字符串
            // DESCRIPTIONS[1]: "已转换 #b"
            // totalConverted: 数值 (例如 5)
            // DESCRIPTIONS[2]: " 个负面效果，回合结束时敌人将获得双倍。"
            description = DESCRIPTIONS[1] + totalConverted + DESCRIPTIONS[2];

            // ================================================================
            // 进阶优化（可选）：如果你希望玩家能看到具体转换了什么
            // ================================================================
            /*
            StringBuilder sb = new StringBuilder();
            sb.append(description).append(" NL (");
            for (int i = 0; i < buffList.size(); i++) {
                ConvertedBuffData data = buffList.get(i);
                // 这里你需要获取对应 Power 的名称，简单起见可以用 ID 代替，或者根据 ID 查找 name
                sb.append(data.typeID).append(": ").append(data.amount);
                if (i < buffList.size() - 1) sb.append(", ");
            }
            sb.append(")");
            description = sb.toString();
            */
        }
    }

}