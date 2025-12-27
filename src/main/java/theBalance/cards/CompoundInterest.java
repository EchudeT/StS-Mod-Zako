package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class CompoundInterest extends AbstractDynamicCard {

    // 复利加持 - Compound Interest
    // 每 150 金币在回合始提供 1 力量。每有 1 力，结束扣 20(10) 金币。

    public static final String ID = BalanceMod.makeID(CompoundInterest.class.getSimpleName());
    public static final String IMG = makeCardPath("CompoundInterest.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.POWER;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 2;
    private static final int MAGIC = 20;
    private static final int UPGRADE_REDUCE_COST = 10;

    public CompoundInterest() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new theBalance.powers.CompoundInterestPower(p, magicNumber), magicNumber));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(-UPGRADE_REDUCE_COST);
            initializeDescription();
        }
    }
}
