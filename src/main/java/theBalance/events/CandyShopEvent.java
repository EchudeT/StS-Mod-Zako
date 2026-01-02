package theBalance.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import theBalance.BalanceMod;

import static theBalance.BalanceMod.makeEventPath;

public class CandyShopEvent extends AbstractImageEvent {
    public static final String ID = BalanceMod.makeID("CandyShopEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    public static final String IMG = makeEventPath("CandyShopEvent.jpg");

    private int screenNum = 0;

    // 定义数值变量
    private int hpLoss = 16;
    private int maxHpGain = 8;
    private int goldCost = 30;

    public CandyShopEvent() {
        super(eventStrings.NAME, eventStrings.DESCRIPTIONS[0], IMG);

        // 进阶 15+ 难度调整 (举例：更贵的糖果)
        if (AbstractDungeon.ascensionLevel >= 15) {
            goldCost = 40; // 只要改了这里，下面的文本会自动变，不需要改 JSON
            hpLoss = 20;
        }

        // 选项1: [吃辣糖] 失去 {0} HP, 获得 {1} MaxHP
        // 这里的 {0} 会被 hpLoss 替换，{1} 会被 maxHpGain 替换
        imageEventText.setDialogOption(String.format(eventStrings.OPTIONS[0], hpLoss, maxHpGain));

        // 选项2: [买汽水] 失去 {0} 金币
        if (AbstractDungeon.player.gold >= goldCost) {
            imageEventText.setDialogOption(String.format(eventStrings.OPTIONS[1], goldCost));
        } else {
            // 选项3: [锁定] 需要 {0} 金币
            // 这里的 true 表示按钮禁用(灰色)
            imageEventText.setDialogOption(String.format(eventStrings.OPTIONS[3], goldCost), true);
        }

        // 选项3: [离开] (没有数字，直接用)
        imageEventText.setDialogOption(eventStrings.OPTIONS[2]);
    }

    @Override
    protected void buttonEffect(int i) {
        switch (screenNum) {
            case 0:
                switch (i) {
                    case 0: // 吃辣糖
                        this.imageEventText.updateBodyText(eventStrings.DESCRIPTIONS[1]);
                        AbstractDungeon.player.damage(new com.megacrit.cardcrawl.cards.DamageInfo(null, hpLoss, com.megacrit.cardcrawl.cards.DamageInfo.DamageType.HP_LOSS));
                        AbstractDungeon.player.increaseMaxHp(maxHpGain, true);
                        break;
                    case 1: // 买汽水
                        this.imageEventText.updateBodyText(eventStrings.DESCRIPTIONS[2]);
                        AbstractDungeon.player.loseGold(goldCost);
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(AbstractDungeon.returnRandomPotion()));
                        AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(AbstractDungeon.returnRandomPotion()));
                        AbstractDungeon.combatRewardScreen.open();
                        break;
                    case 2: // 离开
                        this.imageEventText.updateBodyText(eventStrings.DESCRIPTIONS[3]);
                        break;
                }
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(eventStrings.OPTIONS[2]);
                screenNum = 1;
                break;
            case 1:
                openMap();
                break;
        }
    }
}