package theBalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.relics.CalibratedWeight;

public class CalibratedWeightPatch {

    // 辅助检查函数
    private static void checkAndTrigger(AbstractPower power, int changeAmount) {
        // 1. 必须是玩家
        if (power.owner != AbstractDungeon.player) return;

        // 2. 玩家必须有遗物
        if (!AbstractDungeon.player.hasRelic(CalibratedWeight.ID)) return;

        CalibratedWeight relic = (CalibratedWeight) AbstractDungeon.player.getRelic(CalibratedWeight.ID);

        // 3. 判断 Power 类型
        // 如果是力量
        if (StrengthPower.POWER_ID.equals(power.ID)) {
            relic.onLoseStrength();
        }
        // 如果是敏捷
        else if (DexterityPower.POWER_ID.equals(power.ID)) {
            relic.onLoseDexterity();
        }
    }

    // Patch 1: 监听 stackPower (层数叠加)
    // 游戏中绝大多数"失去"属性（如《重新编程》、被怪物Debuff、活动肌肉回合结束）
    // 实际上是调用 stackPower 传入一个负数。
    @SpirePatch(
            clz = AbstractPower.class,
            method = "stackPower"
    )
    public static class StackPowerPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractPower __instance, int stackAmount) {
            // 如果叠加的数量是负数，说明是"失去"
            if (stackAmount < 0) {
                checkAndTrigger(__instance, stackAmount);
            }
        }
    }

    // Patch 2: 监听 reducePower (层数减少)
    // 某些特殊机制会直接调用 reducePower 减少层数
    @SpirePatch(
            clz = AbstractPower.class,
            method = "reducePower"
    )
    public static class ReducePowerPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractPower __instance, int reduceAmount) {
            // reducePower 的参数通常是正数，代表减少了多少
            if (reduceAmount > 0) {
                checkAndTrigger(__instance, reduceAmount);
            }
        }
    }
}