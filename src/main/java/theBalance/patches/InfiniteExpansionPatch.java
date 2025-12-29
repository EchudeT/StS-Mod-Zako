package theBalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import theBalance.cards.InfiniteExpansion;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "damage"
)
public class InfiniteExpansionPatch {

    @SpirePostfixPatch
    public static void Postfix(AbstractPlayer __instance, DamageInfo info) {
        if (__instance.lastDamageTaken > 0 && info.type != DamageInfo.DamageType.HP_LOSS) {

            resetCardsInGroup(__instance.hand);
            resetCardsInGroup(__instance.drawPile);
            resetCardsInGroup(__instance.discardPile);
        }
    }

    private static void resetCardsInGroup(CardGroup group) {
        for (AbstractCard c : group.group) {
            if (c instanceof InfiniteExpansion) {
                ((InfiniteExpansion) c).resetExpansion();
            }
        }
    }
}