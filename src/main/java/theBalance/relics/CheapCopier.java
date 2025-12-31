package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 劣质复印机 - Cheap Copier
// 每场战斗中，第一张被消耗的"镜像"系列牌改为放入弃牌堆
public class CheapCopier extends CustomRelic {
    public static final String ID = BalanceMod.makeID("CheapCopier");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("CheapCopier.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    private boolean usedThisCombat = false;

    public CheapCopier() {
        super(ID, IMG, OUTLINE, RelicTier.UNCOMMON, LandingSound.FLAT);
    }

    @Override
    public void atBattleStart() {
        usedThisCombat = false;
        grayscale = false;
    }

    @Override
    public void onExhaust(AbstractCard card) {
        // Check if this is the first mirror card exhausted this combat
        if (!usedThisCombat && BalanceMod.isMirrorCard(card)) {
            flash();
            usedThisCombat = true;
            grayscale = true;

            // Move card to discard pile instead of exhaust pile
            AbstractDungeon.player.limbo.removeCard(card);
            AbstractDungeon.player.discardPile.addToTop(card);
            card.unfadeOut();
        }
    }

    @Override
    public void onVictory() {
        usedThisCombat = false;
        grayscale = false;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
