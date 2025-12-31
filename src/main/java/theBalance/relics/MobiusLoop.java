package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 循环结 (莫比乌斯环) - Mobius Loop
// 效果：获得 [E]。在你的回合结束时，将你的 力量 和 敏捷 中较高的一项降低至与较低的一项相等。
public class MobiusLoop extends CustomRelic {
    public static final String ID = BalanceMod.makeID("MobiusLoop");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("MobiusLoop.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    public MobiusLoop() {
        super(ID, IMG, OUTLINE, RelicTier.BOSS, LandingSound.MAGICAL);
    }

    // 1. 获得能量
    @Override
    public void onEquip() {
        AbstractDungeon.player.energy.energyMaster += 1;
    }

    @Override
    public void onUnequip() {
        AbstractDungeon.player.energy.energyMaster -= 1;
    }

    // 2. 回合结束时的强制削减
    @Override
    public void onPlayerEndTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        int str = 0;
        int dex = 0;

        // 获取当前层数
        if (p.hasPower(StrengthPower.POWER_ID)) {
            str = p.getPower(StrengthPower.POWER_ID).amount;
        }
        if (p.hasPower(DexterityPower.POWER_ID)) {
            dex = p.getPower(DexterityPower.POWER_ID).amount;
        }

        // 如果数值相等，无事发生（这是最好的情况）
        if (str == dex) {
            return;
        }

        flash();
        addToBot(new RelicAboveCreatureAction(p, this));

        // 逻辑：高削低
        // 注意：ApplyPowerAction传入负数即为减少
        if (str > dex) {
            // 力量太高，削减力量
            int reduceAmount = str - dex;
            addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, -reduceAmount), -reduceAmount));
        } else {
            // 敏捷太高，削减敏捷
            int reduceAmount = dex - str;
            addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, -reduceAmount), -reduceAmount));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}