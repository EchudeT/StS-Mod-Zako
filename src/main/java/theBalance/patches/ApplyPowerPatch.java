package theBalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

@SpirePatch(clz = ApplyPowerAction.class, method = "update")
public class ApplyPowerPatch {
    // 在 ApplyPowerAction 执行完后触发
    @SpirePostfixPatch
    public static void Postfix(ApplyPowerAction __instance) {
        // 这里的逻辑对应你之前的 Power 逻辑
        // 1. 动作已完成
        // 2. 目标是玩家
        // 3. 施加的是 BUFF
        if (__instance.isDone) {
            // 反射获取私有成员变量（由于 ModTheSpire 已经处理了访问权限，直接读取即可）
            // 我们通过反射或 BaseMod 获取实际要应用的 power
            try {
                // 获取 action 内部的 power 引用
                java.lang.reflect.Field powerToApplyField = ApplyPowerAction.class.getDeclaredField("powerToApply");
                powerToApplyField.setAccessible(true);
                AbstractPower p = (AbstractPower) powerToApplyField.get(__instance);

                if (p != null && __instance.target instanceof AbstractPlayer && p.type == AbstractPower.PowerType.BUFF) {
                    // 给我们的自定义字段 +1
                    int current = BuffTrackerField.buffCount.get(AbstractDungeon.player);
                    BuffTrackerField.buffCount.set(AbstractDungeon.player, current + 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
