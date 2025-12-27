package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class LifeLeverage extends AbstractDynamicCard {

    // 生命杠杆 - Life Leverage
    // 获得已损生命值的格挡。 (升级后：额外获得5格挡)

    public static final String ID = BalanceMod.makeID(LifeLeverage.class.getSimpleName());
    public static final String IMG = makeCardPath("LifeLeverage.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 2;
    private static final int MAGIC = 0;  // 额外格挡
    private static final int UPGRADE_PLUS_BLOCK = 5;

    public LifeLeverage() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int lostHP = p.maxHealth - p.currentHealth;
        int totalBlock = lostHP + magicNumber;

        AbstractDungeon.actionManager.addToBottom(
            new GainBlockAction(p, p, totalBlock));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_PLUS_BLOCK);
            initializeDescription();
        }
    }
}
