package theBalance.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.relics.CalibratedScale;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// Helper power for CalibratedScale relic
public class CalibratedScalePower extends AbstractPower {
    public static final String POWER_ID = BalanceMod.makeID("CalibratedScalePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power32.png"));

    private boolean triggeredThisTurn = false;

    public CalibratedScalePower(AbstractCreature owner) {
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
    public void atStartOfTurn() {
        triggeredThisTurn = false;
        if (AbstractDungeon.player.hasRelic(CalibratedScale.ID)) {
            AbstractDungeon.player.getRelic(CalibratedScale.ID).grayscale = false;
        }
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        // Only check when power is applied to the player
        if (target != AbstractDungeon.player || triggeredThisTurn) {
            return;
        }

        // Check if it's strength or dexterity being applied
        if (power.ID.equals(StrengthPower.POWER_ID) || power.ID.equals(DexterityPower.POWER_ID)) {
            int str = 0;
            int dex = 0;

            if (AbstractDungeon.player.hasPower(StrengthPower.POWER_ID)) {
                str = AbstractDungeon.player.getPower(StrengthPower.POWER_ID).amount;
            }
            if (AbstractDungeon.player.hasPower(DexterityPower.POWER_ID)) {
                dex = AbstractDungeon.player.getPower(DexterityPower.POWER_ID).amount;
            }

            // Check if they're equal and not zero
            if (str == dex && str != 0) {
                if (AbstractDungeon.player.hasRelic(CalibratedScale.ID)) {
                    AbstractDungeon.player.getRelic(CalibratedScale.ID).flash();
                    triggeredThisTurn = true;
                    AbstractDungeon.player.getRelic(CalibratedScale.ID).grayscale = true;
                    AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
                    AbstractDungeon.actionManager.addToBottom(
                        new RelicAboveCreatureAction(AbstractDungeon.player,
                            AbstractDungeon.player.getRelic(CalibratedScale.ID)));
                }
            }
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }
}
