package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.TheDefault;

import java.util.ArrayList;

import static theBalance.BalanceMod.makeCardPath;

public class EquivalentExchange extends AbstractDynamicCard {
    public static final String ID = BalanceMod.makeID(EquivalentExchange.class.getSimpleName());
    public static final String IMG = makeCardPath("EquivalentExchange.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = TheDefault.Enums.COLOR_GRAY;

    private static final int COST = 1;

    public EquivalentExchange() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ArrayList<AbstractCard> rareCards = new ArrayList<>();
        for (AbstractCard c : CardLibrary.getAllCards()) {
            if (c.rarity == CardRarity.RARE && c.color == COLOR) {
                rareCards.add(c);
            }
        }

        if (!rareCards.isEmpty()) {
            AbstractCard randomRare = rareCards.get(AbstractDungeon.cardRandomRng.random(rareCards.size() - 1)).makeCopy();
            randomRare.setCostForTurn(0);
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(randomRare));
        }
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
