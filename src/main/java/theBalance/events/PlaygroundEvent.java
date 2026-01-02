package theBalance.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.OddlySmoothStone;
import com.megacrit.cardcrawl.relics.Vajra;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import theBalance.BalanceMod;

import static theBalance.BalanceMod.makeEventPath;

public class PlaygroundEvent extends AbstractImageEvent {
    public static final String ID = BalanceMod.makeID("PlaygroundEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    public static final String IMG = makeEventPath("PlaygroundEvent.jpg");

    private int screenNum = 0;
    private int maxHpLoss = 6;

    // 新增变量，记录是否已经拥有对应遗物
    private boolean hasStone;
    private boolean hasVajra;
    private int goldReward = 100; // 替代奖励的金额

    public PlaygroundEvent() {
        super(eventStrings.NAME, DESCRIPTIONS[0], IMG);

        if (AbstractDungeon.ascensionLevel >= 15) {
            maxHpLoss = 8;
        }

        // 检查玩家是否已有遗物
        hasStone = AbstractDungeon.player.hasRelic(OddlySmoothStone.ID);
        hasVajra = AbstractDungeon.player.hasRelic(Vajra.ID);

        // 选项1: 荡秋千
        if (hasStone) {
            // 如果有石头，显示获得金币的文本，且不显示遗物预览
            // 文本拼接："[荡秋千] 失去 #r" + 6 + " 点最大生命。 获得 #y100 金币。"
            imageEventText.setDialogOption(OPTIONS[0] + maxHpLoss + OPTIONS[5]);
        } else {
            // 正常流程，显示遗物预览
            imageEventText.setDialogOption(OPTIONS[0] + maxHpLoss + OPTIONS[1], new OddlySmoothStone());
        }

        // 选项2: 打沙袋
        if (hasVajra) {
            // 如果有金刚杵，显示获得金币文本
            imageEventText.setDialogOption(OPTIONS[2] + maxHpLoss + OPTIONS[5]);
        } else {
            imageEventText.setDialogOption(OPTIONS[2] + maxHpLoss + OPTIONS[3], new Vajra());
        }

        // 选项3: 醒来
        imageEventText.setDialogOption(OPTIONS[4]);
    }

    @Override
    protected void buttonEffect(int i) {
        switch (screenNum) {
            case 0:
                switch (i) {
                    case 0: // 荡秋千
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.decreaseMaxHealth(maxHpLoss);

                        if (hasStone) {
                            // 替代奖励：金币
                            AbstractDungeon.effectList.add(new RainingGoldEffect(this.goldReward));
                            AbstractDungeon.player.gainGold(this.goldReward);
                        } else {
                            // 原奖励：遗物
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f, new OddlySmoothStone());
                        }

                        CardCrawlGame.screenShake.shake(com.megacrit.cardcrawl.helpers.ScreenShake.ShakeIntensity.MED, com.megacrit.cardcrawl.helpers.ScreenShake.ShakeDur.SHORT, false);
                        break;

                    case 1: // 打沙袋
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.player.decreaseMaxHealth(maxHpLoss);

                        if (hasVajra) {
                            // 替代奖励：金币
                            AbstractDungeon.effectList.add(new RainingGoldEffect(this.goldReward));
                            AbstractDungeon.player.gainGold(this.goldReward);
                        } else {
                            // 原奖励：遗物
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f, new Vajra());
                        }

                        CardCrawlGame.sound.play("BLUNT_HEAVY");
                        break;

                    case 2: // 醒来
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        break;
                }
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[4]);
                screenNum = 1;
                break;
            case 1:
                openMap();
                break;
        }
    }
}