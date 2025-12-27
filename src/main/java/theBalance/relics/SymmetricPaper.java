package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 对称剪纸 - Symmetric Paper
// 回合开始时，如果你的力量与敏捷相等，获得 6 点格挡
public class SymmetricPaper extends CustomRelic {
    public static final String ID = BalanceMod.makeID("SymmetricPaper");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("placeholder_relic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    private static final int BLOCK_AMOUNT = 6;

    public SymmetricPaper() {
        super(ID, IMG, OUTLINE, RelicTier.COMMON, LandingSound.FLAT);
    }

    @Override
    public void atTurnStart() {
        int str = 0;
        int dex = 0;

        if (AbstractDungeon.player.hasPower(StrengthPower.POWER_ID)) {
            str = AbstractDungeon.player.getPower(StrengthPower.POWER_ID).amount;
        }
        if (AbstractDungeon.player.hasPower(DexterityPower.POWER_ID)) {
            dex = AbstractDungeon.player.getPower(DexterityPower.POWER_ID).amount;
        }

        if (str == dex) {
            flash();
            AbstractDungeon.actionManager.addToBottom(
                new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, BLOCK_AMOUNT));
            AbstractDungeon.actionManager.addToBottom(
                new RelicAboveCreatureAction(AbstractDungeon.player, this));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
