package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;
import theBalance.powers.CombatGoldPower;

import static theBalance.BalanceMod.makeCardPath;

public class Lending extends AbstractDynamicCard {
    public static final String ID = BalanceMod.makeID(Lending.class.getSimpleName());
    public static final String IMG = makeCardPath("Lending.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 0;
    private static final int MAGIC = 40;
    private static final int MAGIC2 = 60;
    private static final int UPGRADE_PLUS_GOLD = 20;
    private static final int MAGIC3 = 3;

    public Lending() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
        baseDefaultSecondMagicNumber = defaultSecondMagicNumber = MAGIC2;
        this.isEthereal = true;
        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int goldNeeded = magicNumber;
        int fromCombatGold = 0;
        int fromRegularGold = 0;

        // 检查战斗津贴
        if (p.hasPower(CombatGoldPower.POWER_ID)) {
            CombatGoldPower combatGoldPower = (CombatGoldPower) p.getPower(CombatGoldPower.POWER_ID);
            fromCombatGold = Math.min(combatGoldPower.amount, goldNeeded);
            goldNeeded -= fromCombatGold;
        }

        // 检查剩余金币
        if (goldNeeded > 0) {
            fromRegularGold = Math.min(p.gold, goldNeeded);
        }

        // 消耗战斗津贴
        if (fromCombatGold > 0) {
            AbstractDungeon.actionManager.addToBottom(
                    new ReducePowerAction(p, p, CombatGoldPower.POWER_ID, fromCombatGold));
        }

        // 消耗金币
        if (fromRegularGold > 0) {
            p.loseGold(fromRegularGold);
        }

        for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!monster.isDeadOrEscaped()) {
                AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(monster, p, new StrengthPower(monster, MAGIC3), MAGIC3));
            }
        }
        // 战胜后获金币
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new theBalance.powers.LendingPower(p, defaultSecondMagicNumber), defaultSecondMagicNumber));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDefaultSecondMagicNumber(UPGRADE_PLUS_GOLD);
            initializeDescription();
        }
    }
}
