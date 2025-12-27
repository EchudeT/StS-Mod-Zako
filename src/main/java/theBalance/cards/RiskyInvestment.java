package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class RiskyInvestment extends AbstractDynamicCard {

    // 风险投资 - Risky Investment
    // 造成 10(14) 点伤害。抽 1 张牌，若非攻击牌，失去 3 点生命。

    public static final String ID = BalanceMod.makeID(RiskyInvestment.class.getSimpleName());
    public static final String IMG = makeCardPath("RiskyInvestment.png");

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 10;
    private static final int UPGRADE_PLUS_DMG = 4;
    private static final int MAGIC = 3;  // 失去的生命值

    public RiskyInvestment() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
        baseMagicNumber = magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
            new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(1, new AbstractGameAction() {
            @Override
            public void update() {
                if (DrawCardAction.drawnCards != null && !DrawCardAction.drawnCards.isEmpty()) {
                    for (AbstractCard c : DrawCardAction.drawnCards) {
                        if (c.type != CardType.ATTACK) {
                            AbstractDungeon.actionManager.addToTop(new LoseHPAction(p, p, magicNumber));
                            break;
                        }
                    }
                }
                this.isDone = true;
            }
        }));
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
