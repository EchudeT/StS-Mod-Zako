package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 共感娃娃 - Empathy Doll
// 当你拥有负面状态时，你对敌人施加的负面状态层数 +1
public class EmpathyDoll extends CustomRelic {
    public static final String ID = BalanceMod.makeID("EmpathyDoll");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("EmpathyDoll.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    public EmpathyDoll() {
        super(ID, IMG, OUTLINE, RelicTier.RARE, LandingSound.MAGICAL);
    }


    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}