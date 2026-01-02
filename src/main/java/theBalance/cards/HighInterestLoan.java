package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;
import theBalance.powers.HighInterestLoanPower; // 引用对应的Power

import static theBalance.BalanceMod.makeCardPath;

public class HighInterestLoan extends AbstractDynamicCard {

    // High Interest Loan (保持不变)
    // 效果：每当你消耗战斗津贴时，抽 1 张牌。

    public static final String ID = BalanceMod.makeID(HighInterestLoan.class.getSimpleName());
    public static final String IMG = makeCardPath("HighInterestLoan.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.POWER;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int MAGIC = 1; // 抽牌数量

    public HighInterestLoan() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new HighInterestLoanPower(p, magicNumber), magicNumber));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(0); // 升级后 0 费，符合“本钱”变低的感觉
            initializeDescription();
        }
    }
}