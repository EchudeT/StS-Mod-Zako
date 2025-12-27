package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class ZeroSumGame extends AbstractDynamicCard {
    public static final String ID = BalanceMod.makeID(ZeroSumGame.class.getSimpleName());
    public static final String IMG = makeCardPath("ZeroSumGame.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 0;
    private static final int MAGIC = 2;
    private static final int UPGRADE_PLUS_MAGIC = 1;

    public ZeroSumGame() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(m, p, new StrengthPower(m, -magicNumber), -magicNumber));
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new StrengthPower(p, magicNumber), magicNumber));

            // 回合结束归还
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new theBalance.powers.ZeroSumGamePower(p, m, magicNumber), magicNumber));
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
