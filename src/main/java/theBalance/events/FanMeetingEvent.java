package theBalance.events;

import com.megacrit.cardcrawl.cards.colorless.RitualDagger;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import theBalance.BalanceMod;

import static theBalance.BalanceMod.makeEventPath;

public class FanMeetingEvent extends AbstractImageEvent {
    public static final String ID = BalanceMod.makeID("FanMeetingEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("FanMeetingEvent.jpg");

    private int screenNum = 0;
    private int goldGain = 99;
    private int healAmt = 20;
    private int selfDamage = 6;

    public FanMeetingEvent() {
        super(eventStrings.NAME, DESCRIPTIONS[0], IMG);

        // 选项1: 金币
        imageEventText.setDialogOption(OPTIONS[0] + goldGain + OPTIONS[1]);
        // 选项2: 回血
        imageEventText.setDialogOption(OPTIONS[2] + healAmt + OPTIONS[3]);
        // 选项3: 仪式匕首
        imageEventText.setDialogOption(OPTIONS[4] + selfDamage + OPTIONS[5], new RitualDagger());
        // 选项4: 离开
        imageEventText.setDialogOption(OPTIONS[6]);
    }

    @Override
    protected void buttonEffect(int i) {
        switch (screenNum) {
            case 0:
                switch (i) {
                    case 0: // 金币
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.gainGold(goldGain);
                        break;
                    case 1: // 回血
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.player.heal(healAmt);
                        break;
                    case 2: // 匕首
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        AbstractDungeon.player.damage(new com.megacrit.cardcrawl.cards.DamageInfo(null, selfDamage, com.megacrit.cardcrawl.cards.DamageInfo.DamageType.HP_LOSS));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new RitualDagger(), Settings.WIDTH / 2.0f - 200, Settings.HEIGHT / 2.0f));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new RitualDagger(), Settings.WIDTH / 2.0f + 200, Settings.HEIGHT / 2.0f));
                        break;
                    case 3: // 离开
                        // 直接离开，不用更新文本
                        openMap();
                        return;
                }

                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[6]); // 离开
                screenNum = 1;
                break;

            case 1:
                openMap();
                break;
        }
    }
}