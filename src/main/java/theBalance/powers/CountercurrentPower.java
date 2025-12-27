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
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import java.util.ArrayList;

import static theBalance.BalanceMod.makePowerPath;

// 逆流 - Countercurrent
// 将自身负面属性转为正面，下回合敌人获双倍该属性
public class CountercurrentPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("CountercurrentPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));

    private ArrayList<PowerData> storedDebuffs = new ArrayList<>();

    public CountercurrentPower(final AbstractCreature owner) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        type = PowerType.BUFF;
        isTurnBased = true;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        // 保存并转换负面效果
        convertDebuffs();
        updateDescription();
    }

    private void convertDebuffs() {
        // 处理虚弱 -> 强化
        if (owner.hasPower(WeakPower.POWER_ID)) {
            int weakAmount = owner.getPower(WeakPower.POWER_ID).amount;
            storedDebuffs.add(new PowerData(WeakPower.POWER_ID, weakAmount));
            AbstractDungeon.actionManager.addToBottom(
                new RemoveSpecificPowerAction(owner, owner, WeakPower.POWER_ID));
            // 给玩家强化
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(owner, owner, new EmpoweredPower(owner, weakAmount), weakAmount));
        }

        // 处理易伤 -> 抗性
        if (owner.hasPower(VulnerablePower.POWER_ID)) {
            int vulnAmount = owner.getPower(VulnerablePower.POWER_ID).amount;
            storedDebuffs.add(new PowerData(VulnerablePower.POWER_ID, vulnAmount));
            AbstractDungeon.actionManager.addToBottom(
                new RemoveSpecificPowerAction(owner, owner, VulnerablePower.POWER_ID));
            // 给玩家抗性
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(owner, owner, new ResilientPower(owner, vulnAmount), vulnAmount));
        }

        // 处理脆弱 -> 坚固
        if (owner.hasPower(FrailPower.POWER_ID)) {
            int frailAmount = owner.getPower(FrailPower.POWER_ID).amount;
            storedDebuffs.add(new PowerData(FrailPower.POWER_ID, frailAmount));
            AbstractDungeon.actionManager.addToBottom(
                new RemoveSpecificPowerAction(owner, owner, FrailPower.POWER_ID));
            // 给玩家坚固
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(owner, owner, new SolidPower(owner, frailAmount), frailAmount));
        }

        // 处理负力量
        if (owner.hasPower(StrengthPower.POWER_ID)) {
            int strAmount = owner.getPower(StrengthPower.POWER_ID).amount;
            if (strAmount < 0) {
                storedDebuffs.add(new PowerData(StrengthPower.POWER_ID, -strAmount));
                AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(owner, owner, new StrengthPower(owner, -strAmount * 2), -strAmount * 2));
            }
        }

        // 处理负敏捷 -> 转换为正敏捷，给敌人防御姿态（因为敌人正敏捷格挡没意义）
        if (owner.hasPower(DexterityPower.POWER_ID)) {
            int dexAmount = owner.getPower(DexterityPower.POWER_ID).amount;
            if (dexAmount < 0) {
                // 存储负敏捷的绝对值
                storedDebuffs.add(new PowerData("CONVERTED_DEX_TO_STANCE", -dexAmount));
                // 将负敏捷转为正敏捷
                AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(owner, owner, new DexterityPower(owner, -dexAmount), -dexAmount));
            }
        }

        this.amount = storedDebuffs.size();
    }

    @Override
    public void updateDescription() {
        if (storedDebuffs.isEmpty()) {
            description = DESCRIPTIONS[0];
        } else {
            description = DESCRIPTIONS[1] + storedDebuffs.size() + DESCRIPTIONS[2];
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer && !storedDebuffs.isEmpty()) {
            flash();
            // 给所有敌人双倍debuff
            for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!monster.isDeadOrEscaped()) {
                    for (PowerData data : storedDebuffs) {
                        AbstractPower powerToApply = null;
                        int doubleAmount = data.amount * 2;

                        if (data.powerId.equals(WeakPower.POWER_ID)) {
                            powerToApply = new WeakPower(monster, doubleAmount, false);
                        } else if (data.powerId.equals(VulnerablePower.POWER_ID)) {
                            powerToApply = new VulnerablePower(monster, doubleAmount, false);
                        } else if (data.powerId.equals(FrailPower.POWER_ID)) {
                            powerToApply = new FrailPower(monster, doubleAmount, false);
                        } else if (data.powerId.equals(StrengthPower.POWER_ID)) {
                            powerToApply = new StrengthPower(monster, -doubleAmount);
                        } else if (data.powerId.equals("CONVERTED_DEX_TO_STANCE")) {
                            // 负敏捷转换为防御姿态（敌人攻击后获得格挡）
                            powerToApply = new DefensiveStancePower(monster, doubleAmount);
                        }

                        if (powerToApply != null) {
                            AbstractDungeon.actionManager.addToBottom(
                                new ApplyPowerAction(monster, owner, powerToApply, doubleAmount));
                        }
                    }
                }
            }
        }

        // 回合结束后移除Power
        AbstractDungeon.actionManager.addToBottom(
            new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
    }

    @Override
    public AbstractPower makeCopy() {
        return new CountercurrentPower(owner);
    }

    // 内部类用于存储Power数据
    private static class PowerData {
        String powerId;
        int amount;

        PowerData(String id, int amt) {
            this.powerId = id;
            this.amount = amt;
        }
    }
}
