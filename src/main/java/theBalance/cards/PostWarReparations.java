package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AngerPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;
import theBalance.powers.RegenerationPower;

import static theBalance.BalanceMod.makeCardPath;

public class PostWarReparations extends AbstractDynamicCard {

    // 战后赔偿 - Post-War Reparations
    // 消耗。获得 3(4) 点不衰减的再生。全体敌人获得 1 点激怒。

    public static final String ID = BalanceMod.makeID(PostWarReparations.class.getSimpleName());
    public static final String IMG = makeCardPath("PostWarReparations.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int MAGIC = 3;
    private static final int UPGRADE_PLUS_REGEN = 1;
    private static final int MAGIC2 = 1;

    public PostWarReparations() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
        baseDefaultSecondMagicNumber = defaultSecondMagicNumber = MAGIC2;
        initializeDescription();
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new RegenerationPower(p, magicNumber), magicNumber));

        for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!monster.isDeadOrEscaped()) {
                AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(monster, p, new AngerPower(monster, defaultSecondMagicNumber), defaultSecondMagicNumber));
            }
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_PLUS_REGEN);
            initializeDescription();
        }
    }
}
