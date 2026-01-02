package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.BetterDrawPileToHandAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import java.util.ArrayList;

import static theBalance.BalanceMod.makeCardPath;

public class BadDebtRecovery extends AbstractDynamicCard {

    // 坏账回收 - Bad Debt Recovery
    // 从弃牌堆或抽牌堆选择 1 张牌加入手牌。将一张晕眩加入抽牌堆。

    public static final String ID = BalanceMod.makeID(BadDebtRecovery.class.getSimpleName());
    public static final String IMG = makeCardPath("BadDebtRecovery.png");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 0;

    public BadDebtRecovery() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        this.cardsToPreview = new Dazed();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 将晕眩加入抽牌堆 (副作用总是发生)
        addToBot(new MakeTempCardInDrawPileAction(new Dazed(), 1, true, true));

        // 2. 构建选项
        ArrayList<AbstractCard> choices = new ArrayList<>();

        // 选项A：弃牌堆有牌才显示
        if (!p.discardPile.isEmpty()) {
            choices.add(new ChooseFromDiscardCard());
        }

        // 选项B：抽牌堆有牌才显示
        if (!p.drawPile.isEmpty()) {
            choices.add(new ChooseFromDrawCard());
        }

        // 3. 执行逻辑
        if (choices.isEmpty()) {
            // 如果两个堆都没牌，只加晕眩，无事发生
            return;
        } else if (choices.size() == 1) {
            // 只有一种选择，直接执行，不弹窗
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    choices.get(0).use(p, m);
                    this.isDone = true;
                }
            });
        } else {
            // 弹窗二选一
            addToBot(new ChooseOneAction(choices));
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            this.isInnate = true;
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    // =================================================================================
    // 内部类：选项卡牌
    // =================================================================================

    // 选项1：从弃牌堆寻找
    private static class ChooseFromDiscardCard extends AbstractCard {
        public static final String ID = BadDebtRecovery.ID + "_Discard";
        // 复用主卡牌的图片，或者你可以做专门的图片
        public static final String IMG = makeCardPath("BadDebtRecovery.png");

        public ChooseFromDiscardCard() {
            super(ID, "弃牌堆", IMG, -2, "从弃牌堆选择 1 张牌加入手牌。",
                    CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {
            addToBot(new com.megacrit.cardcrawl.actions.common.BetterDiscardPileToHandAction(1));
        }

        @Override public void upgrade() {}
        @Override public AbstractCard makeCopy() { return new ChooseFromDiscardCard(); }
    }

    private static class ChooseFromDrawCard extends AbstractCard {
        public static final String ID = BadDebtRecovery.ID + "_Draw";
        public static final String IMG = makeCardPath("BadDebtRecovery.png");

        public ChooseFromDrawCard() {
            super(ID, "抽牌堆", IMG, -2, "从抽牌堆选择 1 张牌加入手牌。",
                    CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {
            addToBot(new BetterDrawPileToHandAction(1));
        }

        @Override public void upgrade() {}
        @Override public AbstractCard makeCopy() { return new ChooseFromDrawCard(); }
    }
}