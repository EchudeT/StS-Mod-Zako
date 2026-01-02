package theBalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BuffTrackerPatch {
    // =================================================================
    // 2. 回合开始时重置计数
    // =================================================================
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "applyStartOfTurnRelics"
    )
    public static class ResetBuffCountPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance) {
            BuffTrackerField.buffCount.set(__instance, 0);
        }
    }

    // =================================================================
    // 3. 战斗开始前重置 (防止数值带入下一场战斗)
    // =================================================================
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "onVictory"
    )
    public static class ClearOnVictoryPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance) {
            BuffTrackerField.buffCount.set(__instance, 0);
        }
    }
}