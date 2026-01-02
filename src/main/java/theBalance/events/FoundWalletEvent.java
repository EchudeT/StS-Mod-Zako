package theBalance.events;

import com.megacrit.cardcrawl.cards.curses.Shame;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import theBalance.BalanceMod;

import static theBalance.BalanceMod.makeEventPath;

public class FoundWalletEvent extends AbstractImageEvent {
    public static final String ID = BalanceMod.makeID("FoundWalletEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("FoundWalletEvent.jpg");

    private int screenNum = 0;
    private int goldGain = 175;
    private int goldCost = 30;

    public FoundWalletEvent() {
        super(eventStrings.NAME, DESCRIPTIONS[0], IMG);

        if (AbstractDungeon.ascensionLevel >= 15) {
            goldGain = 150;
        }

        // --- 修复重点开始 ---

        // 选项1: [私吞] 获得 X 金币。 获得 羞耻 。
        // JSON结构: OPTIONS[0] = "[私吞] 获得 #g", OPTIONS[1] = " 金币。 获得 #r羞耻 。"
        // 这里的逻辑应该是拼接，而不是 replace，因为 JSON 里根本没有写 "175" 这个死数字
        imageEventText.setDialogOption(OPTIONS[0] + goldGain + OPTIONS[1], new Shame());

        // 选项2: [踢飞] 失去 X 金币。 从牌组中 #g移除 一张牌。
        // JSON结构: OPTIONS[2] = "[踢飞] 失去 #r", OPTIONS[3] = " 金币。 从牌组中 #g移除 一张牌。"
        if (AbstractDungeon.player.gold >= goldCost) {
            imageEventText.setDialogOption(OPTIONS[2] + goldCost + OPTIONS[3]);
        } else {
            // 钱不够时: [锁定] 需要 X 金币。
            // JSON结构: OPTIONS[4] = "[锁定] 需要 #r", OPTIONS[5] = " 金币。"
            imageEventText.setDialogOption(OPTIONS[4] + goldCost + OPTIONS[5], true);
        }

        // 选项3: [离开] 离开。
        // JSON结构: OPTIONS[6] = "[离开] 离开。"
        imageEventText.setDialogOption(OPTIONS[6]);

        // --- 修复重点结束 ---
    }

    @Override
    protected void buttonEffect(int i) {
        switch (screenNum) {
            case 0:
                switch (i) {
                    case 0: // 私吞
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.gainGold(goldGain);
                        // 添加诅咒并展示特效
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Shame(), Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f));
                        endEvent();
                        break;
                    case 1: // 踢飞 (付费删牌)
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.player.loseGold(goldCost);
                        // 打开删牌界面
                        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck, 1, "选择一张牌移除", false, false, false, true);
                        break;
                    case 2: // 离开
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        endEvent();
                        break;
                }
                break;
            case 1:
                openMap();
                break;
        }
    }

    @Override
    public void update() {
        super.update();
        // 删牌界面的回调逻辑
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            com.megacrit.cardcrawl.cards.AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f));
            AbstractDungeon.player.masterDeck.removeCard(c);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            endEvent();
        }
    }

    private void endEvent() {
        this.imageEventText.clearAllDialogs();
        // 设置唯一的离开按钮
        this.imageEventText.setDialogOption(OPTIONS[6]);
        screenNum = 1;
    }
}