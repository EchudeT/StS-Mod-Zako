package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class InfiniteOverdraft extends AbstractDynamicCard {

    // 无限透支 - Infinite Overdraft
    // 消耗。本回合费用为 0。下回合跳过。 (升级：抽2牌)

    public static final String ID = BalanceMod.makeID(InfiniteOverdraft.class.getSimpleName());
    public static final String IMG = makeCardPath("InfiniteOverdraft.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 3;
    private static final int MAGIC = 0;  // 抽牌数
    private static final int UPGRADE_DRAW = 2;

    public InfiniteOverdraft() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 应用无限透支Power（本回合费用为0，下回合跳过）
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new theBalance.powers.InfiniteOverdraftPower(p), -1));

        if (upgraded) {
            AbstractDungeon.actionManager.addToBottom(
                new DrawCardAction(p, UPGRADE_DRAW));
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_DRAW);
            this.rawDescription += " NL 抽2张牌。";
            initializeDescription();
        }
    }
}
