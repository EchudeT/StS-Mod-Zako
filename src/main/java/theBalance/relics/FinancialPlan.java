package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import theBalance.BalanceMod;
import theBalance.powers.CombatGoldPower;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 理财计划书 - Financial Plan
// 战斗结束时，剩余战斗津贴的 20% 转化为永久金币
public class FinancialPlan extends CustomRelic {
    public static final String ID = BalanceMod.makeID("FinancialPlan");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("placeholder_relic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    private static final float CONVERSION_RATE = 0.2f; // 20%

    public FinancialPlan() {
        super(ID, IMG, OUTLINE, RelicTier.RARE, LandingSound.MAGICAL);
    }

    @Override
    public void onVictory() {
        if (AbstractDungeon.player.hasPower(CombatGoldPower.POWER_ID)) {
            flash();
            CombatGoldPower combatGold = (CombatGoldPower) AbstractDungeon.player.getPower(CombatGoldPower.POWER_ID);
            int goldToGain = (int) (combatGold.amount * CONVERSION_RATE);

            if (goldToGain > 0) {
                AbstractDungeon.player.gainGold(goldToGain);
            }
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
