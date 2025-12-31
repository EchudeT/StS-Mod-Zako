package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageRandomEnemyAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 校准砝码 - Calibrated Weight
// 每当你失去力量，获 3 点格挡；每当你失去敏捷，对随机敌人造成 5 点伤害
public class CalibratedWeight extends CustomRelic {
    public static final String ID = BalanceMod.makeID("CalibratedWeight");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("CalibratedWeight.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    private static final int BLOCK_AMT = 3;
    private static final int DMG_AMT = 5;

    public CalibratedWeight() {
        super(ID, IMG, OUTLINE, RelicTier.UNCOMMON, LandingSound.HEAVY);
    }

    // --- 供 Patch 调用的触发方法 ---

    // 触发失去力量的效果
    public void onLoseStrength() {
        flash();
        addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, BLOCK_AMT));
    }

    // 触发失去敏捷的效果
    public void onLoseDexterity() {
        flash();
        // 对随机敌人造成伤害 (使用 THORNS 类型代表被动伤害，类似水银沙漏)
        addToBot(new DamageRandomEnemyAction(
                new DamageInfo(AbstractDungeon.player, DMG_AMT, DamageInfo.DamageType.THORNS),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY
        ));
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}