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
        public static final String IMG = BadDebtRecovery.IMG;

        public ChooseFromDiscardCard() {
            super(ID, "弃牌堆", IMG, -2, "从弃牌堆选择 1 张牌加入手牌。",
                    CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);
            // 这里的文本建议放入 JSON (ID: "theBalance:BadDebtRecovery_Discard")
        }

        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {
            // 这里不需要 addToBottom，因为 ChooseOneAction 会自动将其加入队列
            // 但为了安全起见，通常还是 addToBot，或者直接在 Action 队列里执行
            // ChooseOneAction 的逻辑是：选中卡牌后，调用该卡牌的 use，所以这里必须 addToBot
            addToBot(new PickFromDiscardAction(1));
        }

        @Override public void upgrade() {}
        @Override public AbstractCard makeCopy() { return new ChooseFromDiscardCard(); }
    }

    // 选项2：从抽牌堆寻找
    private static class ChooseFromDrawCard extends AbstractCard {
        public static final String ID = BadDebtRecovery.ID + "_Draw";
        public static final String IMG = BadDebtRecovery.IMG;

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

    // =================================================================================
    // 自定义动作：修复后的从弃牌堆拿牌
    // =================================================================================
    public static class PickFromDiscardAction extends AbstractGameAction {
        private final AbstractPlayer p;

        public PickFromDiscardAction(int amount) {
            this.p = AbstractDungeon.player;
            this.setValues(this.p, AbstractDungeon.player, amount);
            this.actionType = ActionType.CARD_MANIPULATION;
            this.duration = Settings.ACTION_DUR_FASTER; // 使用较快的动画时间
        }

        @Override
        public void update() {
            // 1. 如果弃牌堆空了，直接结束
            if (this.p.discardPile.isEmpty()) {
                this.isDone = true;
                return;
            }

            // 2. 动作开始时，打开选择界面
            if (this.duration == Settings.ACTION_DUR_FASTER) {
                // 如果弃牌堆数量正好等于或少于要拿的数量，直接全部拿走，不弹窗
                if (this.p.discardPile.size() <= this.amount) {
                    for (AbstractCard c : this.p.discardPile.group) {
                        if (this.p.hand.size() < 10) {
                            this.p.hand.addToHand(c);
                            c.lighten(false);
                        }
                        this.p.discardPile.removeCard(c);
                    }
                    this.p.hand.refreshHandLayout();
                    this.isDone = true;
                    return;
                }

                // 否则打开网格选择界面
                // 参数：(目标CardGroup, 数量, 标题文本, 是否是"任意数量")
                AbstractDungeon.gridSelectScreen.open(this.p.discardPile, this.amount, "选择加入手牌", false);

                // ★ 关键修改：这里调用 tickDuration 会导致 duration 减少，
                // 但我们必须等待玩家选完，所以这里仅仅减少一点点或者干脆不调用 tickDuration 直到选完
                // 为了逻辑清晰，我们这里只 tick 一次让它不要卡死，但通过 return 暂停 update 直到选择完成
                this.tickDuration();
                return;
            }

            // 3. 等待玩家选择
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    // 加入手牌逻辑
                    if (this.p.hand.size() < 10) {
                        this.p.hand.addToHand(c);
                    } else {
                        // 如果手牌满了，把选中的牌放回弃牌堆（或者你可以改为放回抽牌堆，看设计）
                        this.p.discardPile.addToTop(c);
                        this.p.createHandIsFullDialog();
                    }

                    // 从弃牌堆逻辑移除（因为 gridSelectScreen 只是选中，没从原 Group 移出）
                    this.p.discardPile.removeCard(c);

                    c.lighten(false);
                    c.unhover();
                    c.applyPowers(); // 刷新数值
                }

                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                this.p.hand.refreshHandLayout();

                // 强制刷新弃牌堆位置，防止视觉残留
                for (AbstractCard c : this.p.discardPile.group) {
                    c.unhover();
                    c.target_x = CardGroup.DISCARD_PILE_X;
                    c.target_y = 0.0F;
                }

                this.isDone = true;
            }

            // 如果 duration 耗尽还没选（不太可能发生，因为打开界面时游戏会暂停逻辑更新），这里兜底
            // 但在 Action 中，只要还在打开 GridScreen，通常不会走到这里
            // this.tickDuration();
        }
    }
}