package theBalance.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Doubt;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import theBalance.BalanceMod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static theBalance.BalanceMod.makeEventPath;

public class StrictTutorEvent extends AbstractImageEvent {
    public static final String ID = BalanceMod.makeID("StrictTutorEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("StrictTutorEvent.jpg");

    private int screenNum = 0;
    private int damageAmount = 6;

    public StrictTutorEvent() {
        super(eventStrings.NAME, DESCRIPTIONS[0], IMG);

        if (AbstractDungeon.ascensionLevel >= 15) {
            damageAmount = 9;
        }

        // 选项1: 逃课 (扣血删牌)
        imageEventText.setDialogOption(OPTIONS[0] + damageAmount + OPTIONS[1]);
        // 选项2: 作弊 (升级2张攻击 + 疑虑)
        imageEventText.setDialogOption(OPTIONS[2], new Doubt());
        // 选项3: 听讲 (升级1张)
        imageEventText.setDialogOption(OPTIONS[3]);
    }

    @Override
    protected void buttonEffect(int i) {
        switch (screenNum) {
            case 0:
                switch (i) {
                    case 0: // 逃课
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.damage(new com.megacrit.cardcrawl.cards.DamageInfo(null, damageAmount, com.megacrit.cardcrawl.cards.DamageInfo.DamageType.HP_LOSS));
                        // 打开删牌界面
                        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck, 1, "选择一张牌移除", false, false, false, true);
                        break;

                    case 1: // 作弊
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        cheatUpgrade();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Doubt(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                        endEvent();
                        break;

                    case 2: // 听讲
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        // 打开升级界面
                        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck, 1, "选择一张牌升级", true, false, false, false);
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
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);

            // 简单的判断：如果是逃课（有扣血动作，或者根据按钮索引判断有点难，这里假设只能选一次）
            // 更好的方式是用一个变量记录当前模式，但这里因为逻辑不重叠，我们根据 updateBodyText 的结果或 dialog option 来判定太麻烦
            // 我们可以直接看 selectedCards 的用途

            // 如果是删牌 (Purge)
            // 这是一个简化的判断逻辑：如果当前Dialog文本对应“逃课”
            if (GenericEventDialog.getSelectedOption() == 0) {
                AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f));
                AbstractDungeon.player.masterDeck.removeCard(c);
            }
            // 如果是升级 (Upgrade)
            else if (GenericEventDialog.getSelectedOption() == 2) {
                c.upgrade();
                AbstractDungeon.player.bottledCardUpgradeCheck(c);
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
            }

            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            endEvent();
        }
    }

    private void cheatUpgrade() {
        ArrayList<AbstractCard> attacks = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.type == AbstractCard.CardType.ATTACK && c.canUpgrade()) {
                attacks.add(c);
            }
        }

        Collections.shuffle(attacks, new Random(AbstractDungeon.miscRng.randomLong()));

        int count = 0;
        for (AbstractCard c : attacks) {
            if (count >= 3) break;
            c.upgrade();
            AbstractDungeon.player.bottledCardUpgradeCheck(c);
            AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), Settings.WIDTH / 2.0f - 200 + count * 400, Settings.HEIGHT / 2.0f));
            count++;
        }
    }

    private void endEvent() {
        this.imageEventText.clearAllDialogs();
        this.imageEventText.setDialogOption(OPTIONS[4]); // 离开
        screenNum = 1;
    }
}