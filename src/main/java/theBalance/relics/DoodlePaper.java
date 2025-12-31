package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 涂鸦画纸 - Doodle Paper
// 机制修改：每回合第一次获得负面状态时，将该状态(1层)施加给全体敌人
public class DoodlePaper extends CustomRelic {
    public static final String ID = BalanceMod.makeID("DoodlePaper");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("DoodlePaper.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    // 记录本回合是否触发过
    public boolean triggeredThisTurn = false;

    public DoodlePaper() {
        super(ID, IMG, OUTLINE, RelicTier.COMMON, LandingSound.FLAT);
    }

    @Override
    public void atTurnStart() {
        // 回合开始重置
        this.triggeredThisTurn = false;
        this.grayscale = false;
    }

    @Override
    public void onVictory() {
        this.triggeredThisTurn = false;
        this.grayscale = false;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}