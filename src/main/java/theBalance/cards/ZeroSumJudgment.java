package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class ZeroSumJudgment extends AbstractDynamicCard {

    // 零和裁决 - Zero Sum Judgment
    // 造成 18(24) 点伤害。偷取敌人所有力量，回合结束归还。

    public static final String ID = BalanceMod.makeID(ZeroSumJudgment.class.getSimpleName());
    public static final String IMG = makeCardPath("ZeroSumJudgment.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 2;
    private static final int DAMAGE = 18;
    private static final int UPGRADE_PLUS_DMG = 6;

    public ZeroSumJudgment() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
            new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_HEAVY));

        // 获取敌人的力量
        int enemyStrength = 0;
        if (m.hasPower("Strength")) {
            enemyStrength = m.getPower("Strength").amount;
        }

        if (enemyStrength > 0) {
            // 敌人失去力量
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(m, p, new StrengthPower(m, -enemyStrength), -enemyStrength));

            // 玩家获得力量 (临时, 回合结束归还)
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new StrengthPower(p, enemyStrength), enemyStrength));

            // 回合结束时归还
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new theBalance.powers.ZeroSumGamePower(p, m, enemyStrength), enemyStrength));
        }
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
