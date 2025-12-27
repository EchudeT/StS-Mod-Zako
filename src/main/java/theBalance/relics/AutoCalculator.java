package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import theBalance.BalanceMod;
import theBalance.powers.CombatGoldPower;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 自动计算器 - Auto Calculator
// 每当你洗牌时，获得等同于当前战斗津贴 50% 的格挡
public class AutoCalculator extends CustomRelic {
    public static final String ID = BalanceMod.makeID("AutoCalculator");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("placeholder_relic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    public AutoCalculator() {
        super(ID, IMG, OUTLINE, RelicTier.UNCOMMON, LandingSound.FLAT);
    }

    @Override
    public void onShuffle() {
        if (AbstractDungeon.player.hasPower(CombatGoldPower.POWER_ID)) {
            flash();
            CombatGoldPower combatGold = (CombatGoldPower) AbstractDungeon.player.getPower(CombatGoldPower.POWER_ID);
            int blockAmount = combatGold.amount / 2;

            if (blockAmount > 0) {
                AbstractDungeon.actionManager.addToBottom(
                    new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, blockAmount));
                AbstractDungeon.actionManager.addToBottom(
                    new RelicAboveCreatureAction(AbstractDungeon.player, this));
            }
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
