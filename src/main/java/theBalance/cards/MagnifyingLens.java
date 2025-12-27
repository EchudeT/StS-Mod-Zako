package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class MagnifyingLens extends AbstractDynamicCard {
    public static final String ID = BalanceMod.makeID(MagnifyingLens.class.getSimpleName());
    public static final String IMG = makeCardPath("MagnifyingLens.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int MAGIC = 4;
    private static final int UPGRADE_PLUS_MAGIC = 2;
    private static final int MAGIC2 = 8;

    public MagnifyingLens() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
        baseDefaultSecondMagicNumber = defaultSecondMagicNumber = MAGIC2;
        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new StrengthPower(p, magicNumber), magicNumber));

        if (m != null) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(m, p, new StrengthPower(m, defaultSecondMagicNumber), defaultSecondMagicNumber));
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            initializeDescription();
        }
    }
}
