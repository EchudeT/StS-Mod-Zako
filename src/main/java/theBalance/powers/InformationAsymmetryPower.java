package theBalance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 资讯不对称 - Information Asymmetry
// 下回合少抽2张牌
public class InformationAsymmetryPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("InformationAsymmetryPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("SpecialPower84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("SpecialPower32.png"));

    public InformationAsymmetryPower(final AbstractCreature owner, final int drawReduction) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = drawReduction;
        type = PowerType.DEBUFF;
        isTurnBased = true;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public void onInitialApplication() {
        // 减少玩家的手牌上限来实现"下回合少抽牌"
        AbstractDungeon.player.gameHandSize -= this.amount;
    }

    @Override
    public void atStartOfTurnPostDraw() {
        flash();
        // 恢复手牌上限
        AbstractDungeon.player.gameHandSize += this.amount;
        // 移除此Power
        AbstractDungeon.actionManager.addToBottom(
            new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
    }

    @Override
    public void onRemove() {
        // 如果Power被提前移除，确保恢复手牌上限
        if (AbstractDungeon.player != null) {
            AbstractDungeon.player.gameHandSize += this.amount;
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new InformationAsymmetryPower(owner, amount);
    }
}
