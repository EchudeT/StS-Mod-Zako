package theBalance.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Shame;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import theBalance.BalanceMod;
import theBalance.cards.Lending;
import theBalance.cards.PriceOfHubris;
import theBalance.relics.BlackHeart;
import theBalance.relics.BlankCard;
import theBalance.relics.SymbioticCrystal;

import static theBalance.BalanceMod.makeEventPath;

public class TheForkEvent extends AbstractImageEvent {

    public static final String ID = BalanceMod.makeID("TheForkEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("forkEvent.png");

    private int screenNum = 0;
    private int goldCost = 0;

    public TheForkEvent() {
        super(NAME, DESCRIPTIONS[0], IMG); // 显示第一段描述（登场）

        this.goldCost = (int)(AbstractDungeon.player.gold * 0.10f);

        // === 初始界面只有 [继续] ===
        // OPTIONS[7] 对应 json 里的 "[继续]"
        imageEventText.setDialogOption(OPTIONS[7]);
    }

    @Override
    protected void buttonEffect(int i) {
        switch (screenNum) {

            // === 状态 0: 介绍页 ===
            // 玩家点击了 [继续]
            case 0:
                this.screenNum = 1;
                // 更新文本为第二段（对话）
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);

                // 清除之前的 [继续] 按钮
                this.imageEventText.clearAllDialogs();

                // 设置真正的选择项
                AbstractRelic r1 = new BlackHeart();
                imageEventText.setDialogOption(OPTIONS[0] + r1.name, r1);

                AbstractRelic r2 = new BlankCard();
                AbstractCard c2 = new Lending();
                imageEventText.setDialogOption(OPTIONS[1] + r2.name + OPTIONS[8] + c2.name, c2 , r2);

                AbstractRelic r3 = new SymbioticCrystal();
                imageEventText.setDialogOption(OPTIONS[2] + r3.name + OPTIONS[9], r3);

                imageEventText.setDialogOption(OPTIONS[10]);

                if (this.goldCost > 0) {
                    imageEventText.setDialogOption(OPTIONS[3] + this.goldCost + OPTIONS[4]);
                } else {
                    imageEventText.setDialogOption(OPTIONS[5]);
                }
                break;

            // === 状态 1: 选择页 ===
            case 1:
                switch (i) {
                    case 0: // 选了 黑色的心
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractRelic r_heart = new BlackHeart();
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), r_heart);
                        endEvent();
                        break;

                    case 1: // 选了 空白副卡
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        AbstractRelic r_card = new BlankCard();
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), r_card);
                        endEvent();
                        break;

                    case 2: // 选了 共生红水晶
                        AbstractDungeon.player.decreaseMaxHealth(20);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        AbstractRelic r_crystal = new SymbioticCrystal();
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), r_crystal);
                        endEvent();
                        break;

                    case 3: // [全部都要]
                        // 1. 扣除 20 最大生命值
                        AbstractDungeon.player.decreaseMaxHealth(20);

                        AbstractDungeon.player.loseGold(9999);

                        // 2. 获得诅咒：羞耻 (或者你可以换成 Pain / Writhe)
                        // AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Shame(), Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f));

                        // 3. 获得所有三个遗物
                        // 注意：连续获取遗物最好稍微错开一点逻辑，或者直接依次生成，游戏会自动排队
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f, new BlackHeart());
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f, new BlankCard());
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f, new SymbioticCrystal());

                        // 更新文本为新的第6段
                        this.imageEventText.updateBodyText(DESCRIPTIONS[6]);

                        this.screenNum = 3; // 设置为新的状态
                        this.imageEventText.updateBodyText(DESCRIPTIONS[6]); // 更新为"获得了一切...但还有一张卡"
                        this.imageEventText.clearAllDialogs();

                        // 添加选卡按钮 (OPTIONS[11] = "[拿走] 获得 ", OPTIONS[12] = "[跳过]...")
                        AbstractCard hubrisCard = new PriceOfHubris();
                        this.imageEventText.setDialogOption(OPTIONS[11] + hubrisCard.name, hubrisCard);
                        this.imageEventText.setDialogOption(OPTIONS[12]);

                        break;

                    case 4: // 拒绝
                        this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                        if (this.goldCost > 0) {
                            AbstractDungeon.player.loseGold(this.goldCost);
                        }
                        CardCrawlGame.sound.play("VO_GREMLINNOB_1A");
                        endEvent();
                        break;
                }
                break;
            case 3:
                switch (i) {
                    case 0: // [拿走] 傲慢之价
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new PriceOfHubris(), Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[7]); // "你收下了..."
                        break;

                    case 1: // [跳过]
                        this.imageEventText.updateBodyText(DESCRIPTIONS[8]); // "你无视了..."
                        break;
                }
                endEvent(); // 无论拿没拿，都进入离开流程
                break;

            // === 状态 2: 结果页 ===
            case 2:
                openMap();
                break;
        }
    }

    private void endEvent() {
        this.imageEventText.clearAllDialogs();
        this.imageEventText.setDialogOption(OPTIONS[6]); // [离开]
        this.screenNum = 2;
    }

}