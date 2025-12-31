package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.LoseDexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 手持小镜子 - Hand Mirror
// 修改版：回合开始时，获得等同于全场敌人最高力量值的临时敏捷。
public class HandMirror extends CustomRelic {
    public static final String ID = BalanceMod.makeID("HandMirror");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("HandMirror.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png")); // 记得改为正确的轮廓图

    public HandMirror() {
        super(ID, IMG, OUTLINE, RelicTier.UNCOMMON, LandingSound.MAGICAL); // 建议提升为罕见(Uncommon)
    }

    @Override
    public void atTurnStart() {
        int maxEnemyStr = 0;

        // 1. 遍历寻找敌人中最高的力量值
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped() && m.hasPower(StrengthPower.POWER_ID)) {
                int str = m.getPower(StrengthPower.POWER_ID).amount;
                if (str > maxEnemyStr) {
                    maxEnemyStr = str;
                }
            }
        }

        // 2. 如果存在力量，则获得等量的临时敏捷
        if (maxEnemyStr > 0) {
            flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));

            // 获得敏捷
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    new DexterityPower(AbstractDungeon.player, maxEnemyStr), maxEnemyStr));

            // 获得"回合结束失去敏捷" (实现临时效果)
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    new LoseDexterityPower(AbstractDungeon.player, maxEnemyStr), maxEnemyStr));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}