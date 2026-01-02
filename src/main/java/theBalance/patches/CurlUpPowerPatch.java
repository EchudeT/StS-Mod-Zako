package theBalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.CurlUpPower;

@SpirePatch(
        clz = CurlUpPower.class,
        method = "onAttacked"
)
public class CurlUpPowerPatch {

    // 使用 Prefix 在原方法执行前拦截
    @SpirePrefixPatch
    public static SpireReturn<Integer> Prefix(CurlUpPower __instance, DamageInfo info, int damageAmount) {

        // 1. 检查拥有者是否是玩家
        if (__instance.owner instanceof AbstractPlayer) {

            // 2. 只有受到攻击且伤害大于0时才触发
            if (damageAmount < __instance.owner.currentHealth && damageAmount > 0 && info.owner != null && info.type == DamageInfo.DamageType.NORMAL) {

                __instance.flash();

                // ★ 关键修改：
                // 原版这里有一句 new ChangeStateAction(...) 导致了崩溃
                // 我们直接跳过动画，只执行加格挡逻辑
                AbstractDungeon.actionManager.addToBottom(
                        new GainBlockAction(__instance.owner, __instance.owner, __instance.amount)
                );

                // 触发一次后移除该能力
                AbstractDungeon.actionManager.addToBottom(
                        new RemoveSpecificPowerAction(__instance.owner, __instance.owner, __instance)
                );
            }

            // 3. 返回 SpireReturn.Return(...) 以阻止原版崩溃代码的执行
            // 必须返回 damageAmount，因为原方法有返回值
            return SpireReturn.Return(damageAmount);
        }

        // 如果拥有者是怪物，继续执行原版逻辑
        return SpireReturn.Continue();
    }
}