package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.*;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 半透膜 - Semipermeable Membrane
// 每当你偷取（Steal）属性时，额外获得 2 点对应的临时属性
public class Semipermeable extends CustomRelic {
    public static final String ID = BalanceMod.makeID("Semipermeable");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("Semipermeable.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    private static final int BONUS_AMT = 2;

    public Semipermeable() {
        super(ID, IMG, OUTLINE, RelicTier.UNCOMMON, LandingSound.MAGICAL);
    }

    /**
     * 静态触发方法：供你的卡牌调用
     * @param powerID 被偷取属性的 ID (StrengthPower.POWER_ID 或 DexterityPower.POWER_ID)
     */
    public static void trigger(String powerID) {
        if (AbstractDungeon.player.hasRelic(ID)) {
            ((Semipermeable) AbstractDungeon.player.getRelic(ID)).onSteal(powerID);
        }
    }

    // 实际执行逻辑
    public void onSteal(String powerID) {
        flash();
        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));

        if (StrengthPower.POWER_ID.equals(powerID)) {
            // 获得 2 点力量
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    new StrengthPower(AbstractDungeon.player, BONUS_AMT), BONUS_AMT));
            // 获得 2 点失去力量 (回合结束自动扣除，实现"临时"效果)
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    new LoseStrengthPower(AbstractDungeon.player, BONUS_AMT), BONUS_AMT));
        }
        else if (DexterityPower.POWER_ID.equals(powerID)) {
            // 获得 2 点敏捷
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    new DexterityPower(AbstractDungeon.player, BONUS_AMT), BONUS_AMT));
            // 获得 2 点失去敏捷
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    new LoseDexterityPower(AbstractDungeon.player, BONUS_AMT), BONUS_AMT));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}