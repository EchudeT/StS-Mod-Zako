package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.BufferPower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class PhaseShift extends AbstractDynamicCard {

    // 相位转移 - Phase Shift
    // 消耗。获得 1(2) 层缓冲。下回合敌人获得 1 层缓冲。

    public static final String ID = BalanceMod.makeID(PhaseShift.class.getSimpleName());
    public static final String IMG = makeCardPath("PhaseShift.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 2;
    private static final int MAGIC = 1;  // 缓冲层数
    private static final int UPGRADE_PLUS_MAGIC = 1;

    public PhaseShift() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 玩家立即获得缓冲
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new BufferPower(p, magicNumber), magicNumber));

        // 应用延迟Power，下回合给敌人缓冲
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new theBalance.powers.PhaseShiftDelayedPower(p), -1));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            initializeDescription();
        }
    }
}
