package theBalance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.cards.AbstractCard; // 需要引入卡牌类
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 无限透支 - Infinite Overdraft
// 本回合手牌费用为0，下回合跳过
public class InfiniteOverdraftPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("InfiniteOverdraftPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("SpecialPower84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("SpecialPower32.png"));

    public InfiniteOverdraftPower(final AbstractCreature owner) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        type = PowerType.BUFF;
        isTurnBased = false;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    // 1. 能力生效瞬间：让当前手里的所有牌变成0费
    @Override
    public void onInitialApplication() {
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            c.setCostForTurn(0);
        }
    }

    // 2. 抽牌监听：本回合后续抽到的牌也变成0费
    @Override
    public void onCardDraw(AbstractCard card) {
        card.setCostForTurn(0);
    }

    // 3. 下回合开始时：强制结束回合（副作用）
    @Override
    public void atStartOfTurn() {
        AbstractDungeon.actionManager.addToBottom(new PressEndTurnButtonAction());
        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public AbstractPower makeCopy() {
        return new InfiniteOverdraftPower(owner);
    }
}