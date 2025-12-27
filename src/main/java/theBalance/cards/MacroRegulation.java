package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class MacroRegulation extends AbstractDynamicCard {

    // 宏观调控 - Macro Regulation
    // 你的力量和敏捷永远取两者的平均值。 (升级后：Innate)

    public static final String ID = BalanceMod.makeID(MacroRegulation.class.getSimpleName());
    public static final String IMG = makeCardPath("MacroRegulation.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.POWER;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 2;

    public MacroRegulation() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new theBalance.powers.MacroRegulationPower(p), -1));
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
