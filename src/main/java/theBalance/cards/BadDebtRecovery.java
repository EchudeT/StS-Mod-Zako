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

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 0;

    public BadDebtRecovery() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        this.cardsToPreview = new Dazed(); // 预览晕眩卡
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 将晕眩加入抽牌堆
        AbstractCard statusCard = new Dazed();
        AbstractDungeon.actionManager.addToBottom(
                new MakeTempCardInDrawPileAction(statusCard, 1, true, true));

        // 2. 构建选项逻辑
        ArrayList<AbstractCard> choices = new ArrayList<>();

        // 选项A：从弃牌堆选择 (只要弃牌堆有牌)
        if (!p.discardPile.isEmpty()) {
            AbstractCard discardChoice = new ChooseFromDiscardCard();
            choices.add(discardChoice);
        }

        // 选项B：从抽牌堆选择 (只要抽牌堆有牌)
        if (!p.drawPile.isEmpty()) {
            AbstractCard drawChoice = new ChooseFromDrawCard();
            choices.add(drawChoice);
        }

        // 3. 触发选择
        if (choices.size() > 1) {
            AbstractDungeon.actionManager.addToBottom(new ChooseOneAction(choices));
        }
        else if (choices.size() == 1) {
            // 如果只有一边有牌，直接触发那个效果，不再弹窗
            choices.get(0).use(p, m);
        }
    }

    // --- 内部类：选项卡牌 (用于 ChooseOneAction 的显示) ---

    // 选项1：从弃牌堆寻找
    private static class ChooseFromDiscardCard extends AbstractCard {
        public static final String ID = BalanceMod.makeID("ChooseFromDiscard");
        public static final String IMG = "images/1024/ui/missing.png";

        public ChooseFromDiscardCard() {
            super(ID, "从弃牌堆寻找", IMG,
                    -2, "从弃牌堆选择一张牌加入手牌。", CardType.SKILL, CardColor.COLORLESS,
                    CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {
            // 使用自定义的动作
            AbstractDungeon.actionManager.addToBottom(new PickFromDiscardAction(1));
        }

        @Override
        public void upgrade() {}
        @Override
        public AbstractCard makeCopy() { return new ChooseFromDiscardCard(); }
    }

    // 选项2：从抽牌堆寻找
    private static class ChooseFromDrawCard extends AbstractCard {
        public static final String ID = BalanceMod.makeID("ChooseFromDraw");
        public static final String IMG = "images/1024/ui/missing.png";

        public ChooseFromDrawCard() {
            super(ID, "从抽牌堆寻找", IMG,
                    -2, "从抽牌堆选择一张牌加入手牌。", CardType.SKILL, CardColor.COLORLESS,
                    CardRarity.SPECIAL, CardTarget.NONE);
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {
            // BetterDrawPileToHandAction 是原版存在的，可以直接用
            AbstractDungeon.actionManager.addToBottom(
                    new BetterDrawPileToHandAction(1));
        }

        @Override
        public void upgrade() {}
        @Override
        public AbstractCard makeCopy() { return new ChooseFromDrawCard(); }
    }

    // --- 自定义动作：从弃牌堆拿牌 (替换掉找不到的 DiscardPileToHandAction) ---
    public static class PickFromDiscardAction extends AbstractGameAction {
        private final AbstractPlayer p;

        public PickFromDiscardAction(int amount) {
            this.p = AbstractDungeon.player;
            this.setValues(this.p, AbstractDungeon.player, amount);
            this.actionType = ActionType.CARD_MANIPULATION;
            this.duration = Settings.ACTION_DUR_MED;
        }

        @Override
        public void update() {
            // 动作开始时
            if (this.duration == Settings.ACTION_DUR_MED) {
                if (this.p.discardPile.isEmpty()) {
                    this.isDone = true;
                    return;
                }
                // 打开选择界面：参数(目标牌组, 数量, 提示文本, 是否用于升级)
                AbstractDungeon.gridSelectScreen.open(this.p.discardPile, this.amount, "选择一张牌加入手牌", false);
                this.tickDuration();
                return;
            }

            // 玩家选完牌后
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    // 如果手牌没满，加入手牌
                    if (this.p.hand.size() < 10) { // 10 是 BaseMod.MAX_HAND_SIZE 的默认值
                        this.p.hand.addToHand(c);
                        this.p.discardPile.removeCard(c);
                    }
                    // 重置卡牌的高亮状态
                    c.lighten(false);
                    c.unhover();
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                this.p.hand.refreshHandLayout();

                // 确保弃牌堆重新排列
                for (AbstractCard c : this.p.discardPile.group) {
                    c.unhover();
                    c.target_x = CardGroup.DISCARD_PILE_X;
                    c.target_y = 0.0F;
                }

                this.isDone = true;
            }

            this.tickDuration();
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