package theBalance.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import theBalance.relics.DoodlePaper;

import java.lang.reflect.Constructor;

//@SpirePatch(
//        clz = ApplyPowerAction.class,
//        method = "update"
//)
public class DoodlePaperPatch {

//    @SpirePrefixPatch
//    public static void Prefix(ApplyPowerAction __instance) {
//        // 1. 获取动作的持续时间，确保只在动作刚开始的那一帧触发一次
//        float duration = ReflectionHacks.getPrivate(__instance, AbstractGameAction.class, "duration");
//        float startingDuration = ReflectionHacks.getPrivate(__instance, ApplyPowerAction.class, "startingDuration");
//
//        // 如果不是第一帧，直接返回
//        if (duration != startingDuration) {
//            return;
//        }
//
//        // 2. 防止无限递归：如果是我们自己生成的复制动作，跳过
//        if (__instance instanceof DoodleApplyPowerAction) {
//            return;
//        }
//
//        // 3. 获取目标和 Power
//        AbstractCreature target = __instance.target;
//        AbstractCreature source = __instance.source;
//        AbstractPower powerToApply = ReflectionHacks.getPrivate(__instance, ApplyPowerAction.class, "powerToApply");
//
//        // 4. 核心判定逻辑
//        // 目标必须是玩家
//        if (target != AbstractDungeon.player) return;
//
//        // 施加的必须是 Debuff
//        if (powerToApply == null || powerToApply.type != AbstractPower.PowerType.DEBUFF) return;
//
//        // 玩家必须持有遗物
//        if (!AbstractDungeon.player.hasRelic(DoodlePaper.ID)) return;
//
//        DoodlePaper relic = (DoodlePaper) AbstractDungeon.player.getRelic(DoodlePaper.ID);
//
//        // 本回合必须未触发过 (需要在遗物里写好 atTurnStart 重置逻辑)
//        if (relic.triggeredThisTurn) return;
//
//        // 概率判定（如果你原本的设计是50%概率）
//        // if (AbstractDungeon.cardRandomRng.randomBoolean()) return;
//
//        // === 触发效果 ===
//        relic.flash();
//        relic.triggeredThisTurn = true;
//        relic.grayscale = true; // 变灰
//
//        // 遍历所有活着的敌人
//        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
//            if (!m.isDeadOrEscaped()) {
//                // 复制 Power
//                AbstractPower debuffCopy = createPowerCopy(powerToApply, m);
//
//                if (debuffCopy != null) {
//                    // 加入动作队列
//                    // 注意：使用自定义的 DoodleApplyPowerAction 防止递归触发
//                    AbstractDungeon.actionManager.addToBottom(
//                            new DoodleApplyPowerAction(m, AbstractDungeon.player, debuffCopy, debuffCopy.amount));
//                }
//            }
//        }
//    }
//
//    // =================================================================
//    // 辅助类：自定义 Action 防止无限套娃
//    // =================================================================
//    public static class DoodleApplyPowerAction extends ApplyPowerAction {
//        public DoodleApplyPowerAction(AbstractCreature target, AbstractCreature source, AbstractPower powerToApply, int stackAmount) {
//            super(target, source, powerToApply, stackAmount);
//        }
//    }
//
//    // =================================================================
//    // 辅助方法：Power 复制工厂
//    // =================================================================
//    private static AbstractPower createPowerCopy(AbstractPower original, AbstractCreature target) {
//        int amount = original.amount;
//        String id = original.ID;
//
//        // 1. 常见 Debuff 硬编码 (最稳妥)
//        if (VulnerablePower.POWER_ID.equals(id)) return new VulnerablePower(target, amount, false);
//        if (WeakPower.POWER_ID.equals(id)) return new WeakPower(target, amount, false);
//        if (FrailPower.POWER_ID.equals(id)) return new FrailPower(target, amount, false);
//        if (StrengthPower.POWER_ID.equals(id)) return new StrengthPower(target, amount); // 力量作为Debuff时通常amount是负数，直接传即可
//        if (DexterityPower.POWER_ID.equals(id)) return new DexterityPower(target, amount);
//        if (ConstrictedPower.POWER_ID.equals(id)) return new ConstrictedPower(target, AbstractDungeon.player, amount);
//        if (PoisonPower.POWER_ID.equals(id)) return new PoisonPower(target, AbstractDungeon.player, amount);
//
//        // 2. 反射尝试
//        Class<? extends AbstractPower> clazz = original.getClass();
//
//        // 尝试 (Owner, Amount, boolean isSourceMonster) - 针对易伤/虚弱等
//        try {
//            Constructor<?> c = clazz.getConstructor(AbstractCreature.class, int.class, boolean.class);
//            return (AbstractPower) c.newInstance(target, amount, false);
//        } catch (Exception ignored) {}
//
//        // 尝试 (Owner, Amount) - 最常见的通用构造函数
//        try {
//            Constructor<?> c = clazz.getConstructor(AbstractCreature.class, int.class);
//            return (AbstractPower) c.newInstance(target, amount);
//        } catch (Exception ignored) {}
//
//        // 尝试 (Owner, Source, Amount) - 针对中毒/缠绕
//        try {
//            Constructor<?> c = clazz.getConstructor(AbstractCreature.class, AbstractCreature.class, int.class);
//            return (AbstractPower) c.newInstance(target, AbstractDungeon.player, amount);
//        } catch (Exception ignored) {}
//
//        System.out.println("TheBalance: 无法复制 Power " + id);
//        return null;
//    }
}