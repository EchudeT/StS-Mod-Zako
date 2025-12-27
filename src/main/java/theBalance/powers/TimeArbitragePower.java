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
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 时空套利 - Time Arbitrage
// 接下来2回合敌人跳过行动（通过巨额负力量+延迟恢复实现），之后2回合玩家能量为0
public class TimeArbitragePower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("TimeArbitragePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));

    private int advantageTurns = 2; // 优势回合数（敌人跳过）
    private int penaltyTurns = 0; // 惩罚回合数（玩家能量为0）
    private boolean inPenalty = false; // 是否在惩罚阶段
    private static final int STUN_STRENGTH = 999; // 用于"眩晕"的力量削减值

    public TimeArbitragePower(final AbstractCreature owner) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = 2; // 显示剩余优势回合数
        type = PowerType.BUFF;
        isTurnBased = false;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (inPenalty) {
            description = DESCRIPTIONS[1] + penaltyTurns + DESCRIPTIONS[2];
        } else {
            description = DESCRIPTIONS[0] + advantageTurns + DESCRIPTIONS[2];
        }
    }

    @Override
    public void atStartOfTurnPostDraw() {
        if (!inPenalty && advantageTurns > 0) {
            // 优势阶段：给所有敌人施加巨额负力量，回合结束后恢复
            flash();
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!m.isDeadOrEscaped()) {
                    // 施加-999力量
                    AbstractDungeon.actionManager.addToBottom(
                        new ApplyPowerAction(m, owner, new StrengthPower(m, -STUN_STRENGTH), -STUN_STRENGTH));
                    // 回合结束时恢复
                    AbstractDungeon.actionManager.addToBottom(
                        new ApplyPowerAction(m, owner, new GainStrengthPower(m, STUN_STRENGTH), STUN_STRENGTH));
                }
            }

            advantageTurns--;
            this.amount = advantageTurns;

            if (advantageTurns <= 0) {
                // 进入惩罚阶段
                inPenalty = true;
                penaltyTurns = 2;
                this.amount = penaltyTurns;
                type = PowerType.DEBUFF;
            }
            updateDescription();
        }
    }

    @Override
    public void onEnergyRecharge() {
        if (inPenalty && penaltyTurns > 0) {
            // 惩罚阶段：能量变为0
            flash();
            AbstractDungeon.player.energy.use(AbstractDungeon.player.energy.energy);
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (inPenalty && isPlayer && penaltyTurns > 0) {
            penaltyTurns--;
            this.amount = penaltyTurns;
            updateDescription();

            if (penaltyTurns <= 0) {
                // 惩罚结束，移除Power
                AbstractDungeon.actionManager.addToBottom(
                    new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
            }
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new TimeArbitragePower(owner);
    }
}
