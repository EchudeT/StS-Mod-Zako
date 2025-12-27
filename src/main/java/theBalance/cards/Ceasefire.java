package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class Ceasefire extends AbstractDynamicCard {

    // 全面停火 - Ceasefire
    // 消耗。本回合全场伤害变为 0。 (升级后：2费)

    public static final String ID = BalanceMod.makeID(Ceasefire.class.getSimpleName());
    public static final String IMG = makeCardPath("Ceasefire.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ALL;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 3;

    public Ceasefire() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 本回合全场伤害变为0
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new theBalance.powers.CeasefirePower(p), -1));

        // 也给所有敌人应用这个Power
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(mo, p, new theBalance.powers.CeasefirePower(mo), -1));
            }
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(2);
            initializeDescription();
        }
    }
}
