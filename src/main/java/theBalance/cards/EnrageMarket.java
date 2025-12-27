package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AngerPower;
import theBalance.BalanceMod;
import theBalance.characters.TheDefault;

import static theBalance.BalanceMod.makeCardPath;

public class EnrageMarket extends AbstractDynamicCard {

    // 激怒市场 - Enrage Market
    // 所有敌人获得激怒。每当敌人通过激怒获得力量，你获得 1(2) 点力量。

    public static final String ID = BalanceMod.makeID(EnrageMarket.class.getSimpleName());
    public static final String IMG = makeCardPath("EnrageMarket.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;
    private static final CardType TYPE = CardType.POWER;
    public static final CardColor COLOR = TheDefault.Enums.COLOR_GRAY;

    private static final int COST = 2;
    private static final int MAGIC = 1;  // 玩家获得的力量
    private static final int UPGRADE_PLUS_MAGIC = 1;
    private static final int MAGIC2 = 1;  // 敌人获得的激怒层数

    public EnrageMarket() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
        baseDefaultSecondMagicNumber = defaultSecondMagicNumber = MAGIC2;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 给所有敌人施加激怒
        for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!monster.isDeadOrEscaped()) {
                AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(monster, p, new AngerPower(monster, defaultSecondMagicNumber), defaultSecondMagicNumber));
            }
        }

        // 给玩家施加激怒市场Power
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new theBalance.powers.EnrageMarketPower(p, magicNumber), magicNumber));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            // upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            upgradeBaseCost(1);
            initializeDescription();
        }
    }
}
