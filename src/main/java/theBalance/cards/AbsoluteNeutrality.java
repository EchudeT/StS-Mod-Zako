package theBalance.cards;

import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.TheDefault;

import static theBalance.BalanceMod.makeCardPath;

public class AbsoluteNeutrality extends AbstractDynamicCard {

    // 绝对中立 - Absolute Neutrality
    // 消耗。若敌血量比你高，将其生命降至与你相同（上限 40(60)）。

    public static final String ID = BalanceMod.makeID(AbsoluteNeutrality.class.getSimpleName());
    public static final String IMG = makeCardPath("AbsoluteNeutrality.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = TheDefault.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int MAGIC = 40;  // 伤害上限
    private static final int UPGRADE_PLUS_MAGIC = 20;

    public AbsoluteNeutrality() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null && m.currentHealth > p.currentHealth) {
            // 计算需要造成的伤害
            int damageAmount = Math.min(m.currentHealth - p.currentHealth, magicNumber);

            if (damageAmount > 0) {
                m.damage(new DamageInfo(p, damageAmount, DamageInfo.DamageType.HP_LOSS));
            }
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            initializeDescription();
        }
    }
}
