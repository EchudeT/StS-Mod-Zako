package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;
import theBalance.powers.CombatGoldPower;

import static theBalance.BalanceMod.makeCardPath;

public class BankruptcyRush extends AbstractDynamicCard {

    // 破产冲击 - Bankruptcy Rush
    // 消耗 15(10) 金币。造成 12(16) 点伤害。金币不足则扣 5 生命。

    public static final String ID = BalanceMod.makeID(BankruptcyRush.class.getSimpleName());
    public static final String IMG = makeCardPath("BankruptcyRush.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 12;
    private static final int UPGRADE_PLUS_DMG = 4;
    private static final int MAGIC = 15;  // 消耗的金币
    private static final int UPGRADE_REDUCE_GOLD = 5;
    private static final int MAGIC2 = 5;  // 金币不足时扣的生命

    public BankruptcyRush() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
        baseMagicNumber = magicNumber = MAGIC;
        baseDefaultSecondMagicNumber = defaultSecondMagicNumber = MAGIC2;
        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 优先消耗战斗津贴，不足时消耗金币，金币不足则扣生命
        int goldNeeded = magicNumber;
        int fromCombatGold = 0;
        int fromRegularGold = 0;

        // 检查战斗津贴
        if (p.hasPower(CombatGoldPower.POWER_ID)) {
            CombatGoldPower combatGoldPower = (CombatGoldPower) p.getPower(CombatGoldPower.POWER_ID);
            fromCombatGold = Math.min(combatGoldPower.amount, goldNeeded);
            goldNeeded -= fromCombatGold;
        }

        // 检查剩余金币是否足够
        if (goldNeeded > 0) {
            fromRegularGold = Math.min(p.gold, goldNeeded);
            goldNeeded -= fromRegularGold;
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

        // 如果还有欠款，扣生命
        if (goldNeeded > 0) {
            AbstractDungeon.actionManager.addToBottom(
                new LoseHPAction(p, p, defaultSecondMagicNumber));
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
            upgradeMagicNumber(-UPGRADE_REDUCE_GOLD);  // 降低所需金币
            initializeDescription();
        }
    }
}
