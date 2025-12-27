package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.TheDefault;

import static theBalance.BalanceMod.makeCardPath;

public class Countercurrent extends AbstractDynamicCard {

    // 逆流 - Countercurrent
    // 将自身负面属性转为正面。下回合敌人获双倍该属性。 (升级后：0费)

    public static final String ID = BalanceMod.makeID(Countercurrent.class.getSimpleName());
    public static final String IMG = makeCardPath("Countercurrent.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = TheDefault.Enums.COLOR_GRAY;

    private static final int COST = 1;

    public Countercurrent() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 应用逆流Power：将负面效果转为正面，下回合敌人获得双倍
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new theBalance.powers.CountercurrentPower(p), -1));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(0);
            initializeDescription();
        }
    }
}
