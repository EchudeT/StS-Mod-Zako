package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 挑衅面具 - Taunt Mask
// 回合结束时，如果敌人的力量高于你，下回合获得 [E]
public class TauntMask extends CustomRelic {
    public static final String ID = BalanceMod.makeID("TauntMask");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("TauntMask.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    private boolean giveEnergyNextTurn = false;

    public TauntMask() {
        super(ID, IMG, OUTLINE, RelicTier.UNCOMMON, LandingSound.MAGICAL);
    }

    @Override
    public void atTurnStart() {
        if (giveEnergyNextTurn) {
            flash();
            giveEnergyNextTurn = false;
            AbstractDungeon.actionManager.addToBottom(
                new GainEnergyAction(1));
            AbstractDungeon.actionManager.addToBottom(
                new RelicAboveCreatureAction(AbstractDungeon.player, this));
        }
    }

    @Override
    public void onPlayerEndTurn() {
        int playerStr = 0;
        if (AbstractDungeon.player.hasPower(StrengthPower.POWER_ID)) {
            playerStr = AbstractDungeon.player.getPower(StrengthPower.POWER_ID).amount;
        }

        // Check if any enemy has more strength
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped() && m.hasPower(StrengthPower.POWER_ID)) {
                int enemyStr = m.getPower(StrengthPower.POWER_ID).amount;
                if (enemyStr > playerStr) {
                    flash();
                    giveEnergyNextTurn = true;
                    break;
                }
            }
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
