package theBalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.relics.CalibratedScale;

public class CalibratedScalePatch {


    // 辅助方法：检查并触发遗物
    private static void checkRelic() {
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(CalibratedScale.ID)) {
            ((CalibratedScale) AbstractDungeon.player.getRelic(CalibratedScale.ID)).checkBalance();
        }
    }

    // 1. 监听层数叠加 (stackPower) - 比如已经有力量了，又打了一张打击+1力量
    @SpirePatch(
            clz = AbstractPower.class,
            method = "stackPower"
    )
    public static class StackPowerPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractPower __instance, int stackAmount) {
            // 只有当变化的 Power 是力量或敏捷，且属于玩家时才检查
            if (__instance.owner == AbstractDungeon.player) {
                if (StrengthPower.POWER_ID.equals(__instance.ID) || DexterityPower.POWER_ID.equals(__instance.ID)) {
                    checkRelic();
                }
            }
        }
    }

    // 2. 监听层数减少 (reducePower) - 比如临时力量消失
    @SpirePatch(
            clz = AbstractPower.class,
            method = "reducePower"
    )
    public static class ReducePowerPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractPower __instance, int reduceAmount) {
            if (__instance.owner == AbstractDungeon.player) {
                if (StrengthPower.POWER_ID.equals(__instance.ID) || DexterityPower.POWER_ID.equals(__instance.ID)) {
                    checkRelic();
                }
            }
        }
    }

    // 3. 监听新增能力 (addPower) - 比如原本没有力量，打出第一张卡获得了力量
    @SpirePatch(
            clz = AbstractCreature.class,
            method = "addPower"
    )
    public static class AddPowerPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCreature __instance, AbstractPower powerToApply) {
            if (__instance == AbstractDungeon.player) {
                if (StrengthPower.POWER_ID.equals(powerToApply.ID) || DexterityPower.POWER_ID.equals(powerToApply.ID)) {
                    checkRelic();
                }
            }
        }
    }
}