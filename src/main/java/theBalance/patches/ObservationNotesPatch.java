package theBalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.relics.ObservationNotes;

// 监听施加能力动作
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
public class ObservationNotesPatch {

    @SpirePostfixPatch
    public static void Postfix(ApplyPowerAction __instance, AbstractCreature target, AbstractCreature source, AbstractPower powerToApply, int stackAmount, boolean isFast, AbstractGameAction.AttackEffect effect) {

        // 1. 来源必须是玩家
        if (source != AbstractDungeon.player) return;

        // 2. 玩家必须持有遗物
        if (!AbstractDungeon.player.hasRelic(ObservationNotes.ID)) return;

        // 3. 目标必须是敌人 (对自己上Debuff通常不算"观察敌人")
        if (!(target instanceof AbstractMonster)) return;

        // 4. 施加的必须是 DEBUFF
        if (powerToApply.type != AbstractPower.PowerType.DEBUFF) return;

        // === 触发遗物逻辑 ===
        ObservationNotes relic = (ObservationNotes) AbstractDungeon.player.getRelic(ObservationNotes.ID);
        relic.onDebuffApplied();
    }
}