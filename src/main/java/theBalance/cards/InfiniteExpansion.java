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
    // 副作用：此卡在手牌中时，每层膨胀使你受到的伤害增加 25%。

    public static final String ID = BalanceMod.makeID(InfiniteExpansion.class.getSimpleName());
    public static final String IMG = makeCardPath("InfiniteExpansion.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 2;
    private static final int DAMAGE = 6;
    private static final int UPGRADE_PLUS_DMG = 2;

    // 平衡参数：每层增加受到的伤害百分比 (0.25 = 25%)
    public static final float VULNERABLE_RATE = 0.25f;

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
        // 关键：层数增加后，刷新敌人意图
        updateMonsterIntents();
    }

    // 关键：如果抽到了这张本身带有层数的牌（例如洗牌后抽回），也刷新意图
    @Override
    public void triggerWhenDrawn() {
        super.triggerWhenDrawn();
        if (this.expansionStage > 0) {
            updateMonsterIntents();
        }
    }

    public void resetExpansion() {
        if (this.expansionStage > 0) {
            this.expansionStage = 0;
            if (AbstractDungeon.player.hand.contains(this)) {
                this.flash();
            }
            this.applyPowers();
            // 关键：重置后，刷新敌人意图（数字变回正常）
            updateMonsterIntents();
        }
    }

    // 辅助方法：通知所有活着的怪物重新计算伤害
    private void updateMonsterIntents() {
        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().monsters != null) {
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!m.isDeadOrEscaped()) {
                    m.applyPowers(); // 这会触发 calculateDamage 并更新 intentDmg
                }
            }
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
                new DamageAction(m, new DamageInfo(p, this.damage, damageTypeForTurn),
                        AbstractGameAction.AttackEffect.BLUNT_HEAVY));
    }

    @Override
    public void applyPowers() {
        this.damage = this.baseDamage;
        this.isDamageModified = false;

        super.applyPowers();

        if (this.expansionStage > 0) {
            float multiplier = (float) Math.pow(2, this.expansionStage);
            this.damage = (int)(this.damage * multiplier);
            this.isDamageModified = true;
        }

        int riskPercent = (int)(this.expansionStage * VULNERABLE_RATE * 100);

        this.rawDescription = this.originalRawDescription + " NL " +
                "(伤害: " + (int)Math.pow(2, this.expansionStage) + "倍, " +
                "受伤: #r+" + riskPercent + "% )";
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