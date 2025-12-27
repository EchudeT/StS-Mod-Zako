package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class ScaleTilt extends AbstractDynamicCard {

    // 天平倾斜 - Scale Tilt
    // 敌+3力。每当敌获力量，你获等量。 (升级后：1费)

    public static final String ID = BalanceMod.makeID(ScaleTilt.class.getSimpleName());
    public static final String IMG = makeCardPath("ScaleTilt.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.POWER;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 2;
    private static final int MAGIC = 3;

    public ScaleTilt() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(m, p, new StrengthPower(m, magicNumber), magicNumber));
        }
        // 每当敌获力量,你获等量
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new theBalance.powers.ScaleTiltPower(p), -1));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(1);
            initializeDescription();
        }
    }
}
