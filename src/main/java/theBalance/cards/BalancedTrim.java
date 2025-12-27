package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class BalancedTrim extends AbstractDynamicCard {

    // 均衡剪裁 - Balanced Trim
    // 造成 8(11) 点伤害。双方格挡重置为两人的平均值。

    public static final String ID = BalanceMod.makeID(BalancedTrim.class.getSimpleName());
    public static final String IMG = makeCardPath("BalancedTrim.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 8;
    private static final int UPGRADE_PLUS_DMG = 3;

    public BalancedTrim() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
        isMultiDamage = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
            new DamageAllEnemiesAction(p, multiDamage, damageTypeForTurn,
                AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));

        // 计算平均格挡
        // 简化：只与目标敌人交换格挡
        if (m != null) {
            int playerBlock = p.currentBlock;
            int enemyBlock = m.currentBlock;
            int avgBlock = (playerBlock + enemyBlock) / 2;

            // 重置格挡
            p.loseBlock();
            m.loseBlock();

            // 设置平均格挡
            if (avgBlock > 0) {
                AbstractDungeon.actionManager.addToBottom(
                    new GainBlockAction(p, p, avgBlock));
                AbstractDungeon.actionManager.addToBottom(
                    new GainBlockAction(m, p, avgBlock));
            }
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
