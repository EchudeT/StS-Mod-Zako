package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;
import theBalance.powers.DefensiveStancePower;

import static theBalance.BalanceMod.makeCardPath;

public class BalancePivot extends AbstractDynamicCard {

    // 天平支点 - Balance Pivot
    // 消耗。造成 12(16) 点伤害。本回合将敌人的力量和防御姿态变为 0。

    public static final String ID = BalanceMod.makeID(BalancePivot.class.getSimpleName());
    public static final String IMG = makeCardPath("BalancePivot.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 2;
    private static final int DAMAGE = 12;
    private static final int UPGRADE_PLUS_DMG = 4;

    public BalancePivot() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 造成伤害
        AbstractDungeon.actionManager.addToBottom(
            new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY));

        // 获取敌人当前的力量和防御姿态
        int enemyStr = 0;
        int enemyStance = 0;

        if (m.hasPower(StrengthPower.POWER_ID)) {
            enemyStr = m.getPower(StrengthPower.POWER_ID).amount;
        }
        if (m.hasPower(DefensiveStancePower.POWER_ID)) {
            enemyStance = m.getPower(DefensiveStancePower.POWER_ID).amount;
        }

        // 本回合将敌人的力量变为0
        if (enemyStr != 0) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(m, p, new StrengthPower(m, -enemyStr), -enemyStr));
        }

        // 本回合移除敌人的防御姿态
        if (enemyStance > 0) {
            AbstractDungeon.actionManager.addToBottom(
                new RemoveSpecificPowerAction(m, p, DefensiveStancePower.POWER_ID));
        }

        // 应用天平支点Power，回合结束时恢复
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(m, p, new theBalance.powers.BalancePivotPower(m, enemyStr, enemyStance), -1));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_PLUS_DMG);
            initializeDescription();
        }
    }
}
