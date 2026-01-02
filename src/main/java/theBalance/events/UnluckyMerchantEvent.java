package theBalance.events;

import com.megacrit.cardcrawl.cards.curses.Shame;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import theBalance.BalanceMod;

import static theBalance.BalanceMod.makeEventPath;

public class UnluckyMerchantEvent extends AbstractImageEvent {
    public static final String ID = BalanceMod.makeID("UnluckyMerchantEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("UnluckyMerchantEvent.jpg");

    private int screenNum = 0;
    private int goldGain = 120;
    private int hpLoss = 0;

    public UnluckyMerchantEvent() {
        super(eventStrings.NAME, DESCRIPTIONS[0], IMG);

        // 根据进阶等级调整数值
        if (AbstractDungeon.ascensionLevel >= 15) {
            goldGain = 100;
            hpLoss = (int)(AbstractDungeon.player.maxHealth * 0.15f); // 15% HP
        } else {
            goldGain = 120;
            hpLoss = (int)(AbstractDungeon.player.maxHealth * 0.10f); // 10% HP
        }

        // 选项1: 勒索金钱 (给羞耻)
        imageEventText.setDialogOption(OPTIONS[0] + goldGain + OPTIONS[1], new Shame());
        // 选项2: 抢劫货物 (扣血拿罕见遗物)
        imageEventText.setDialogOption(OPTIONS[2] + hpLoss + OPTIONS[3]);
        // 选项3: 收保护费 (拿药水)
        imageEventText.setDialogOption(OPTIONS[4]);
        // 选项4: 离开
        imageEventText.setDialogOption(OPTIONS[5]);
    }

    @Override
    protected void buttonEffect(int i) {
        switch (screenNum) {
            case 0:
                switch (i) {
                    case 0: // 勒索
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.gainGold(goldGain);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Shame(), Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f));
                        break;
                    case 1: // 抢劫 (由于地精反抗，扣血)
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.player.damage(new com.megacrit.cardcrawl.cards.DamageInfo(null, hpLoss, com.megacrit.cardcrawl.cards.DamageInfo.DamageType.HP_LOSS));
                        AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.UNCOMMON);
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), r);
                        break;
                    case 2: // 药水
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(AbstractDungeon.returnRandomPotion()));
                        AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(AbstractDungeon.returnRandomPotion()));
                        AbstractDungeon.combatRewardScreen.open();
                        break;
                    case 3: // 离开
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
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