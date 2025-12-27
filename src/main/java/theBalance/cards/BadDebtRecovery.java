package theBalance.cards;

import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.TheDefault;

import java.util.ArrayList;

import static theBalance.BalanceMod.makeCardPath;

public class BadDebtRecovery extends AbstractDynamicCard {

    // 坏账回收 - Bad Debt Recovery
    // 从弃牌堆或抽牌堆选择 1 张牌加入手牌。将一张虚空加入抽牌堆。

    public static final String ID = BalanceMod.makeID(BadDebtRecovery.class.getSimpleName());
    public static final String IMG = makeCardPath("BadDebtRecovery.png");

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = TheDefault.Enums.COLOR_GRAY;

    private static final int COST = 0;

    public BadDebtRecovery() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 添加虚空卡到抽牌堆
        AbstractCard voidCard = new VoidCard();
        AbstractDungeon.actionManager.addToBottom(
            new com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction(voidCard, 1, true, true));

        // 从弃牌堆或抽牌堆选择一张卡
        if (!p.discardPile.isEmpty() || !p.drawPile.isEmpty()) {
            ArrayList<AbstractCard> choices = new ArrayList<>();

            // 添加选择：从弃牌堆选择
            if (!p.discardPile.isEmpty()) {
                AbstractCard discardChoice = new ChooseFromDiscardCard();
                choices.add(discardChoice);
            }

            // 添加选择：从抽牌堆选择
            if (!p.drawPile.isEmpty()) {
                AbstractCard drawChoice = new ChooseFromDrawCard();
                choices.add(drawChoice);
            }

            if (!choices.isEmpty()) {
                AbstractDungeon.actionManager.addToBottom(new ChooseOneAction(choices));
            }
        }
    }

    // 内部类：从弃牌堆选择
    private static class ChooseFromDiscardCard extends AbstractCard {
        public ChooseFromDiscardCard() {
            super("ChooseFromDiscard", "从弃牌堆选择", "images/1024/ui/missing.png",
                  -2, "从弃牌堆选择一张牌加入手牌。", CardType.SKILL, CardColor.COLORLESS,
                  CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {
            AbstractDungeon.actionManager.addToBottom(
                new com.megacrit.cardcrawl.actions.unique.ExhumeAction(false));
        }

        @Override
        public void upgrade() {}

        @Override
        public AbstractCard makeCopy() {
            return new ChooseFromDiscardCard();
        }
    }

    // 内部类：从抽牌堆选择
    private static class ChooseFromDrawCard extends AbstractCard {
        public ChooseFromDrawCard() {
            super("ChooseFromDraw", "从抽牌堆选择", "images/1024/ui/missing.png",
                  -2, "从抽牌堆选择一张牌加入手牌。", CardType.SKILL, CardColor.COLORLESS,
                  CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {
            AbstractDungeon.actionManager.addToBottom(
                new theBalance.actions.ChooseFromDrawPileAction(1));
        }

        @Override
        public void upgrade() {}

        @Override
        public AbstractCard makeCopy() {
            return new ChooseFromDrawCard();
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            this.isInnate = true;
            this.rawDescription = CardCrawlGame.languagePack.getCardStrings(ID).UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}
