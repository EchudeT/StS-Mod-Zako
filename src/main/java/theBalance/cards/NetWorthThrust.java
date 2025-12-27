package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class NetWorthThrust extends AbstractDynamicCard {

    // 净值突刺 - Net Worth Thrust
    // 造成 6(9) 点伤害。本回合每获得过一次正面buff，重复一次。

    public static final String ID = BalanceMod.makeID(NetWorthThrust.class.getSimpleName());
    public static final String IMG = makeCardPath("NetWorthThrust.png");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 6;
    private static final int UPGRADE_PLUS_DMG = 3;

    public NetWorthThrust() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int buffGainCount = theBalance.patches.BuffTrackerField.buffCount.get(p);

        int totalHits = 1 + buffGainCount;

        for (int i = 0; i < totalHits; i++) {
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn),
                    AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        }
    }

    @Override
    public void applyPowers() {
        int buffGainCount = theBalance.patches.BuffTrackerField.buffCount.get(AbstractDungeon.player);
        this.magicNumber = this.baseMagicNumber = 1 + buffGainCount;
        super.applyPowers();
        this.rawDescription = cardStrings.DESCRIPTION + " (当前攻击 " + this.magicNumber + " 次)";
        initializeDescription();
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
