package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.TheDefault;

import static theBalance.BalanceMod.makeCardPath;

public class InfiniteExpansion extends AbstractDynamicCard {

    // 无限膨胀 - Infinite Expansion
    // 造成 6(8) 伤害。每存留一回合伤害翻倍。受伤后倍率重置。

    public static final String ID = BalanceMod.makeID(InfiniteExpansion.class.getSimpleName());
    public static final String IMG = makeCardPath("InfiniteExpansion.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = TheDefault.Enums.COLOR_GRAY;

    private static final int COST = 2;
    private static final int DAMAGE = 6;
    private static final int UPGRADE_PLUS_DMG = 2;

    public InfiniteExpansion() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
        this.selfRetain = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 确保玩家有追踪Power
        if (!p.hasPower(theBalance.powers.InfiniteExpansionPower.POWER_ID)) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new theBalance.powers.InfiniteExpansionPower(p), -1));
        }

        // 计算实际伤害（每存留一回合翻倍）
        int turnsRetained = 0;
        if (p.hasPower(theBalance.powers.InfiniteExpansionPower.POWER_ID)) {
            turnsRetained = p.getPower(theBalance.powers.InfiniteExpansionPower.POWER_ID).amount;
        }

        int actualDamage = damage;
        for (int i = 0; i < turnsRetained; i++) {
            actualDamage *= 2;
        }

        AbstractDungeon.actionManager.addToBottom(
            new DamageAction(m, new DamageInfo(p, actualDamage, damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY));
    }

    @Override
    public void applyPowers() {
        // 先调用父类的 applyPowers 计算基础伤害
        super.applyPowers();

        // 获取倍率
        int turnsRetained = 0;
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasPower(theBalance.powers.InfiniteExpansionPower.POWER_ID)) {
            turnsRetained = AbstractDungeon.player.getPower(theBalance.powers.InfiniteExpansionPower.POWER_ID).amount;
        }

        // 根据回合数翻倍伤害值（不修改baseDamage）
        if (turnsRetained > 0) {
            for (int i = 0; i < turnsRetained; i++) {
                this.damage *= 2;
            }
            this.isDamageModified = true;
        }
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        // 先调用父类方法
        super.calculateCardDamage(mo);

        // 获取倍率
        int turnsRetained = 0;
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasPower(theBalance.powers.InfiniteExpansionPower.POWER_ID)) {
            turnsRetained = AbstractDungeon.player.getPower(theBalance.powers.InfiniteExpansionPower.POWER_ID).amount;
        }

        // 根据回合数翻倍伤害值
        if (turnsRetained > 0) {
            for (int i = 0; i < turnsRetained; i++) {
                this.damage *= 2;
            }
            this.isDamageModified = true;
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
