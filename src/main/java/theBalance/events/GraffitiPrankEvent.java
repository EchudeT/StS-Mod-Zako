package theBalance.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import theBalance.BalanceMod;

import static theBalance.BalanceMod.makeEventPath;

public class GraffitiPrankEvent extends AbstractImageEvent {
    public static final String ID = BalanceMod.makeID("GraffitiPrankEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("GraffitiPrankEvent.jpg");

    private int screenNum = 0;

    public GraffitiPrankEvent() {
        super(eventStrings.NAME, DESCRIPTIONS[0], IMG);
        // 选项1: 失败 (扣血变化2张)
        imageEventText.setDialogOption(OPTIONS[0]);
        // 选项2: 成功 (无伤变化1张)
        imageEventText.setDialogOption(OPTIONS[1]);
        // 选项3: 离开
        imageEventText.setDialogOption(OPTIONS[2]);
    }

    @Override
    protected void buttonEffect(int i) {
        switch (screenNum) {
            case 0:
                switch (i) {
                    case 0: // 失败
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.damage(new com.megacrit.cardcrawl.cards.DamageInfo(null, 8, com.megacrit.cardcrawl.cards.DamageInfo.DamageType.HP_LOSS));
                        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck, 2, "选择2张牌进行变化", false, false, false, false);
                        break;
                    case 1: // 成功
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck, 1, "选择1张牌进行变化", false, false, false, false);
                        break;
                    case 2: // 离开
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        finishEvent();
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
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            // 执行变化逻辑 (Transform)
            float displayCount = 0.0F;
            for (com.megacrit.cardcrawl.cards.AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                AbstractDungeon.player.masterDeck.removeCard(c);
                AbstractDungeon.transformCard(c, false, AbstractDungeon.miscRng);

                // 显示特效
                com.megacrit.cardcrawl.cards.AbstractCard transCard = AbstractDungeon.getTransformedCard();
                AbstractDungeon.effectList.add(new com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect(transCard, com.megacrit.cardcrawl.core.Settings.WIDTH / 2.0F + displayCount, com.megacrit.cardcrawl.core.Settings.HEIGHT / 2.0F));
                displayCount += com.megacrit.cardcrawl.core.Settings.WIDTH / 5.0F; // 稍微错开显示
            }

            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            finishEvent();
        }
    }

    private void finishEvent() {
        this.imageEventText.clearAllDialogs();
        this.imageEventText.setDialogOption(OPTIONS[2]);
        screenNum = 1;
    }
}