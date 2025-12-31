package theBalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.random.Random;
import theBalance.characters.Zako;
import theBalance.events.TheForkEvent;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "getEvent"
)
public class ForceEventPatch {

    public static boolean hasEncountered = false;

    @SpirePrefixPatch
    public static SpireReturn<AbstractEvent> Prefix(Random rng) {

        if (!AbstractDungeon.id.equals(Exordium.ID)) {
            return SpireReturn.Continue();
        }

        if (AbstractDungeon.player.chosenClass != Zako.Enums.THE_DEFAULT) {
            return SpireReturn.Continue();
        }

        if (!hasEncountered) {
            hasEncountered = true;
            return SpireReturn.Return(new TheForkEvent());
        }

        return SpireReturn.Continue();
    }
}