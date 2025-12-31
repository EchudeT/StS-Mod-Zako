package theBalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BuffTrackerPatch {

    // =================================================================
    // 1. 监听获得 Buff 的行为
    // =================================================================
    // AbstractCreature.addPower 是游戏中最底层添加能力的方法。
    // 无论是获得新能力，还是堆叠现有层数，都会经过这里。
    @SpirePatch(
            clz = AbstractCreature.class,
            method = "addPower"
    )
    public static class OnGainBuffPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractCreature __instance, AbstractPower powerToApply) {
            // 1. 目标必须是玩家
            if (__instance.isPlayer) {
                // 2. 必须是正面 Buff
                if (powerToApply.type == AbstractPower.PowerType.BUFF) {
                    // 3. 读取当前计数
                    int count = BuffTrackerField.buffCount.get(__instance);
                    // 4. 计数 +1
                    BuffTrackerField.buffCount.set(__instance, count + 1);
                }
            }
        }
    }

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