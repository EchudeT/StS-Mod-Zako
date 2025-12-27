package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 幸运硬币 - Lucky Coin
// 每次战斗获胜后多获得 10-15 金币
public class LuckyCoin extends CustomRelic {
    public static final String ID = BalanceMod.makeID("LuckyCoin");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("placeholder_relic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    private static final int MIN_GOLD = 10;
    private static final int MAX_GOLD = 15;

    public LuckyCoin() {
        super(ID, IMG, OUTLINE, RelicTier.COMMON, LandingSound.CLINK);
    }

    @Override
    public void onVictory() {
        flash();
        int goldGained = AbstractDungeon.miscRng.random(MIN_GOLD, MAX_GOLD);
        AbstractDungeon.player.gainGold(goldGained);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
