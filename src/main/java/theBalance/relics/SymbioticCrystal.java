package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;


public class SymbioticCrystal extends CustomRelic {
    public static final String ID = BalanceMod.makeID("SymbioticCrystal");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("SymbioticCrystal.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    private static final float HEAL_RATE = 0.2F;
    public SymbioticCrystal() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    // 玩家吸血逻辑
    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (damageAmount > 0 && target != AbstractDungeon.player && info.type == DamageInfo.DamageType.NORMAL) {
            flash();
            addToBot(new HealAction(AbstractDungeon.player, AbstractDungeon.player, (int)(damageAmount * HEAL_RATE)));
        }
    }

    // 敌人吸血逻辑
    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.owner instanceof AbstractMonster && damageAmount > 0) {
            flash();
            addToBot(new HealAction(info.owner, info.owner, (int)(damageAmount * HEAL_RATE)));
        }
        return damageAmount;
    }

    // 玩家格挡减半
    @Override
    public int onPlayerGainedBlock(float blockAmount) {
        return Math.round(blockAmount * 0.5F);
    }

    // 敌人格挡减半逻辑需要配合Power或者Patch，这里建议使用之前的 Power 方式，
    // 或者如果你想纯 Patch，也可以Patch AbstractCreature.addBlock。
    // 为了代码整洁，这里沿用之前建议的 atBattleStart 加 Power 的方式（上一个回答的代码），
    // 或者你可以选择不再遗物里写，而是统一在下面的Patch里写。

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
