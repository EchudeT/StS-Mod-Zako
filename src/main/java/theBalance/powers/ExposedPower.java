package theBalance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 破绽 - Exposed
// 受到攻击时额外受到等量层数的伤害
public class ExposedPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("ExposedPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("AttackPower84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("AttackPower32.png"));

    public ExposedPower(final AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        type = PowerType.DEBUFF;
        isTurnBased = true;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        // 当受到攻击伤害时，额外受到等量层数的伤害
        if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL) {
            flash();
            AbstractDungeon.actionManager.addToTop(
                new DamageAction(owner, new DamageInfo(info.owner, this.amount, DamageInfo.DamageType.THORNS),
                    AbstractGameAction.AttackEffect.FIRE));
        }
        return damageAmount;
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
        return new ExposedPower(owner, amount);
    }
}
