package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import theBalance.BalanceMod;
import theBalance.powers.ExemptionPower;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 耍赖证明 - Cheating Proof
// 每回合你打出的第一张带负面效果的牌，其负面部分不再生效
public class CheatingProof extends CustomRelic {
    public static final String ID = BalanceMod.makeID("CheatingProof");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("placeholder_relic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    private boolean usedThisTurn = false;

    public CheatingProof() {
        super(ID, IMG, OUTLINE, RelicTier.RARE, LandingSound.MAGICAL);
    }

    @Override
    public void atTurnStart() {
        usedThisTurn = false;
        grayscale = false;
        // Apply exemption power for the first negative card this turn
        if (!AbstractDungeon.player.hasPower(ExemptionPower.POWER_ID)) {
            flash();
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    new ExemptionPower(AbstractDungeon.player), -1));
            AbstractDungeon.actionManager.addToBottom(
                new RelicAboveCreatureAction(AbstractDungeon.player, this));
        }
    }

    @Override
    public void onVictory() {
        usedThisTurn = false;
        grayscale = false;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
