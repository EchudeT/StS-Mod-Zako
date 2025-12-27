package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;
import theBalance.powers.RegenerationPower;

import static theBalance.BalanceMod.makeCardPath;

public class PostWarReparations extends AbstractDynamicCard {

    // 战后赔偿 - Post-War Reparations
    // 消耗。获得 3(4) 点再生。全体敌人获得 2 点力量。

    public static final String ID = BalanceMod.makeID(PostWarReparations.class.getSimpleName());
    public static final String IMG = makeCardPath("PostWarReparations.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int MAGIC = 3;  // 再生
    private static final int UPGRADE_PLUS_REGEN = 1;
    private static final int MAGIC2 = 2;  // 敌人力量

    public PostWarReparations() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
        baseDefaultSecondMagicNumber = defaultSecondMagicNumber = MAGIC2;
        initializeDescription();
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 玩家获得再生
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new RegenerationPower(p, magicNumber), magicNumber));

        // 所有敌人获得力量
        for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!monster.isDeadOrEscaped()) {
                AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(monster, p, new StrengthPower(monster, defaultSecondMagicNumber), defaultSecondMagicNumber));
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
