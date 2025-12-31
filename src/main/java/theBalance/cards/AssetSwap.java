package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class AssetSwap extends AbstractDynamicCard {

    // 资产置换 - Asset Swap
    // 造成 9(12) 点伤害。交换你与敌人的易伤和虚弱层数。

    public static final String ID = BalanceMod.makeID(AssetSwap.class.getSimpleName());
    public static final String IMG = makeCardPath("AssetSwap.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int DAMAGE = 9;
    private static final int UPGRADE_PLUS_DMG = 3;

    public AssetSwap() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseDamage = DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
            new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

        // 获取双方数据
        int pWeak = 0, pVuln = 0, mWeak = 0, mVuln = 0;
        if (p.hasPower(WeakPower.POWER_ID)) pWeak = p.getPower(WeakPower.POWER_ID).amount;
        if (p.hasPower(VulnerablePower.POWER_ID)) pVuln = p.getPower(VulnerablePower.POWER_ID).amount;
        if (m.hasPower(WeakPower.POWER_ID)) mWeak = m.getPower(WeakPower.POWER_ID).amount;
        if (m.hasPower(VulnerablePower.POWER_ID)) mVuln = m.getPower(VulnerablePower.POWER_ID).amount;

        // 移除所有旧状态
        if (pWeak > 0) addToBot(new RemoveSpecificPowerAction(p, p, WeakPower.POWER_ID));
        if (pVuln > 0) addToBot(new RemoveSpecificPowerAction(p, p, VulnerablePower.POWER_ID));
        if (mWeak > 0) addToBot(new RemoveSpecificPowerAction(m, p, WeakPower.POWER_ID));
        if (mVuln > 0) addToBot(new RemoveSpecificPowerAction(m, p, VulnerablePower.POWER_ID));

        // 交叉施加 (玩家获得怪物的层数，怪物获得玩家的层数)
        if (mWeak > 0) addToBot(new ApplyPowerAction(p, p, new WeakPower(p, mWeak, false), mWeak));
        if (mVuln > 0) addToBot(new ApplyPowerAction(p, p, new VulnerablePower(p, mVuln, false), mVuln));
        if (pWeak > 0) addToBot(new ApplyPowerAction(m, p, new WeakPower(m, pWeak, false), pWeak));
        if (pVuln > 0) addToBot(new ApplyPowerAction(m, p, new VulnerablePower(m, pVuln, false), pVuln));
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
