package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.TheDefault;

import static theBalance.BalanceMod.makeCardPath;

public class BalancingAct extends AbstractDynamicCard {

    // 配平 - Balancing Act
    // 获得 5 点格挡。若上回合失去过生命，改为获得 10(14) 点格挡。

    public static final String ID = BalanceMod.makeID(BalancingAct.class.getSimpleName());
    public static final String IMG = makeCardPath("BalancingAct.png");

    private static final CardRarity RARITY = CardRarity.BASIC;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = TheDefault.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int BLOCK = 5;
    private static final int MAGIC = 10;  // 受伤后的格挡值
    private static final int UPGRADE_PLUS_MAGIC = 4;

    public BalancingAct() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseBlock = BLOCK;
        baseMagicNumber = magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int blockAmount = block;

        // 检查上回合是否失去过生命（Power在战斗开始时已自动添加）
        if (p.hasPower(theBalance.powers.DamageTakenLastTurnPower.POWER_ID)) {
            theBalance.powers.DamageTakenLastTurnPower power =
                (theBalance.powers.DamageTakenLastTurnPower) p.getPower(theBalance.powers.DamageTakenLastTurnPower.POWER_ID);
            if (power.wasDamagedLastTurn()) {
                blockAmount = magicNumber;
            }
        }

        AbstractDungeon.actionManager.addToBottom(
            new GainBlockAction(p, p, blockAmount));
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
