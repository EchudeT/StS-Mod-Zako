package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.TheDefault;

import static theBalance.BalanceMod.makeCardPath;

public class NetWorthThrust extends AbstractDynamicCard {

    // 净值突刺 - Net Worth Thrust
    // 造成 6(9) 点伤害。本回合每获得过一次正面buff，重复一次。

    public static final String ID = BalanceMod.makeID(NetWorthThrust.class.getSimpleName());
    public static final String IMG = makeCardPath("NetWorthThrust.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = TheDefault.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 6;
    private static final int UPGRADE_PLUS_DMG = 3;

    public NetWorthThrust() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 首次使用时应用追踪Power
        if (!p.hasPower(theBalance.powers.NetWorthTrackingPower.POWER_ID)) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new theBalance.powers.NetWorthTrackingPower(p), -1));
        }

        // 获取本回合获得buff的次数
        int buffGainCount = 0;
        if (p.hasPower(theBalance.powers.NetWorthTrackingPower.POWER_ID)) {
            buffGainCount = p.getPower(theBalance.powers.NetWorthTrackingPower.POWER_ID).amount;
        }

        // 至少攻击一次，然后根据buff获得次数额外攻击
        int attackTimes = 1 + buffGainCount;

        for (int i = 0; i < attackTimes; i++) {
            AbstractDungeon.actionManager.addToBottom(
                new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn),
                    AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
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
