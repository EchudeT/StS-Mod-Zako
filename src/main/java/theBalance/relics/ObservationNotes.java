package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import theBalance.BalanceMod;
import theBalance.powers.ObservationNotesPower;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 观察笔记 - Observation Notes
// 每当你对敌人施加负面状态（Debuff），下一次攻击伤害提升 3 点
public class ObservationNotes extends CustomRelic {
    public static final String ID = BalanceMod.makeID("ObservationNotes");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("placeholder_relic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    public ObservationNotes() {
        super(ID, IMG, OUTLINE, RelicTier.COMMON, LandingSound.FLAT);
        counter = 0;
    }

    @Override
    public void atBattleStart() {
        counter = 0;
        flash();
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                new ObservationNotesPower(AbstractDungeon.player)));
    }

    @Override
    public int onAttackToChangeDamage(com.megacrit.cardcrawl.cards.DamageInfo info, int damageAmount) {
        if (info.owner == AbstractDungeon.player && counter > 0 &&
            info.type != com.megacrit.cardcrawl.cards.DamageInfo.DamageType.HP_LOSS &&
            info.type != com.megacrit.cardcrawl.cards.DamageInfo.DamageType.THORNS) {

            int bonus = counter;
            counter = 0; // Reset after use
            return damageAmount + bonus;
        }
        return damageAmount;
    }

    @Override
    public void onVictory() {
        counter = 0;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
