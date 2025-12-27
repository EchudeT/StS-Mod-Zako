package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.TheDefault;

import static theBalance.BalanceMod.makeCardPath;

public class QuickFinancing extends AbstractDynamicCard {

    // 快速融资 - Quick Financing
    // 获得 [E]。将一张[负债]加入抽牌堆。 (升级后：Innate)

    public static final String ID = BalanceMod.makeID(QuickFinancing.class.getSimpleName());
    public static final String IMG = makeCardPath("QuickFinancing.png");

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = TheDefault.Enums.COLOR_GRAY;

    private static final int COST = 0;
    private static final int MAGIC = 1;  // 获得的能量

    public QuickFinancing() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
            new GainEnergyAction(magicNumber));

        // 使用Slimed作为负债的替代
        AbstractDungeon.actionManager.addToBottom(
            new MakeTempCardInDrawPileAction(new Slimed(), 1, true, true));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            this.isInnate = true;
            initializeDescription();
        }
    }
}
