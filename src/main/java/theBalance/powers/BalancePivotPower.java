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
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 天平支点 - Balance Pivot
// 本回合力量和防御姿态为0，回合结束恢复
public class BalancePivotPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("BalancePivotPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));

    private int originalStrength;
    private int originalStance;

    public BalancePivotPower(final AbstractCreature owner, int str, int stance) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.originalStrength = str;
        this.originalStance = stance;
        this.amount = -1;
        type = PowerType.DEBUFF;
        isTurnBased = true;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public void atEndOfRound() {
        flash();
        // 恢复原来的力量和防御姿态
        if (originalStrength != 0) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(owner, owner, new StrengthPower(owner, originalStrength), originalStrength));
        }
        if (originalStance > 0) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(owner, owner, new DefensiveStancePower(owner, originalStance), originalStance));
        }
        // 移除此Power
        AbstractDungeon.actionManager.addToBottom(
            new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
    }

    @Override
    public void onRemove() {
        // 如果Power被提前移除，确保恢复力量和防御姿态
        if (owner != null && !owner.isDeadOrEscaped()) {
            if (originalStrength != 0) {
                AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(owner, owner, new StrengthPower(owner, originalStrength), originalStrength));
            }
            if (originalStance > 0) {
                AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(owner, owner, new DefensiveStancePower(owner, originalStance), originalStance));
            }
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new BalancePivotPower(owner, originalStrength, originalStance);
    }
}
