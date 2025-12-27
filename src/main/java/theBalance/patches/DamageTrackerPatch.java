package theBalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class DamageTrackerPatch {

    // 定义两个隐形字段
    @SpirePatch(clz = AbstractPlayer.class, method = SpirePatch.CLASS)
    public static class DamageFields {
        // 本回合是否受过伤
        public static SpireField<Boolean> tookDamageThisTurn = new SpireField<>(() -> false);
        // 上回合是否受过伤
        public static SpireField<Boolean> tookDamageLastTurn = new SpireField<>(() -> false);
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "damage")
    public static class PlayerDamageTracker {
        private static int healthBefore;

        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer __instance, DamageInfo info) {
            healthBefore = __instance.currentHealth;
        }

        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance, DamageInfo info) {
            // 如果执行后血量减少了，说明“失去了生命”
            if (__instance.currentHealth < healthBefore) {
                DamageFields.tookDamageThisTurn.set(__instance, true);
                // logger.info("雌小鬼受伤了！本回合受伤标记已设为 true");
            }
        }
    }

    // 2. 回合转换逻辑：使用 applyStartOfTurn
    @SpirePatch(clz = AbstractPlayer.class, method = "applyStartOfTurnRelics")
    public static class TurnStartSwitchPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance) {
            boolean statusThisTurn = DamageFields.tookDamageThisTurn.get(__instance);
            DamageFields.tookDamageLastTurn.set(__instance, statusThisTurn);

            // 重置本回合计数
            DamageFields.tookDamageThisTurn.set(__instance, false);
            // logger.info("回合开始：上回合受伤状态已更新为: " + statusThisTurn);
        }
    }

    // 3. 战斗前重置：使用 preBattlePrep
    @SpirePatch(clz = AbstractPlayer.class, method = "preBattlePrep")
    public static class BattleResetPatch {
        public static void Postfix(AbstractPlayer __instance) {
            DamageFields.tookDamageThisTurn.set(__instance, false);
            DamageFields.tookDamageLastTurn.set(__instance, false);
        }
    }
}