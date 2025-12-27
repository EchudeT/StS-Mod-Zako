package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 循环结 (莫比乌斯环) - Mobius Loop
// 替换初始遗物。力敏相等时，所有卡牌数值（伤害/防）提升 25%。失去能量奖励
public class MobiusLoop extends CustomRelic {
    public static final String ID = BalanceMod.makeID("MobiusLoop");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("placeholder_relic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    private static final float BOOST_MULTIPLIER = 1.25f;

    public MobiusLoop() {
        super(ID, IMG, OUTLINE, RelicTier.BOSS, LandingSound.MAGICAL);
    }

    private boolean isBalanced() {
        int str = 0;
        int dex = 0;

        if (AbstractDungeon.player.hasPower(StrengthPower.POWER_ID)) {
            str = AbstractDungeon.player.getPower(StrengthPower.POWER_ID).amount;
        }
        if (AbstractDungeon.player.hasPower(DexterityPower.POWER_ID)) {
            dex = AbstractDungeon.player.getPower(DexterityPower.POWER_ID).amount;
        }

        return str == dex;
    }

    @Override
    public float atDamageModify(float damage, AbstractCard card) {
        if (isBalanced() && card != null) {
            return damage * BOOST_MULTIPLIER;
        }
        return damage;
    }

    @Override
    public int onAttackToChangeDamage(DamageInfo info, int damageAmount) {
        if (isBalanced() && info.owner == AbstractDungeon.player) {
            return (int) (damageAmount * BOOST_MULTIPLIER);
        }
        return damageAmount;
    }

    @Override
    public int onPlayerGainedBlock(float blockAmount) {
        if (isBalanced()) {
            return (int) (blockAmount * BOOST_MULTIPLIER);
        }
        return (int) blockAmount;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
