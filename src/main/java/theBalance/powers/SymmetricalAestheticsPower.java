package theBalance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 对称美学 - Symmetrical Aesthetics
// 你与敌人同步获得所有Debuff
public class SymmetricalAestheticsPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("SymmetricalAestheticsPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));

    // 防止递归应用的标志
    private static boolean isApplying = false;

    public SymmetricalAestheticsPower(final AbstractCreature owner) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        type = PowerType.BUFF;
        isTurnBased = false;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        // 防止递归：如果已经在应用中，直接返回
        if (isApplying) {
            return;
        }

        // 当任何Debuff被施加时，同时施加给所有单位
        if (power.type == PowerType.DEBUFF) {
            flash();
            isApplying = true;  // 设置标志
            try {
                // 特殊处理：负敏捷转换为破绽（对敌人）
                boolean isNegativeDex = power.ID.equals(DexterityPower.POWER_ID) && power.amount < 0;

                // 创建新的Power实例（通过反射或使用构造函数）
                AbstractPower newPower = null;
                try {
                    // 尝试使用反射创建新实例
                    newPower = power.getClass().newInstance();
                    newPower.amount = power.amount;
                } catch (Exception e) {
                    // 如果反射失败，直接返回不处理
                    return;
                }

                // 给玩家（玩家获得原始Debuff）
                if (target != AbstractDungeon.player && newPower != null) {
                    AbstractDungeon.actionManager.addToBottom(
                        new ApplyPowerAction(AbstractDungeon.player, source, newPower, power.amount));
                }

                // 给所有敌人
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!m.isDeadOrEscaped() && target != m) {
                        try {
                            AbstractPower enemyPower;
                            int enemyAmount;

                            if (isNegativeDex) {
                                // 负敏捷对敌人转换为破绽
                                enemyPower = new ExposedPower(m, -power.amount);
                                enemyAmount = -power.amount;
                            } else {
                                // 其他Debuff正常复制
                                enemyPower = power.getClass().newInstance();
                                enemyPower.amount = power.amount;
                                enemyAmount = power.amount;
                            }

                            AbstractDungeon.actionManager.addToBottom(
                                new ApplyPowerAction(m, source, enemyPower, enemyAmount));
                        } catch (Exception e) {
                            // 跳过这个敌人
                        }
                    }
                }
            } finally {
                isApplying = false;  // 重置标志
            }
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new SymmetricalAestheticsPower(owner);
    }
}
