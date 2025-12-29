package theBalance.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.cards.InfiniteExpansion;

@SpirePatch(
        clz = AbstractMonster.class,
        method = "calculateDamage"
)
public class InfiniteExpansionIntentPatch {
    static {
        System.out.println("==================================================");
        System.out.println(">>> 补丁类 InfiniteExpansionIntentPatch 已被 JVM 加载 <<<");
        System.out.println("==================================================");
    }
    @SpirePostfixPatch
    public static void Postfix(AbstractMonster __instance, int dmg) {
        // 如果玩家不存在或手牌为空，直接返回
        if (AbstractDungeon.player == null || AbstractDungeon.player.hand == null) {
            return;
        }

        float totalVulnerability = 0.0f;

        // 遍历玩家手牌，计算总的易伤加成
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c instanceof InfiniteExpansion) {
                InfiniteExpansion card = (InfiniteExpansion) c;
                // 获取卡牌定义的费率 (0.25)
                totalVulnerability += (card.expansionStage * InfiniteExpansion.VULNERABLE_RATE);
            }
        }

        // 如果有加成，修改意图显示的伤害值
        if (totalVulnerability > 0.0f) {
            // 1. 使用反射读取当前的 intentDmg
            int currentIntentDmg = ReflectionHacks.getPrivate(__instance, AbstractMonster.class, "intentDmg");

            // 2. 计算新的数值
            int newDmg = (int)(currentIntentDmg * (1.0f + totalVulnerability));

            // 3. 使用反射写回新的 intentDmg
            ReflectionHacks.setPrivate(__instance, AbstractMonster.class, "intentDmg", newDmg);
        }
    }
}