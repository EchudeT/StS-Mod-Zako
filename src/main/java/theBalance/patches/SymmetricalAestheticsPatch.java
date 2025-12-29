package theBalance.patches;

import basemod.ReflectionHacks; // 必须确保有这个依赖
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import theBalance.powers.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

@SpirePatch(
        clz = ApplyPowerAction.class,
        method = "update"
)
public class SymmetricalAestheticsPatch {

    @SpirePrefixPatch
    public static void Prefix(ApplyPowerAction __instance) {
        if (AbstractDungeon.player == null || !AbstractDungeon.player.hasPower(SymmetricalAestheticsPower.POWER_ID)) {
            return;
        }

        // 1. 死循环防护
        if (__instance instanceof SymmetricalApplyPowerAction) {
            return;
        }

        // 2. 获取时间字段 (全部使用反射)
        // duration 在父类 AbstractGameAction 中
        float duration = ReflectionHacks.getPrivate(__instance, AbstractGameAction.class, "duration");

        // startingDuration 在当前类 ApplyPowerAction 中
        // 注意：ApplyPowerAction 的字段名通常是 "startingDuration"
        float startingDuration = ReflectionHacks.getPrivate(__instance, ApplyPowerAction.class, "startingDuration");

        // 3. 仅在动作刚开始的那一帧触发
        // 在 update 的第一帧，duration 应该等于 startingDuration
        if (Math.abs(duration - startingDuration) > 0.001f) {
            return;
        }

        // 4. 检查核心条件 (玩家是否拥有能力)


        // 5. 获取 Power 和 Target (使用反射读取 private 字段)
        AbstractPower powerToApply = ReflectionHacks.getPrivate(__instance, ApplyPowerAction.class, "powerToApply");
        AbstractCreature target = __instance.target;

        // 安全检查
        if (target == null || powerToApply == null) return;

        // 6. 只处理 Debuff
        if (powerToApply.type == AbstractPower.PowerType.DEBUFF) {

            // 收集同步目标
            ArrayList<AbstractCreature> targets = new ArrayList<>();
            targets.add(AbstractDungeon.player);
            if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().monsters != null) {
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!m.isDeadOrEscaped()) targets.add(m);
                }
            }

            // 执行同步
            for (AbstractCreature t : targets) {
                if (t == target) continue; // 跳过原目标

                AbstractPower copy = duplicatePower(powerToApply, t);
                if (copy != null) {
                    // 加入自定义动作
                    AbstractDungeon.actionManager.addToBottom(
                            new SymmetricalApplyPowerAction(t, AbstractDungeon.player, copy, copy.amount)
                    );
                }
            }
        }
    }

    // 自定义动作 (用于防止递归)
    public static class SymmetricalApplyPowerAction extends ApplyPowerAction {
        public SymmetricalApplyPowerAction(AbstractCreature target, AbstractCreature source, AbstractPower powerToApply, int stackAmount) {
            super(target, source, powerToApply, stackAmount);
        }
    }


    /**
     * 辅助方法：为新目标复制一个 Power
     * 这是最复杂的部分，因为 Power 的构造函数不统一。
     * 尽量兼容原生 Power 和 Mod Power。
     */
    private static AbstractPower duplicatePower(AbstractPower original, AbstractCreature newTarget) {
        String id = original.ID;
        int amount = original.amount;

        // 1. 硬编码处理常见原生 Debuff
        // 这些 Debuff 有特殊的构造函数或需要 isSourceMonster 参数
        if (id.equals(VulnerablePower.POWER_ID)) {
            return new VulnerablePower(newTarget, amount, false); // 同步给玩家或敌人时，来源都不是怪物
        }
        if (id.equals(WeakPower.POWER_ID)) {
            return new WeakPower(newTarget, amount, false);
        }
        if (id.equals(FrailPower.POWER_ID)) {
            return new FrailPower(newTarget, amount, false);
        }
        if (id.equals(PoisonPower.POWER_ID)) {
            // PoisonPower 需要一个 source
            return new PoisonPower(newTarget, AbstractDungeon.player, amount);
        }
        if (id.equals(ConstrictedPower.POWER_ID)) {
            // ConstrictedPower 也需要一个 source
            return new ConstrictedPower(newTarget, AbstractDungeon.player, amount);
        }
        // 力量（StrengthPower）和敏捷（DexterityPower）也可以是 Debuff（负值）
        if (id.equals(StrengthPower.POWER_ID)) {
            return new StrengthPower(newTarget, amount);
        }
        if (id.equals(DexterityPower.POWER_ID)) {
            // 负敏捷的特殊处理 (如果你的Mod有 ExposedPower)
            if (amount < 0) {
                 return new ExposedPower(newTarget, -amount);
//                return new DexterityPower(newTarget, amount); // 暂时返回负敏捷
            }
            return new DexterityPower(newTarget, amount);
        }
        // 其他你Mod中自定义的Debuff也在这里添加硬编码复制逻辑
        if (id.equals(ExposedPower.POWER_ID)){
            return new ExposedPower(newTarget, amount);
        }

        if (id.equals(SolidPower.POWER_ID)){
            return new SolidPower(newTarget, amount);
        }

        if (id.equals(ResilientPower.POWER_ID)){
            return new ResilientPower(newTarget, amount);
        }

        if (id.equals(DefensiveStancePower.POWER_ID)){
            return new DefensiveStancePower(newTarget, amount);
        }

        if (id.equals(RegenerationPower.POWER_ID)){
            return new RegenerationPower(newTarget, amount);
        }

        // 2. 尝试使用反射处理其他 Power (通用方法)
        // 假设大多数 Power 的构造函数是 (AbstractCreature owner, int amount)
        try {
            Class<? extends AbstractPower> clazz = original.getClass();
            Constructor<? extends AbstractPower> constructor = clazz.getConstructor(AbstractCreature.class, int.class);
            return constructor.newInstance(newTarget, amount);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            // 如果找不到 (owner, amount) 构造函数，尝试 (owner) - 针对无层数的 Debuff
            try {
                Class<? extends AbstractPower> clazz = original.getClass();
                Constructor<? extends AbstractPower> constructor = clazz.getConstructor(AbstractCreature.class);
                return constructor.newInstance(newTarget);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException ex) {
                // 如果都失败了，打印日志并返回 null
                System.err.println("SymmetricalAestheticsPatch: Failed to duplicate power " + id + " for " + newTarget.name + ": " + ex.getMessage());
                return null;
            }
        }
    }
}