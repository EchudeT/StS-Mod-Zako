package theBalance.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import java.util.ArrayList;

import static theBalance.BalanceMod.makeCardPath;

public class AssetReorganization extends AbstractDynamicCard {

    // 资产重组 - Asset Reorganization
    // 将弃牌堆和抽牌堆互换。 (升级后：1费)

    public static final String ID = BalanceMod.makeID(AssetReorganization.class.getSimpleName());
    public static final String IMG = makeCardPath("AssetReorganization.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 2;

    public AssetReorganization() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 交换抽牌堆和弃牌堆
        ArrayList<AbstractCard> tempDrawPile = new ArrayList<>(p.drawPile.group);
        ArrayList<AbstractCard> tempDiscardPile = new ArrayList<>(p.discardPile.group);

        p.drawPile.clear();
        p.discardPile.clear();

        for (AbstractCard card : tempDiscardPile) {
            p.drawPile.addToTop(card);
        }

        for (AbstractCard card : tempDrawPile) {
            p.discardPile.addToTop(card);
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(1);
            initializeDescription();
        }
    }
}
