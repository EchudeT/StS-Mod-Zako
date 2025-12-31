package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 王牌代理人 - Ace Agent
// 替换初始遗物。每当敌人获得格挡，你同步获得50%格挡
public class AceAgent extends CustomRelic {
    public static final String ID = BalanceMod.makeID("AceAgent");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("AceAgent.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    public static final float BLOCK_RATE = 0.5F;

    public AceAgent() {
        super(ID, IMG, OUTLINE, RelicTier.BOSS, LandingSound.MAGICAL);
    }


    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}