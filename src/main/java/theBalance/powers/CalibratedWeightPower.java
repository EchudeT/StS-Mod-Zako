package theBalance.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageRandomEnemyAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.relics.CalibratedWeight;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// Helper power for CalibratedWeight relic
public class CalibratedWeightPower extends AbstractPower {
    public static final String POWER_ID = BalanceMod.makeID("CalibratedWeightPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power32.png"));

    private static final int BLOCK_AMOUNT = 3;
    private static final int DAMAGE_AMOUNT = 5;

    public CalibratedWeightPower(AbstractCreature owner) {
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
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        // Only trigger when player loses strength or dexterity
        if (target != AbstractDungeon.player) {
            return;
        }

        if (power.ID.equals(StrengthPower.POWER_ID) && power.amount < 0) {
            // Losing strength, gain block
            if (AbstractDungeon.player.hasRelic(CalibratedWeight.ID)) {
                AbstractDungeon.player.getRelic(CalibratedWeight.ID).flash();
                AbstractDungeon.actionManager.addToBottom(
                    new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, BLOCK_AMOUNT));
                AbstractDungeon.actionManager.addToBottom(
                    new RelicAboveCreatureAction(AbstractDungeon.player,
                        AbstractDungeon.player.getRelic(CalibratedWeight.ID)));
            }
        } else if (power.ID.equals(DexterityPower.POWER_ID) && power.amount < 0) {
            // Losing dexterity, deal damage to random enemy
            if (AbstractDungeon.player.hasRelic(CalibratedWeight.ID)) {
                AbstractDungeon.player.getRelic(CalibratedWeight.ID).flash();
                AbstractDungeon.actionManager.addToBottom(
                    new DamageRandomEnemyAction(
                        new DamageInfo(AbstractDungeon.player, DAMAGE_AMOUNT, DamageInfo.DamageType.THORNS),
                        AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(
                    new RelicAboveCreatureAction(AbstractDungeon.player,
                        AbstractDungeon.player.getRelic(CalibratedWeight.ID)));
            }
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }
}
