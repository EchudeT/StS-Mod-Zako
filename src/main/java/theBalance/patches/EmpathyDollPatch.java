package theBalance.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.relics.EmpathyDoll;

@SpirePatch(
        clz = ApplyPowerAction.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = {
                AbstractCreature.class,
                AbstractCreature.class,
                AbstractPower.class,
                int.class,
                boolean.class,
                AbstractGameAction.AttackEffect.class
        }
)
public class EmpathyDollPatch {

    @SpirePostfixPatch
    public static void Postfix(ApplyPowerAction __instance, AbstractCreature target, AbstractCreature source, AbstractPower powerToApply, int stackAmount, boolean isFast, AbstractGameAction.AttackEffect effect) {

        // 1. 基础检查：来源必须是玩家，且玩家必须持有遗物
        if (source != AbstractDungeon.player) return;
        if (!AbstractDungeon.player.hasRelic(EmpathyDoll.ID)) return;

        // 2. 目标必须是敌人 (对自己上Debuff不加成，否则副作用太大了)
        if (!(target instanceof AbstractMonster)) return;

        // 3. 施加的能力必须是 DEBUFF
        if (powerToApply.type != AbstractPower.PowerType.DEBUFF) return;

        // 4. 核心条件：玩家自身必须拥有至少一个 DEBUFF
        boolean playerHasDebuff = false;
        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (p.type == AbstractPower.PowerType.DEBUFF) {
                playerHasDebuff = true;
                break;
            }
        }

        if (!playerHasDebuff) return;

        // === 触发效果 ===

        // 让遗物闪烁
        AbstractDungeon.player.getRelic(EmpathyDoll.ID).flash();

        // 修改 Action 中的数值
        // 因为 ApplyPowerAction 已经初始化了，我们需要同时修改 Action 的 amount 和 Power 本身的 amount

        // 1. 修改 Action 的 amount
        __instance.amount += 1;

        // 2. 修改 Power 对象的 amount (确保显示正确)
        // powerToApply 是私有变量，但在构造函数参数里我们能拿到引用，或者用 Reflection
        // 这里直接操作传入的 powerToApply 对象引用即可，因为它和 Action 里存的是同一个对象
        powerToApply.amount += 1;

        // 为了保险起见（防止构造函数内部做了深拷贝，虽然原版没有），也可以用反射再次确认
        // AbstractPower powerInAction = ReflectionHacks.getPrivate(__instance, ApplyPowerAction.class, "powerToApply");
        // if (powerInAction != powerToApply) { powerInAction.amount += 1; }
    }
}