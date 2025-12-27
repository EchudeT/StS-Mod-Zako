package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class AssetSwap extends AbstractDynamicCard {

    // 资产置换 - Asset Swap
    // 造成 9(12) 点伤害。交换你与敌人的易伤层数。

    public static final String ID = BalanceMod.makeID(AssetSwap.class.getSimpleName());
    public static final String IMG = makeCardPath("AssetSwap.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 9;
    private static final int UPGRADE_PLUS_DMG = 3;

    public AssetSwap() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
            new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

        // 获取双方的易伤层数
        int playerVuln = 0;
        int enemyVuln = 0;

        if (p.hasPower("Vulnerable")) {
            playerVuln = p.getPower("Vulnerable").amount;
        }
        if (m.hasPower("Vulnerable")) {
            enemyVuln = m.getPower("Vulnerable").amount;
        }

        // 交换易伤
        if (p.hasPower("Vulnerable")) {
            AbstractDungeon.actionManager.addToBottom(
                new RemoveSpecificPowerAction(p, p, "Vulnerable"));
        }
        if (m.hasPower("Vulnerable")) {
            AbstractDungeon.actionManager.addToBottom(
                new RemoveSpecificPowerAction(m, p, "Vulnerable"));
        }

        if (enemyVuln > 0) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new VulnerablePower(p, enemyVuln, false), enemyVuln));
        }
        if (playerVuln > 0) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(m, p, new VulnerablePower(m, playerVuln, false), playerVuln));
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
