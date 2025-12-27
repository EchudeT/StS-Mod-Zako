package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class InventoryClearance extends AbstractDynamicCard {

    // 库存清理 - Inventory Clearance
    // 弃所有手牌。每弃一张抽 1 张牌并获 2(3) 点战斗津贴。

    public static final String ID = BalanceMod.makeID(InventoryClearance.class.getSimpleName());
    public static final String IMG = makeCardPath("InventoryClearance.png");

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int MAGIC = 2;  // 每张牌获得的津贴
    private static final int UPGRADE_PLUS_MAGIC = 1;

    public InventoryClearance() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int handSize = p.hand.size() - 1; // 减去这张牌本身

        // 弃所有手牌
        AbstractDungeon.actionManager.addToBottom(
            new DiscardAction(p, p, handSize, false));

        // 每弃一张抽1张牌并获得津贴
        for (int i = 0; i < handSize; i++) {
            AbstractDungeon.actionManager.addToBottom(
                new DrawCardAction(p, 1));
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new theBalance.powers.CombatGoldPower(p, magicNumber), magicNumber));
        }
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
