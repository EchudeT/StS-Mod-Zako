package theBalance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 无限膨胀追踪 - Infinite Expansion Tracking
public class InfiniteExpansionPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("InfiniteExpansionPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));

    public InfiniteExpansionPower(final AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        type = PowerType.BUFF;
        isTurnBased = false;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    // 为了兼容旧的构造函数调用
    public InfiniteExpansionPower(final AbstractCreature owner) {
        this(owner, 0);
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    // 注意：这里移除了 atStartOfTurn，因为层数增加由卡牌的 onRetained 控制
    // 这样只有你手里真的有这张卡并保留了，层数才会涨

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        // 当受到未被格挡的攻击伤害时，重置倍率
        // 注意：如果你希望只要被攻击（哪怕被格挡）就重置，把 damageAmount > 0 去掉即可
        if (damageAmount > 0 && info.type != DamageInfo.DamageType.HP_LOSS) {
            if (this.amount > 0) {
                flash();
                this.amount = 0;
                updateDescription();
                // 关键：重置后立刻刷新手牌显示的数值
                updateHandText();
            }
        }
        return damageAmount;
    }

    // 辅助方法：刷新手牌数值
    private void updateHandText() {
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c instanceof theBalance.cards.InfiniteExpansion) {
                c.applyPowers();
            }
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new InfiniteExpansionPower(owner, amount);
    }
}