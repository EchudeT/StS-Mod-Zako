package theBalance.patches;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;
import javassist.CtBehavior;
import theBalance.powers.CombatGoldPower;
import theBalance.relics.BlankCard;
import theBalance.relics.SymbioticCrystal;

import java.util.ArrayList;

public class SpecialRelicPatches {

    // =================================================================
    //  遗物二：老爹的“空白”副卡 Patch 逻辑
    // =================================================================

    // 1. 强制允许出牌：只要有遗物，任何时候都视为能量足够
    @SpirePatch(
            clz = AbstractCard.class,
            method = "hasEnoughEnergy"
    )
    public static class AlwaysEnoughEnergyPatch {
        @SpirePostfixPatch
        public static boolean Postfix(boolean __result, AbstractCard __instance) {
            if (AbstractDungeon.player.hasRelic(BlankCard.ID)) {
                return true; // 总是返回 true，绕过能量检查
            }
            return __result;
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "useCard"
    )
    public static class PayGoldInsteadOfEnergyPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse) {
            if (__instance.hasRelic(BlankCard.ID)) {
                BlankCard relic = (BlankCard) __instance.getRelic(BlankCard.ID);
                relic.flash();

                // 1. 计算总费用
                int totalCostNeeded;
                if (c.cost == -1) {
                    totalCostNeeded = BlankCard.ZERO_COST_PRICE;
                } else if (c.costForTurn <= 0) {
                    totalCostNeeded = BlankCard.ZERO_COST_PRICE;
                } else {
                    totalCostNeeded = c.costForTurn * BlankCard.GOLD_MULTIPLIER;
                }

                int remainingCost = totalCostNeeded;

                // 2. 优先消耗：战斗津贴 (CombatGoldPower)
                int paidFromAllowance = 0;
                if (__instance.hasPower(CombatGoldPower.POWER_ID)) {
                    int allowanceAmt = __instance.getPower(CombatGoldPower.POWER_ID).amount;
                    // 能支付多少就支付多少
                    paidFromAllowance = Math.min(allowanceAmt, remainingCost);

                    remainingCost -= paidFromAllowance;
                }

                // 3. 次级消耗：实际金币 (Gold)
                int paidFromGold = 0;
                if (remainingCost > 0) {
                    paidFromGold = Math.min(__instance.gold, remainingCost);
                    remainingCost -= paidFromGold;
                }

                // 4. 剩余消耗：生命值 (HP)
                int hpToPay = remainingCost;

                // === 执行消耗 ===

                // A. 扣除津贴
                if (paidFromAllowance > 0) {
                    // 使用 ReducePowerAction 来扣除层数，保证视觉同步
                    AbstractDungeon.actionManager.addToBottom(
                            new ReducePowerAction(__instance, __instance, CombatGoldPower.POWER_ID, paidFromAllowance));
                }

                // B. 扣除金币
                if (paidFromGold > 0) {
                    __instance.loseGold(paidFromGold);
                }

                // C. 扣除生命 (如果还没付清)
                if (hpToPay > 0) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(__instance,
                            new DamageInfo(__instance, hpToPay, DamageInfo.DamageType.HP_LOSS),
                            AbstractGameAction.AttackEffect.FIRE));
                }

                // === 能量处理 (保持不变) ===
                if (c.cost != -1) {
                    c.freeToPlayOnce = true;
                }
            }
        }
    }


    // 3. 移除战斗奖励中的金币
    @SpirePatch(
            clz = CombatRewardScreen.class,
            method = "setupItemReward"
    )
    public static class NoGoldRewardPatch {
        @SpirePostfixPatch
        public static void Postfix(CombatRewardScreen __instance) {
            if (AbstractDungeon.player.hasRelic(BlankCard.ID)) {
                // 遍历奖励列表，移除类型为 GOLD 的奖励
                ArrayList<RewardItem> rewardsToRemove = new ArrayList<>();
                for (RewardItem reward : __instance.rewards) {
                    if (reward.type == RewardItem.RewardType.GOLD) {
                        rewardsToRemove.add(reward);
                    }
                }
                __instance.rewards.removeAll(rewardsToRemove);
            }
        }
    }


    // =================================================================
    //  遗物三：共生红水晶 Patch 逻辑
    // =================================================================

    // 1. 锁死生命上限提升
    @SpirePatch(
            clz = AbstractCreature.class,
            method = "increaseMaxHp"
    )
    public static class LockMaxHpPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AbstractCreature __instance, int amount, boolean showEffect) {
            // 只有当受影响的是玩家，且玩家持有该遗物时拦截
            if (__instance instanceof AbstractPlayer && ((AbstractPlayer) __instance).hasRelic(SymbioticCrystal.ID)) {
                // 如果是加血 (amount > 0)，直接拦截，不执行原方法
                if (amount > 0) {
                    ((AbstractPlayer) __instance).getRelic(SymbioticCrystal.ID).flash();
                    // 这里可以加一个文字特效提示玩家
                    AbstractDungeon.effectsQueue.add(new TextAboveCreatureEffect(__instance.hb.cX, __instance.hb.cY, "上限锁定!", Color.RED));
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }

    // 2. 敌人格挡减半 (如果不想用 Power 实现，可以用 Patch 暴力实现)
    @SpirePatch(
            clz = AbstractCreature.class,
            method = "addBlock"
    )
    public static class HalveEnemyBlockPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractCreature __instance, @ByRef int[] amount) {
            // 如果玩家有红水晶
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(SymbioticCrystal.ID)) {
                // 且获得格挡的对象不是玩家（即是敌人）
                // (玩家自己的格挡减半已经在Relic类里通过 onPlayerGainedBlock 实现了，这里只处理敌人)
                if (!(__instance instanceof AbstractPlayer)) {
                    amount[0] = Math.round(amount[0] * 1.5F);
                }
            }
        }
    }
}