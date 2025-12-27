package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class ChainReaction extends AbstractDynamicCard {

    // 连锁反应 - Chain Reaction
    // 造成全体 2(3) 点伤害 3 次。获得 3 层易伤。

    public static final String ID = BalanceMod.makeID(ChainReaction.class.getSimpleName());
    public static final String IMG = makeCardPath("ChainReaction.png");

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 2;
    private static final int UPGRADE_PLUS_DMG = 1;
    private static final int MAGIC = 3;  // 攻击次数
    private static final int MAGIC2 = 3;  // 易伤层数

    public ChainReaction() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
        baseMagicNumber = magicNumber = MAGIC;
        baseDefaultSecondMagicNumber = defaultSecondMagicNumber = MAGIC2;
        isMultiDamage = true;
        // 更新描述以显示第二魔法数字
        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < magicNumber; i++) {
            AbstractDungeon.actionManager.addToBottom(
                new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn),
                    AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        }

        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new VulnerablePower(p, defaultSecondMagicNumber, false), defaultSecondMagicNumber));
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
