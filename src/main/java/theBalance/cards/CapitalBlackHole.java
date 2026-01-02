package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
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
    private static final int MAGIC = 5;  // 阈值：每5点
    private static final int MAGIC2 = 3; // 伤害：3点
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
        if (!p.hasPower(CombatGoldPower.POWER_ID)) return;

        int totalCombatGold = p.getPower(CombatGoldPower.POWER_ID).amount;

        // 1. 计算攻击段数 (例如 20 津贴 / 5 = 4 段)
        int hitCount = totalCombatGold / magicNumber;

        // 2. 获取单段伤害数值 (3 或 5)
        int damagePerHit = defaultSecondMagicNumber;

        // 消耗所有津贴
        if (totalCombatGold > 0) {
            AbstractDungeon.actionManager.addToBottom(
                    new ReducePowerAction(p, p, CombatGoldPower.POWER_ID, totalCombatGold));
        }

        // 3. 循环造成伤害
        if (hitCount > 0) {
            // 构建伤害数组：数组中每个元素都是单段伤害值 (damagePerHit)
            int[] damageMatrix = new int[AbstractDungeon.getCurrRoom().monsters.monsters.size()];
            Arrays.fill(damageMatrix, damagePerHit);

            for (int i = 0; i < hitCount; i++) {
                // 每次循环加入一个对全体造成伤害的动作
                // 为了视觉效果不至于太单调，你可以让攻击特效在 FIRE 和 SLASH 之间切换，或者统一用 FIRE
                // 你目前使用的是 THORNS（荆棘伤害）。这种伤害通常不享受力量（Strength）加成，也不会触发像“造成未被格挡的伤害时”这样的遗物效果。
                // 如果希望这几段伤害能享受力量加成（例如：力量+2，每段伤害从3变成5），需要将 DamageType.THORNS 改为 DamageType.NORMAL。多段伤害配合力量流是非常强力的机制。
                AbstractDungeon.actionManager.addToBottom(
                        new DamageAllEnemiesAction(p, damageMatrix, DamageInfo.DamageType.NORMAL,
                                AbstractGameAction.AttackEffect.FIRE));
            }
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