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

public class MultilayerFortress extends AbstractDynamicCard {

    // 多层堡垒 - Multilayer Fortress
    // 消耗 X*10 金币。获得 X*2(3) 层多层护甲。

    public static final String ID = BalanceMod.makeID(MultilayerFortress.class.getSimpleName());
    public static final String IMG = makeCardPath("MultilayerFortress.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = -1;  // X cost
    private static final int MAGIC = 10;  // 每点能量消耗的金币
    private static final int MAGIC2 = 2;  // 每点能量获得的多层护甲
    private static final int UPGRADE_PLUS_ARMOR = 1;

    public MultilayerFortress() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
        defaultBaseSecondMagicNumber = baseDefaultSecondMagicNumber = defaultSecondMagicNumber = MAGIC2;
        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int effect = this.energyOnUse;
        int goldRequired = effect * magicNumber;

        if (effect > 0) {
            // 优先消耗战斗津贴，不足时消耗金币
            int goldNeeded = goldRequired;
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
            if (totalPaid >= goldRequired) {
                int armorGained = effect * defaultSecondMagicNumber;
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
