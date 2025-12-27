package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class Fluctuation extends AbstractDynamicCard {

    // 波动 - Fluctuation
    // 消耗。全场所有状态层数翻倍。 (升级后：1费)

    public static final String ID = BalanceMod.makeID(Fluctuation.class.getSimpleName());
    public static final String IMG = makeCardPath("Fluctuation.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ALL;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 2;

    public Fluctuation() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 翻倍玩家的所有power
        for (AbstractPower power : p.powers) {
            if (power.amount > 0) {
                AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(p, p, power, power.amount));
            }
        }

        // 翻倍所有敌人的power
        for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!monster.isDeadOrEscaped()) {
                for (AbstractPower power : monster.powers) {
                    if (power.amount > 0) {
                        AbstractDungeon.actionManager.addToBottom(
                            new ApplyPowerAction(monster, p, power, power.amount));
                    }
                }
            }
        }
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
