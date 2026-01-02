package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class InfiniteExpansion extends AbstractDynamicCard {

    // 无限膨胀 - Infinite Expansion
    // 造成 6(8) 伤害。每存留一回合伤害翻倍。
    // 副作用：此卡在手牌中时，每层膨胀使你受到的伤害增加 100%。

    public static final String ID = BalanceMod.makeID(InfiniteExpansion.class.getSimpleName());
    public static final String IMG = makeCardPath("InfiniteExpansion.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 6;
    private static final int UPGRADE_PLUS_DMG = 2;

    public static final float VULNERABLE_RATE = 1f; // 100% 易伤加成

    public int expansionStage = 0;

    public InfiniteExpansion() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
        this.selfRetain = true;
    }

    @Override
    public void atTurnStart() {
        this.expansionStage++;
        this.applyPowers();
        this.initializeDescription();
        updateMonsterIntents();
    }

    @Override
    public void triggerWhenDrawn() {
        super.triggerWhenDrawn();
        resetExpansion();
    }

    // 重置逻辑
    public void resetExpansion() {
        if (this.expansionStage > 0) {
            this.expansionStage = 0;
            // 只有在手里时才闪烁，避免奇怪的视觉效果
            if (AbstractDungeon.player.hand.contains(this)) {
                this.flash();
            }
            this.applyPowers();
            updateMonsterIntents();
        }
    }

    // 刷新怪物意图（因为怪物对你的伤害可能因为此卡的层数而改变）
    private void updateMonsterIntents() {
        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().monsters != null) {
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!m.isDeadOrEscaped()) {
                    m.applyPowers();
                }
            }
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
                new DamageAction(m, new DamageInfo(p, this.damage, damageTypeForTurn),
                        AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        this.expansionStage = 0;

        updateMonsterIntents();
    }

    @Override
    public void applyPowers() {
        this.damage = this.baseDamage;
        this.isDamageModified = false;

        super.applyPowers();

        // 计算翻倍伤害
        if (this.expansionStage > 0) {
            float multiplier = (float) Math.pow(2, this.expansionStage);
            this.damage = (int)(this.damage * multiplier);
            this.isDamageModified = true;
        }

        // 更新描述
        int riskPercent = (int)(this.expansionStage * VULNERABLE_RATE * 100);
        this.rawDescription = this.originalRawDescription + " NL " +
                "(伤害: " + (int)Math.pow(2, this.expansionStage) + "倍, " +
                "受伤: " + riskPercent / 100 + "倍 )";
        super.initializeDescription();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        this.damage = this.baseDamage;
        this.isDamageModified = false;
        super.calculateCardDamage(mo);

        if (this.expansionStage > 0) {
            float multiplier = (float) Math.pow(2, this.expansionStage);
            this.damage = (int)(this.damage * multiplier);
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