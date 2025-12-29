package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class TurtleUp extends AbstractDynamicCard {

    // 龟缩 - Turtle Up
    // 获得 3(5) 敏捷，-4 力量。

    public static final String ID = BalanceMod.makeID(TurtleUp.class.getSimpleName());
    public static final String IMG = makeCardPath("TurtleUp.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int MAGIC = 3;  // 敏捷
    private static final int UPGRADE_PLUS_MAGIC = 2;
    private static final int MAGIC2 = 4;  // 失去力量

    public TurtleUp() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
        baseDefaultSecondMagicNumber = defaultSecondMagicNumber = MAGIC2;
        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new DexterityPower(p, magicNumber), magicNumber));

        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new StrengthPower(p, -defaultSecondMagicNumber), -defaultSecondMagicNumber));
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
