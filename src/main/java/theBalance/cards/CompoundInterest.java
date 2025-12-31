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
    // 每当你失去金币时（消费或被盗），获得等同于失去量 !M! % 的 #y格挡 。

    public static final String ID = BalanceMod.makeID(CompoundInterest.class.getSimpleName());
    public static final String IMG = makeCardPath("CompoundInterest.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.POWER;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1; // 降为1费，更灵活
    private static final int MAGIC = 50; // 转化率 50%
    private static final int UPGRADE_MAGIC = 30; // 升级后 80%


    public CompoundInterest() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p,
                        new theBalance.powers.CompoundInterestPower(p, magicNumber), magicNumber));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_MAGIC);
            initializeDescription();
        }
    }
}
