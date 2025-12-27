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

public class ParityStrike extends AbstractDynamicCard {

    // 对等打击 - Parity Strike
    // 造成 6(9) 点伤害。若上回合失去过生命，额外造成 4(6) 点伤害。

    public static final String ID = BalanceMod.makeID(ParityStrike.class.getSimpleName());
    public static final String IMG = makeCardPath("ParityStrike.png");

    private static final CardRarity RARITY = CardRarity.BASIC;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 6;
    private static final int UPGRADE_PLUS_DMG = 3;
    private static final int MAGIC = 4;
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
        boolean damagedLastTurn = theBalance.patches.DamageTrackerPatch.DamageFields.tookDamageLastTurn.get(p);
        int totalDamage = damage;

        if (damagedLastTurn) {
            totalDamage += magicNumber;
        }

        AbstractDungeon.actionManager.addToBottom(
            new DamageAction(m, new DamageInfo(p, totalDamage, damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    }

    @Override
    public void applyPowers() {
        boolean damagedLastTurn = theBalance.patches.DamageTrackerPatch.DamageFields.tookDamageLastTurn.get(com.megacrit.cardcrawl.dungeons.AbstractDungeon.player);

        if (damagedLastTurn) {
            this.isDamageModified = true; // 让数字变绿
        }

        super.applyPowers();
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
