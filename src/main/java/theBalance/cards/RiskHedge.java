package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import theBalance.BalanceMod;
import theBalance.characters.Zako;
import theBalance.powers.CombatGoldPower;

import static theBalance.BalanceMod.makeCardPath;

public class RiskHedge extends AbstractDynamicCard {

    // 风险对冲(杠杆) - Risk Hedge
    // 消耗金币。每 10 金币获 1(2) 层多层护甲。赛后不返还。

    public static final String ID = BalanceMod.makeID(RiskHedge.class.getSimpleName());
    public static final String IMG = makeCardPath("RiskHedge.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = -1;  // X cost
    private static final int MAGIC = 10;  // 每多少金币
    private static final int MAGIC2 = 1;  // 护甲层数
    private static final int UPGRADE_PLUS_ARMOR = 1;

    public RiskHedge() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
        defaultBaseSecondMagicNumber = baseDefaultSecondMagicNumber = defaultSecondMagicNumber = MAGIC2;
        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int effect = this.energyOnUse;

        // 优先消耗战斗津贴，不足时消耗金币
        int goldNeeded = effect * 10;
        int fromCombatGold = 0;
        int fromRegularGold = 0;

        // 检查战斗津贴
        if (p.hasPower(CombatGoldPower.POWER_ID)) {
            CombatGoldPower combatGoldPower = (CombatGoldPower) p.getPower(CombatGoldPower.POWER_ID);
            fromCombatGold = Math.min(combatGoldPower.amount, goldNeeded);
            goldNeeded -= fromCombatGold;
        }

        // 检查剩余金币
        if (goldNeeded > 0) {
            fromRegularGold = Math.min(p.gold, goldNeeded);
        }

        // 计算总共可以支付的金币
        int totalPaid = fromCombatGold + fromRegularGold;

        // 消耗战斗津贴
        if (fromCombatGold > 0) {
            AbstractDungeon.actionManager.addToBottom(
                new ReducePowerAction(p, p, CombatGoldPower.POWER_ID, fromCombatGold));
        }

        // 消耗金币
        if (fromRegularGold > 0) {
            p.loseGold(fromRegularGold);
        }

        // 根据实际支付金额计算护甲
        if (totalPaid > 0) {
            int armorGained = (totalPaid / magicNumber) * defaultSecondMagicNumber;
            if (armorGained > 0) {
                AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(p, p, new PlatedArmorPower(p, armorGained), armorGained));
            }
        }

        if (!this.freeToPlayOnce) {
            p.energy.use(EnergyPanel.totalCount);
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDefaultSecondMagicNumber(UPGRADE_PLUS_ARMOR);
            initializeDescription();
        }
    }
}
