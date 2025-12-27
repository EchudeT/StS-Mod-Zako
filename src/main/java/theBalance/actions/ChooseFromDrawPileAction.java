package theBalance.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ChooseFromDrawPileAction extends AbstractGameAction {
    private int numberOfCards;

    public ChooseFromDrawPileAction(int numberOfCards) {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = this.startDuration = Settings.ACTION_DUR_FAST;
        this.numberOfCards = numberOfCards;
    }

    @Override
    public void update() {
        if (this.duration == this.startDuration) {
            if (AbstractDungeon.player.drawPile.isEmpty()) {
                this.isDone = true;
                return;
            }

            CardGroup tempGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
                tempGroup.addToTop(c);
            }

            AbstractDungeon.gridSelectScreen.open(
                tempGroup,
                this.numberOfCards,
                "选择一张牌加入手牌",
                false,
                false,
                false,
                false
            );
        } else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                AbstractDungeon.player.drawPile.removeCard(c);
                AbstractDungeon.player.hand.addToTop(c);
                AbstractDungeon.player.hand.refreshHandLayout();
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }

        tickDuration();
    }
}
