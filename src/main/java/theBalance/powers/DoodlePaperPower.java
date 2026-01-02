package theBalance.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.relics.DoodlePaper;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// Helper power for DoodlePaper relic
public class DoodlePaperPower extends AbstractPower {
    public static final String POWER_ID = BalanceMod.makeID("DoodlePaperPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("ZakoPower84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("ZakoPower32.png"));

    public DoodlePaperPower(AbstractCreature owner) {
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
        // Check if a debuff is being applied to the player
        if (target == AbstractDungeon.player && power.type == PowerType.DEBUFF) {
            // 50% chance to spread to all enemies
            if (AbstractDungeon.cardRandomRng.randomBoolean()) {
                if (AbstractDungeon.player.hasRelic(DoodlePaper.ID)) {
                    AbstractDungeon.player.getRelic(DoodlePaper.ID).flash();
                    for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                        if (!m.isDeadOrEscaped()) {
                            try {
                                // Create a copy of the power using reflection
                                AbstractPower enemyPower = power.getClass().getDeclaredConstructor(
                                    AbstractCreature.class, int.class).newInstance(m, power.amount);
                                AbstractDungeon.actionManager.addToBottom(
                                    new ApplyPowerAction(m, AbstractDungeon.player, enemyPower, power.amount));
                            } catch (Exception e) {
                                // If copying fails, skip this enemy
                            }
                        }
                    }
                    AbstractDungeon.actionManager.addToBottom(
                        new RelicAboveCreatureAction(AbstractDungeon.player,
                            AbstractDungeon.player.getRelic(DoodlePaper.ID)));
                }
            }
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }
}
