package theBalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.relics.AceAgent;

@SpirePatch(
        clz = AbstractCreature.class,
        method = "addBlock"
)
public class AceAgentPatch {

    @SpirePrefixPatch
    public static void Prefix(AbstractCreature __instance, int amount) {
        // 1. 检查获得格挡的是不是“活着的敌人”
        // __instance 是当前正在获得格挡的生物
        if (__instance instanceof AbstractMonster && !__instance.isDeadOrEscaped()) {

            AbstractPlayer p = AbstractDungeon.player;

            // 2. 检查玩家是否存在，且拥有“王牌代理人”遗物
            if (p != null && p.hasRelic(AceAgent.ID)) {

                // 计算玩家应获得的格挡量
                int playerBlock = (int) (amount * AceAgent.BLOCK_RATE);

                // 3. 如果数值大于0，给玩家加格挡
                if (playerBlock > 0) {
                    // 让遗物闪烁一下，提示玩家效果触发了
                    p.getRelic(AceAgent.ID).flash();

                    // 必须使用 Action 加入队列，否则可能导致逻辑冲突
                    // 注意：这里使用的是 addToBottom，确保在敌人加完格挡后的逻辑间隙插入
                    // 如果你希望更即时，也可以尝试 addToTop，但 addToBottom 通常更安全
                    AbstractDungeon.actionManager.addToBottom(
                            new GainBlockAction(p, p, playerBlock)
                    );
                }
            }
        }
    }
}