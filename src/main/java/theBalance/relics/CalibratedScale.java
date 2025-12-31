package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 校准天平 - Calibrated Scale
// 每回合中，第一次力量与敏捷相等时（不为0），获得 [E]
public class CalibratedScale extends CustomRelic {
    public static final String ID = BalanceMod.makeID("CalibratedScale");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("CalibratedScale.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    // 记录本回合是否已经触发过
    private boolean triggeredThisTurn = false;

    public CalibratedScale() {
        super(ID, IMG, OUTLINE, RelicTier.COMMON, LandingSound.MAGICAL);
    }

    @Override
    public void atTurnStart() {
        // 回合开始，重置状态
        triggeredThisTurn = false;
        this.grayscale = false; // 恢复颜色
    }

    // --- 核心逻辑检查函数，供Patch调用 ---
    public void checkBalance() {
        // 1. 如果已经触发过，直接跳过
        if (this.triggeredThisTurn) {
            return;
        }

        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) return;

        // 2. 获取当前力量和敏捷
        int str = 0;
        int dex = 0;

        if (p.hasPower(StrengthPower.POWER_ID)) {
            str = p.getPower(StrengthPower.POWER_ID).amount;
        }
        if (p.hasPower(DexterityPower.POWER_ID)) {
            dex = p.getPower(DexterityPower.POWER_ID).amount;
        }

        // 3. 判定条件：相等 且 不为0
        if (str != 0 && str == dex) {
            flash();
            this.triggeredThisTurn = true;
            this.grayscale = true; // 变灰提示已触发

            addToBot(new RelicAboveCreatureAction(p, this));
            addToBot(new GainEnergyAction(1));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}