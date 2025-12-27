package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class DevilsOption extends AbstractDynamicCard {

    // 恶魔期权 - Devil's Option
    // 获得 12(20) 金币。将两张[懊悔]放入弃牌堆。

    public static final String ID = BalanceMod.makeID(DevilsOption.class.getSimpleName());
    public static final String IMG = makeCardPath("DevilsOption.png");

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 0;
    private static final int MAGIC = 12;  // 获得的金币
    private static final int UPGRADE_PLUS_MAGIC = 8;

    public DevilsOption() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 获得战斗津贴
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(p, p, new theBalance.powers.CombatGoldPower(p, magicNumber), magicNumber));

        // 使用Dazed作为茫然的替代
        AbstractDungeon.actionManager.addToBottom(
            new MakeTempCardInDiscardAction(new Dazed(), 2));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            initializeDescription();
        }
    }
}
