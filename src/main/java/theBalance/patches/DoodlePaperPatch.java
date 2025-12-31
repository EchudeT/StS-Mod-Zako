package theBalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import theBalance.relics.DoodlePaper;

import java.lang.reflect.Constructor;

public class DoodlePaperPatch {

    @SpirePatch(
            clz = AbstractCreature.class,
            method = "addPower"
    )
    public static class CopyDebuffPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractCreature __instance, AbstractPower powerToApply) {
            // 1. 必须是玩家获得能力
            if (__instance != AbstractDungeon.player) return;

            // 2. 必须是 Debuff
            if (powerToApply.type != AbstractPower.PowerType.DEBUFF) return;

            // 3. 玩家必须持有遗物
            if (!AbstractDungeon.player.hasRelic(DoodlePaper.ID)) return;

            DoodlePaper relic = (DoodlePaper) AbstractDungeon.player.getRelic(DoodlePaper.ID);

            // 4. 本回合必须未触发过
            if (relic.triggeredThisTurn) return;

            // === 触发逻辑 ===
            relic.flash();
            relic.triggeredThisTurn = true;
            relic.grayscale = true; // 变灰表示本回合已用

            // 遍历所有活着的敌人
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!m.isDeadOrEscaped()) {
                    // 创建一个对应的 Debuff 副本
                    AbstractPower debuffCopy = createPowerCopy(powerToApply, m);

                    if (debuffCopy != null) {
                        // 加入动作队列
                        AbstractDungeon.actionManager.addToBottom(
                                new ApplyPowerAction(m, AbstractDungeon.player, debuffCopy, debuffCopy.amount));
                    }
                }
            }
        }
    }

    /**
     * 辅助方法：根据传入的 Power 创建一个新的实例给目标
     * 因为 Power 往往绑定了 owner，不能直接复用对象，必须 new 一个新的。
     * 这里处理了常见的 Debuff，对于未知的 Debuff，默认给易伤作为保底。
     */
    private static AbstractPower createPowerCopy(AbstractPower original, AbstractCreature target) {
        int amount = original.amount;

        switch (original.ID) {
            case VulnerablePower.POWER_ID:
                return new VulnerablePower(target, amount, false);
            case WeakPower.POWER_ID:
                return new WeakPower(target, amount, false);
            case FrailPower.POWER_ID:
                return new FrailPower(target, amount, false);
            case StrengthPower.POWER_ID:
                // 力量比较特殊，Debuff的力量是负数，但构造函数里通常传负数
                return new StrengthPower(target, original.amount);
            case DexterityPower.POWER_ID:
                return new DexterityPower(target, original.amount);
            case ConstrictedPower.POWER_ID:
                return new ConstrictedPower(target, AbstractDungeon.player, original.amount);
        }

        // 2. 反射尝试：通用构造函数匹配
        Class<? extends AbstractPower> clazz = original.getClass();

        // 尝试顺序 1: (Owner, Amount, boolean isSourceMonster)
        try {
            Constructor<?> c = clazz.getConstructor(AbstractCreature.class, int.class, boolean.class);
            return (AbstractPower) c.newInstance(target, 1, false);
        } catch (NoSuchMethodException e) {
            // 继续尝试下一个
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 尝试顺序 2: (Owner, Amount)
        try {
            Constructor<?> c = clazz.getConstructor(AbstractCreature.class, int.class);
            return (AbstractPower) c.newInstance(target, 1);
        } catch (NoSuchMethodException e) {
            // 继续
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 尝试顺序 3: (Owner, Source, Amount)
        try {
            Constructor<?> c = clazz.getConstructor(AbstractCreature.class, AbstractCreature.class, int.class);
            return (AbstractPower) c.newInstance(target, AbstractDungeon.player, 1);
        } catch (NoSuchMethodException e) {
            // 继续
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 尝试顺序 4: (Owner)
        try {
            Constructor<?> c = clazz.getConstructor(AbstractCreature.class);
            return (AbstractPower) c.newInstance(target);
        } catch (NoSuchMethodException e) {
            // 继续
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. 彻底失败：保底方案
        System.out.println("BalanceMod: 无法通过反射复制 Power [" + original.name + "] (" + original.ID + ")");

        // 返回易伤作为"涂鸦"的混乱效果，或者返回 null 什么都不做
        return new VulnerablePower(target, 1, false);

    }
}