package theBalance.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.relics.AceAgent;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// Helper power for AceAgent relic
// Note: This power needs additional patching to intercept block gain events
public class AceAgentPower extends AbstractPower {
    public static final String POWER_ID = BalanceMod.makeID("AceAgentPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power32.png"));

    private static final int MAX_BLOCK_PER_TRIGGER = 15;

    public AceAgentPower(AbstractCreature owner) {
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

    // This method would need to be called by a patch to AbstractCreature.addBlock
    public void onEnemyGainBlock(AbstractMonster monster, int blockGained) {
        if (blockGained > 0 && AbstractDungeon.player.hasRelic(AceAgent.ID)) {
            AbstractDungeon.player.getRelic(AceAgent.ID).flash();
            int blockToGain = Math.min(blockGained, MAX_BLOCK_PER_TRIGGER);
            AbstractDungeon.actionManager.addToBottom(
                new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, blockToGain));
            AbstractDungeon.actionManager.addToBottom(
                new RelicAboveCreatureAction(AbstractDungeon.player,
                    AbstractDungeon.player.getRelic(AceAgent.ID)));
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }
}
