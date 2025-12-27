package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.characters.TheDefault;

import static theBalance.BalanceMod.makeCardPath;

public class WealthEqualization extends AbstractDynamicCard {

    // 均贫卡 - Wealth Equalization
    // 双方力量取平均值。 (升级后：0费)

    public static final String ID = BalanceMod.makeID(WealthEqualization.class.getSimpleName());
    public static final String IMG = makeCardPath("WealthEqualization.png");

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = TheDefault.Enums.COLOR_GRAY;

    private static final int COST = 1;

    public WealthEqualization() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 获取双方力量
        int playerStr = 0;
        int enemyStr = 0;

        if (p.hasPower("Strength")) {
            playerStr = p.getPower("Strength").amount;
        }
        if (m.hasPower("Strength")) {
            enemyStr = m.getPower("Strength").amount;
        }

        // 计算平均值
        int avgStr = (playerStr + enemyStr) / 2;

        // 调整双方力量到平均值
        int playerDiff = avgStr - playerStr;
        int enemyDiff = avgStr - enemyStr;

        if (playerDiff != 0) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new StrengthPower(p, playerDiff), playerDiff));
        }
        if (enemyDiff != 0) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(m, p, new StrengthPower(m, enemyDiff), enemyDiff));
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(0);
            initializeDescription();
        }
    }
}
