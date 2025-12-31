package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 耍赖证明 - Cheating Proof
// 替代机制：回合开始获得1人工制品，回合结束失去1人工制品。
// 效果：相当于你在自己的回合内拥有一层"免疫权"，可以用来抵消自己卡牌的副作用。
public class CheatingProof extends CustomRelic {
    public static final String ID = BalanceMod.makeID("CheatingProof");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("CheatingProof.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    public CheatingProof() {
        super(ID, IMG, OUTLINE, RelicTier.RARE, LandingSound.MAGICAL);
    }

    // 回合开始：获得特权
    @Override
    public void atTurnStart() {
        flash();
        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                new ArtifactPower(AbstractDungeon.player, 1), 1));
    }

    // 回合结束：特权过期
    @Override
    public void onPlayerEndTurn() {
        // 只有当玩家身上还有人工制品时才减少
        if (AbstractDungeon.player.hasPower(ArtifactPower.POWER_ID)) {
            // 这里不闪烁遗物，因为这是一种惩罚/过期机制
            // 使用 ReducePowerAction 减少1层。如果只有1层，ReducePowerAction会自动移除它。
            addToBot(new ReducePowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    ArtifactPower.POWER_ID, 1));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}