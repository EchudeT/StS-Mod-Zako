package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import theBalance.BalanceMod;
import theBalance.characters.TheDefault;

import static theBalance.BalanceMod.makeCardPath;

public class LeverageStrike extends AbstractDynamicCard {

    // 杠杆打击 - Leverage Strike
    // 造成 8(11) 点伤害 X 次。你获得 X 层虚弱。

    public static final String ID = BalanceMod.makeID(LeverageStrike.class.getSimpleName());
    public static final String IMG = makeCardPath("LeverageStrike.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = TheDefault.Enums.COLOR_GRAY;

    private static final int COST = -1;  // X cost
    private static final int DAMAGE = 8;
    private static final int UPGRADE_PLUS_DMG = 3;

    public LeverageStrike() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int effect = this.energyOnUse;

        for (int i = 0; i < effect; i++) {
            AbstractDungeon.actionManager.addToBottom(
                new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn),
                    AbstractGameAction.AttackEffect.SLASH_HEAVY));
        }

        if (effect > 0) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new WeakPower(p, effect, false), effect));
        }

        if (!this.freeToPlayOnce) {
            p.energy.use(EnergyPanel.totalCount);
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
