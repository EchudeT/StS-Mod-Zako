package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

public class DexToStrConvert extends AbstractDynamicCard {
    public static final String ID = BalanceMod.makeID(DexToStrConvert.class.getSimpleName());
    public static final String IMG = BalanceMod.makeCardPath("DexToStrConvert.png");
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    public DexToStrConvert() {
        super(ID, IMG, 2, CardType.SKILL, COLOR, CardRarity.RARE, CardTarget.SELF);
        this.baseMagicNumber = this.magicNumber = 2; // 转换倍率
        this.exhaust = true; // 这种强力转换通常需要消耗
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int dexAmount = 0;
        AbstractPower dexPower = p.getPower(DexterityPower.POWER_ID);

        if (dexPower != null) {
            dexAmount = dexPower.amount;
            // 1. 移除所有敏捷
            AbstractDungeon.actionManager.addToBottom(
                    new RemoveSpecificPowerAction(p, p, DexterityPower.POWER_ID));

            // 2. 按倍率转化为力量
            int strToGain = dexAmount * this.magicNumber;
            AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(p, p, new StrengthPower(p, strToGain), strToGain));
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(1); // 升级后变为1比3转化
            initializeDescription();
        }
    }
}