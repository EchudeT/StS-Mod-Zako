package theBalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageRandomEnemyAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import theBalance.powers.BurnBoatsPower;

@SpirePatch(
        clz = AbstractCreature.class,
        method = "addBlock"
)
public class BurnBoatsPatch {
    @SpirePrefixPatch
    public static void Prefix(AbstractCreature __instance, @ByRef int[] amount) {
        // 1. 必须是玩家
        if (!__instance.isPlayer) return;

        // 2. 必须拥有"破釜沉舟"状态
        if (!__instance.hasPower(BurnBoatsPower.POWER_ID)) return;

        // 3. 格挡量必须大于0
        if (amount[0] <= 0) return;

        // 获取 Power 实例以触发闪烁特效
        BurnBoatsPower power = (BurnBoatsPower) __instance.getPower(BurnBoatsPower.POWER_ID);
        power.flash();

        // === 转化逻辑 ===
        int damageAmount = amount[0];

        // 造成伤害：对随机敌人造成等同于格挡值的伤害
        // 使用 THORNS 类型，代表这是由能力造成的伤害，通常不享受力量加成（视平衡性而定）
        AbstractDungeon.actionManager.addToBottom(
                new DamageRandomEnemyAction(
                        new DamageInfo(__instance, damageAmount, DamageInfo.DamageType.NORMAL),
                        AbstractGameAction.AttackEffect.FIRE
                )
        );

        // === 核心：将获得的格挡归零 ===
        amount[0] = 0;
    }
}