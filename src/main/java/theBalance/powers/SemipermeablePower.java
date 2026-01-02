package theBalance.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.relics.Semipermeable;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// Helper power for Semipermeable relic
public class SemipermeablePower extends AbstractPower {
    public static final String POWER_ID = BalanceMod.makeID("SemipermeablePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("DefensePower84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("DefensePower32.png"));

    private static final int BONUS_AMOUNT = 2;

    public SemipermeablePower(AbstractCreature owner) {
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
        // Detect when ZeroSumGamePower (stealing power) is applied to player
        if (target == AbstractDungeon.player && source == AbstractDungeon.player &&
            power.ID.equals(ZeroSumGamePower.POWER_ID)) {

            if (AbstractDungeon.player.hasRelic(Semipermeable.ID)) {
                AbstractDungeon.player.getRelic(Semipermeable.ID).flash();
                // The power tracks how much strength was stolen, give bonus strength
                AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                        new StrengthPower(AbstractDungeon.player, BONUS_AMOUNT), BONUS_AMOUNT));
                AbstractDungeon.actionManager.addToBottom(
                    new RelicAboveCreatureAction(AbstractDungeon.player,
                        AbstractDungeon.player.getRelic(Semipermeable.ID)));
            }
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }
}
