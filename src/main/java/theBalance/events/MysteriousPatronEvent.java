package theBalance.events;

import com.megacrit.cardcrawl.cards.curses.Shame;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import theBalance.BalanceMod;

import static theBalance.BalanceMod.makeEventPath;

public class MysteriousPatronEvent extends AbstractImageEvent {
    public static final String ID = BalanceMod.makeID("MysteriousPatronEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("IdentityCrisisEvent.png"); // 需要一张大叔的剪影图

    private int screenNum = 0;

    // 奖励数值
    private static final int GOLD_GAIN = 150;
    private static final int HP_LOSS_PERCENT = 15;

    public MysteriousPatronEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        // 选项 1: 撒娇
        imageEventText.setDialogOption(OPTIONS[0] + GOLD_GAIN + OPTIONS[1], new Shame());

        // 选项 2: 勒索
        int hpLoss = (int)(AbstractDungeon.player.maxHealth * (HP_LOSS_PERCENT / 100.0f));
        imageEventText.setDialogOption(OPTIONS[2] + hpLoss + OPTIONS[3]);

        // 选项 3: 离开
        imageEventText.setDialogOption(OPTIONS[4]);
    }

    @Override
    protected void buttonEffect(int i) {
        switch (screenNum) {
            case 0:
                switch (i) {
                    case 0: // 撒娇
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.gainGold(GOLD_GAIN);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Shame(), Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f));
                        break;

                    case 1: // 勒索
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        // 扣血
                        int hpLoss = (int)(AbstractDungeon.player.maxHealth * (HP_LOSS_PERCENT / 100.0f));
                        AbstractDungeon.player.damage(new com.megacrit.cardcrawl.cards.DamageInfo(null, hpLoss, com.megacrit.cardcrawl.cards.DamageInfo.DamageType.HP_LOSS));
                        // 获得随机稀有遗物
                        AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.RARE);
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), r);
                        break;

                    case 2: // 离开
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        break;
                }

                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[5]); // [离开]
                screenNum = 1;
                break;

            case 1:
                openMap();
                break;
        }
    }
}