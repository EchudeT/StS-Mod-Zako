package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.characters.TheDefault;

import static theBalance.BalanceMod.makeCardPath;

public class ImbalancedBooks extends AbstractDynamicCard {

    // 收支不平 - Imbalanced Books
    // 造成 8(11) 点伤害。如果敌人的力量高于你，伤害翻倍。

    public static final String ID = BalanceMod.makeID(ImbalancedBooks.class.getSimpleName());
    public static final String IMG = makeCardPath("ImbalancedBooks.png");

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = TheDefault.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 8;
    private static final int UPGRADE_PLUS_DMG = 3;

    public ImbalancedBooks() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int totalDamage = damage;

        // 获取玩家和敌人的力量
        int playerStrength = 0;
        int enemyStrength = 0;

        if (p.hasPower("Strength")) {
            playerStrength = p.getPower("Strength").amount;
        }
        if (m.hasPower("Strength")) {
            enemyStrength = m.getPower("Strength").amount;
        }

        // 如果敌人力量高于玩家，伤害翻倍
        if (enemyStrength > playerStrength) {
            totalDamage *= 2;
        }

        AbstractDungeon.actionManager.addToBottom(
            new DamageAction(m, new DamageInfo(p, totalDamage, damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_HEAVY));
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
