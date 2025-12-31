package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class DuskPact extends AbstractDynamicCard {

    // 黄昏协定 - Dusk Pact
    // 双方减 1(2) 点力量。获得 2 层 4(6) 点格挡。

    public static final String ID = BalanceMod.makeID(DuskPact.class.getSimpleName());
    public static final String IMG = makeCardPath("DuskPact.png");

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int MAGIC = 1;  // 减少的力量
    private static final int UPGRADE_PLUS_MAGIC = 1;
    private static final int MAGIC2 = 4;  // 格挡值
    private static final int UPGRADE_PLUS_BLOCK = 2;

    public DuskPact() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
        baseDefaultSecondMagicNumber = defaultSecondMagicNumber = MAGIC2;
        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 双方减力量
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new StrengthPower(p, -magicNumber), -magicNumber));

        if (m != null) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(m, p, new StrengthPower(m, -magicNumber), -magicNumber));
        }

        // 获得2层格挡
        AbstractDungeon.actionManager.addToBottom(
            new GainBlockAction(p, p, defaultSecondMagicNumber));
        AbstractDungeon.actionManager.addToBottom(
            new GainBlockAction(p, p, defaultSecondMagicNumber));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            upgradeDefaultSecondMagicNumber(UPGRADE_PLUS_BLOCK);
            initializeDescription();
        }
    }
}
