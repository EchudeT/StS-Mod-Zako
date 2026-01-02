package theBalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.random.Random;
import theBalance.characters.Zako;
import theBalance.events.TheForkEvent;
import theBalance.skins.SkinManager;

public class ForceEventPatch {

    public static boolean hasEncountered = false;

    // 辅助方法：统一检查是否应该强制触发事件
    private static boolean shouldForceEvent() {
        // 1. 必须是第一层 (Exordium)
        if (AbstractDungeon.id == null || !AbstractDungeon.id.equals(Exordium.ID)) {
            return false;
        }
        // 2. 必须是你的角色，必须是黑化雌小鬼皮肤
        if (AbstractDungeon.player == null
                || AbstractDungeon.player.chosenClass != Zako.Enums.THE_DEFAULT
                || SkinManager.getCurrentSkin().cid != 1) {
            return false;
        }
        // 3. 必须还没遇到过
        if (hasEncountered) {
            return false;
        }
        return true;
    }

    // =================================================================
    // 补丁 1: 重置标记
    // =================================================================
    @SpirePatch(clz = AbstractDungeon.class, method = "generateSeeds")
    public static class ResetFlagPatch {
        @SpirePrefixPatch
        public static void Prefix() {
            hasEncountered = false;
        }
    }

    // =================================================================
    // 补丁 2: 强制问号房间类型为 EVENT
    // =================================================================
    @SpirePatch(clz = EventHelper.class, method = "roll", paramtypez = {Random.class})
    public static class ForceRoomTypePatch {
        @SpirePrefixPatch
        public static SpireReturn<EventHelper.RoomResult> Prefix(Random rng) {
            if (shouldForceEvent()) {
                // 强制返回 EVENT，覆盖 商店/宝箱/怪物
                return SpireReturn.Return(EventHelper.RoomResult.EVENT);
            }
            return SpireReturn.Continue();
        }
    }

    // =================================================================
    // 补丁 3: 拦截 getEvent (普通事件)
    // =================================================================
    @SpirePatch(clz = AbstractDungeon.class, method = "getEvent", paramtypez = {Random.class})
    public static class ForceSpecificEventPatch {
        @SpirePrefixPatch
        public static SpireReturn<AbstractEvent> Prefix(Random rng) {
            if (shouldForceEvent()) {
                hasEncountered = true;
                System.out.println("TheBalanceMod: Forcing TheForkEvent via getEvent!");
                return SpireReturn.Return(new TheForkEvent());
            }
            return SpireReturn.Continue();
        }
    }

    // =================================================================
    // 补丁 4: 拦截 getShrine (新增！关键修复)
    // =================================================================
    // 如果 RNG 判定这次是神龛，getEvent 不会被调用，而是调用 getShrine。
    // 我们需要在这里也拦截，防止变成"金神龛"而错过了你的事件。
    @SpirePatch(clz = AbstractDungeon.class, method = "getShrine", paramtypez = {Random.class})
    public static class ForceSpecificShrinePatch {
        @SpirePrefixPatch
        public static SpireReturn<AbstractEvent> Prefix(Random rng) {
            if (shouldForceEvent()) {
                hasEncountered = true;
                System.out.println("TheBalanceMod: Forcing TheForkEvent via getShrine!");
                return SpireReturn.Return(new TheForkEvent());
            }
            return SpireReturn.Continue();
        }
    }
}