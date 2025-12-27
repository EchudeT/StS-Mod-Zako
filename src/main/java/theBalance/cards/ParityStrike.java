package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.TheDefault;

import static theBalance.BalanceMod.makeCardPath;

public class ParityStrike extends AbstractDynamicCard {

    // 对等打击 - Parity Strike
    // 造成 6(9) 点伤害。若上回合失去过生命，额外造成 4(6) 点伤害。

    public static final String ID = BalanceMod.makeID(ParityStrike.class.getSimpleName());
    public static final String IMG = makeCardPath("ParityStrike.png");

    private static final CardRarity RARITY = CardRarity.BASIC;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = TheDefault.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 6;
    private static final int UPGRADE_PLUS_DMG = 3;
    private static final int MAGIC = 4;  // 额外伤害
    private static final int UPGRADE_PLUS_MAGIC = 2;

    public ParityStrike() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
        baseMagicNumber = magicNumber = MAGIC;
        this.tags.add(CardTags.STARTER_STRIKE);
        this.tags.add(CardTags.STRIKE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int totalDamage = damage;

        // 检查上回合是否受到过伤害（Power在战斗开始时已自动添加）
        if (p.hasPower(theBalance.powers.DamageTakenLastTurnPower.POWER_ID)) {
            theBalance.powers.DamageTakenLastTurnPower power =
                (theBalance.powers.DamageTakenLastTurnPower) p.getPower(theBalance.powers.DamageTakenLastTurnPower.POWER_ID);
            if (power.wasDamagedLastTurn()) {
                totalDamage += magicNumber;
            }
        }

        AbstractDungeon.actionManager.addToBottom(
            new DamageAction(m, new DamageInfo(p, totalDamage, damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_PLUS_DMG);
            upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            initializeDescription();
        }
    }
}
