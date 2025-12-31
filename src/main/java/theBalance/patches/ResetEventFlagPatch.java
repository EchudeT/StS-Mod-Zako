package theBalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "generateMap"
)
public class ResetEventFlagPatch {
    @SpirePrefixPatch
    public static void Prefix() {
        ForceEventPatch.hasEncountered = false;
    }
}