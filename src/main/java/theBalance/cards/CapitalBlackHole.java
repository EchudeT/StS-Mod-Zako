package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;
import theBalance.powers.CombatGoldPower;

import java.util.Arrays;

import static theBalance.BalanceMod.makeCardPath;

public class CapitalBlackHole extends AbstractDynamicCard {

    // 资本黑洞 - Capital Black Hole
    // 消耗所有战斗津贴。每 5 点津贴对全体敌人造成 3(5) 点伤害。

    public static final String ID = BalanceMod.makeID(CapitalBlackHole.class.getSimpleName());
    public static final String IMG = makeCardPath("CapitalBlackHole.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 2;
    private static final int MAGIC = 5;
    private static final int MAGIC2 = 3;
    private static final int UPGRADE_PLUS_DAMAGE = 2;

    public CapitalBlackHole() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
        defaultBaseSecondMagicNumber = baseDefaultSecondMagicNumber = defaultSecondMagicNumber = MAGIC2;
        isMultiDamage = true;
        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!p.hasPower(CombatGoldPower.POWER_ID))return;
        int totalCombatGold = p.getPower(CombatGoldPower.POWER_ID).amount;
        int damagePerUnit = defaultSecondMagicNumber;
        int totalDamage = totalCombatGold / magicNumber * damagePerUnit;

        if (totalCombatGold > 0) {
            AbstractDungeon.actionManager.addToBottom(
                    new ReducePowerAction(p, p, CombatGoldPower.POWER_ID, totalCombatGold));
        }

        if (totalDamage > 0) {
            int[] damageArray = new int[AbstractDungeon.getCurrRoom().monsters.monsters.size()];
            Arrays.fill(damageArray, totalDamage);

            AbstractDungeon.actionManager.addToBottom(
                new DamageAllEnemiesAction(p, damageArray, DamageInfo.DamageType.THORNS,
                    AbstractGameAction.AttackEffect.FIRE));
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDefaultSecondMagicNumber(UPGRADE_PLUS_DAMAGE);
            initializeDescription();
        }
    }
}
