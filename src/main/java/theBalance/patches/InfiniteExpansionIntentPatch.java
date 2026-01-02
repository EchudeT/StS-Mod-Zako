package theBalance.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.cards.InfiniteExpansion;

import java.lang.reflect.Field;

@SpirePatch(
        clz = AbstractMonster.class,
        method = "calculateDamage"
)
public class InfiniteExpansionIntentPatch {
    private static Field intentDmgField;

    static {
        try {
            intentDmgField = AbstractMonster.class.getDeclaredField("intentDmg");
            intentDmgField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }


    @SpirePostfixPatch
    public static void Postfix(AbstractMonster __instance, int dmg) {
        // 如果玩家不存在或手牌为空，直接返回
        if (AbstractDungeon.player == null || AbstractDungeon.player.hand == null) {
            return;
        }

        float totalVulnerability = 0.0f;

        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c == AbstractDungeon.player.cardInUse) {
                continue;
            }

            if (c instanceof InfiniteExpansion) {
                InfiniteExpansion card = (InfiniteExpansion) c;
                totalVulnerability += (card.expansionStage * InfiniteExpansion.VULNERABLE_RATE);
            }
        }

        if (totalVulnerability > 0.0f) {
            try {
                if (intentDmgField != null) {
                    // 使用 cached field 进行读取和写入，速度快得多
                    int currentIntentDmg = intentDmgField.getInt(__instance);
                    int newDmg = (int)(currentIntentDmg * (1.0f + totalVulnerability));
                    intentDmgField.setInt(__instance, newDmg);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}