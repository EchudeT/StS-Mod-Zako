package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class Collection extends AbstractDynamicCard {

    // 催收 - Collection
    // 造成 7(10) 点伤害。若目标有 Debuff，抽 1 张牌。

    public static final String ID = BalanceMod.makeID(Collection.class.getSimpleName());
    public static final String IMG = makeCardPath("Collection.png");

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 7;
    private static final int UPGRADE_PLUS_DMG = 3;
    private static final int MAGIC = 1;  // 抽牌数

    public Collection() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
        baseMagicNumber = magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
            new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

        // 检查目标是否有负面效果
        boolean hasDebuff = false;
        if (m != null) {
            for (AbstractPower power : m.powers) {
                if (power.type == AbstractPower.PowerType.DEBUFF) {
                    hasDebuff = true;
                    break;
                }
            }
        }

        if (hasDebuff) {
            AbstractDungeon.actionManager.addToBottom(
                new DrawCardAction(p, magicNumber));
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
