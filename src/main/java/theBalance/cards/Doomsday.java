package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class Doomsday extends AbstractDynamicCard {

    // 末日 - Doomsday
    // 对全场（含自己）造成 20(30) 点伤害 2 次。

    public static final String ID = BalanceMod.makeID(Doomsday.class.getSimpleName());
    public static final String IMG = makeCardPath("Doomsday.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ALL;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 3;
    private static final int DAMAGE = 20;
    private static final int UPGRADE_PLUS_DMG = 10;
    private static final int MAGIC = 2;

    public Doomsday() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
        baseMagicNumber = magicNumber = MAGIC;
        isMultiDamage = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < magicNumber; i++) {
            // 对所有敌人造成伤害
            AbstractDungeon.actionManager.addToBottom(
                new DamageAllEnemiesAction(p, multiDamage, damageTypeForTurn,
                    AbstractGameAction.AttackEffect.FIRE));

            // 对玩家自己造成伤害
            AbstractDungeon.actionManager.addToBottom(
                new DamageAction(p, new DamageInfo(p, damage, DamageInfo.DamageType.THORNS),
                    AbstractGameAction.AttackEffect.FIRE));
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
