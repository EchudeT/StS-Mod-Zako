package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 创可贴 - Band-Aid
// 每当你因自己的卡牌效果失去生命时，获得 6 点格挡
public class BandAid extends CustomRelic {
    public static final String ID = BalanceMod.makeID("BandAid");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("BandAid.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    private static final int BLOCK_AMOUNT = 6;

    public BandAid() {
        super(ID, IMG, OUTLINE, RelicTier.COMMON, LandingSound.FLAT);
    }

    @Override
    public void onLoseHp(int damageAmount) {
        // 当玩家失去生命（通常来自卡牌效果，如LoseHPAction）
        if (damageAmount > 0) {
            flash();
            AbstractDungeon.actionManager.addToTop(
                new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, BLOCK_AMOUNT));
            AbstractDungeon.actionManager.addToTop(
                new RelicAboveCreatureAction(AbstractDungeon.player, this));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
