package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.BufferPower;
import com.megacrit.cardcrawl.powers.ThornsPower;
import theBalance.BalanceMod;
import theBalance.characters.TheDefault;

import static theBalance.BalanceMod.makeCardPath;

public class MirrorShield extends AbstractDynamicCard {

    // 镜像之盾 - Mirror Shield
    // 消耗。双方获得 1(2) 层缓冲，3层荆棘。

    public static final String ID = BalanceMod.makeID(MirrorShield.class.getSimpleName());
    public static final String IMG = makeCardPath("MirrorShield.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = TheDefault.Enums.COLOR_GRAY;

    private static final int COST = 2;
    private static final int MAGIC = 1;  // 缓冲层数
    private static final int UPGRADE_PLUS_MAGIC = 1;
    private static final int MAGIC2 = 3;  // 荆棘层数

    public MirrorShield() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
        baseDefaultSecondMagicNumber = defaultSecondMagicNumber = MAGIC2;
        this.exhaust = true;
        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new BufferPower(p, magicNumber), magicNumber));

        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new ThornsPower(p, defaultSecondMagicNumber), defaultSecondMagicNumber));

        if (m != null) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(m, p, new BufferPower(m, magicNumber), magicNumber));

            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(m, p, new ThornsPower(m, defaultSecondMagicNumber), defaultSecondMagicNumber));
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
