package theBalance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 抗性 - Resilient
// 易伤的反面，减少受到的伤害
public class ResilientPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("ResilientPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));

    public ResilientPower(final AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        type = PowerType.BUFF;
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
    public float atDamageReceive(float damage, DamageInfo.DamageType damageType) {
        // 减少33%受到的伤害（类似易伤增加50%，这里反向）
        if (damageType != DamageInfo.DamageType.HP_LOSS && damageType != DamageInfo.DamageType.THORNS) {
            return damage * 0.75f;
        }
        return damage;
    }

    @Override
    public void atEndOfRound() {
        // 每回合结束减少1层
        if (this.amount == 0) {
            this.addToBot(new com.megacrit.cardcrawl.actions.common.ReducePowerAction(
                this.owner, this.owner, this.ID, 1));
        } else {
            this.amount--;
            if (this.amount == 0) {
                this.addToBot(new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(
                    this.owner, this.owner, this.ID));
            }
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new ResilientPower(owner, amount);
    }
}
