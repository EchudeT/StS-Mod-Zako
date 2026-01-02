package theBalance.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.powers.ScaleTiltPower;

//@SpirePatch(
//        clz = ApplyPowerAction.class,
//        method = "update"
//)
public class ScaleTiltPatch {
//    @SpirePrefixPatch
//    public static void Prefix(ApplyPowerAction __instance) {
//        float duration = ReflectionHacks.getPrivate(__instance, AbstractGameAction.class, "duration");
//        float startingDuration = ReflectionHacks.getPrivate(__instance, ApplyPowerAction.class, "startingDuration");
//
//        if (Math.abs(duration - startingDuration) > 0.001f) {
//            return;
//        }
//
//        if (AbstractDungeon.player == null || !AbstractDungeon.player.hasPower(ScaleTiltPower.POWER_ID)) {
//            return;
//        }
//
//        AbstractCreature target = __instance.target;
//        AbstractPower powerToApply = ReflectionHacks.getPrivate(__instance, ApplyPowerAction.class, "powerToApply");
//
//        if (target == null || powerToApply == null) return;
//
//        // 核心逻辑判断
//        // 条件A: 目标必须是怪物 (防止玩家获得力量时触发递归，或者误伤队友)
//        // 条件B: 施加的能力必须是力量
//        // 条件C: 力量数值必须 > 0 (如果是失去力量，我们通常不跟着失去)
//        if (target instanceof AbstractMonster &&
//                StrengthPower.POWER_ID.equals(powerToApply.ID) &&
//                powerToApply.amount > 0) {
//
//            int amountToGain = powerToApply.amount;
//
//            AbstractDungeon.player.getPower(ScaleTiltPower.POWER_ID).flash();
//
//            AbstractDungeon.actionManager.addToBottom(
//                    new ApplyPowerAction(
//                            AbstractDungeon.player,
//                            AbstractDungeon.player,
//                            new StrengthPower(AbstractDungeon.player, amountToGain),
//                            amountToGain
//                    )
//            );
//        }
//    }
}