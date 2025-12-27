package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;
import theBalance.powers.CombatGoldPower;

import static theBalance.BalanceMod.makeCardPath;

public class MoneyOffensive extends AbstractDynamicCard {

    // 金钱攻势 - Money Offensive
    // 消耗。消耗 10 金币。造成 15(20) 点伤害。

    public static final String ID = BalanceMod.makeID(MoneyOffensive.class.getSimpleName());
    public static final String IMG = makeCardPath("MoneyOffensive.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 15;
    private static final int UPGRADE_PLUS_DMG = 5;
    private static final int MAGIC = 10;  // 消耗的金币

    public MoneyOffensive() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
        baseMagicNumber = magicNumber = MAGIC;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 优先消耗战斗津贴，不足时消耗金币
        int goldNeeded = magicNumber;
        int fromCombatGold = 0;

        // 检查战斗津贴
        if (p.hasPower(CombatGoldPower.POWER_ID)) {
            CombatGoldPower combatGoldPower = (CombatGoldPower) p.getPower(CombatGoldPower.POWER_ID);
            fromCombatGold = Math.min(combatGoldPower.amount, goldNeeded);
            goldNeeded -= fromCombatGold;
        }

        // 消耗战斗津贴
        if (fromCombatGold > 0) {
            AbstractDungeon.actionManager.addToBottom(
                new ReducePowerAction(p, p, CombatGoldPower.POWER_ID, fromCombatGold));
        }

        // 消耗剩余金币
        if (goldNeeded > 0) {
            p.loseGold(goldNeeded);
        }

        AbstractDungeon.actionManager.addToBottom(
            new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY));
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
