package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.characters.TheDefault;

import static theBalance.BalanceMod.makeCardPath;

public class MirrorSoul extends AbstractDynamicCard {

    // 镜像之魂 - Mirror Soul
    // 消耗。移除负面效果。复制指定敌人Buff给自己。 (升级：0费)

    public static final String ID = BalanceMod.makeID(MirrorSoul.class.getSimpleName());
    public static final String IMG = makeCardPath("MirrorSoul.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = TheDefault.Enums.COLOR_GRAY;

    private static final int COST = 1;

    public MirrorSoul() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 移除自己的负面效果
        p.powers.removeIf(power -> power.type == AbstractPower.PowerType.DEBUFF);

        // 复制敌人的Buff
        if (m != null) {
            for (AbstractPower power : m.powers) {
                if (power.type == AbstractPower.PowerType.BUFF) {
                    try {
                        AbstractPower copiedPower = power.getClass().newInstance();
                        AbstractDungeon.actionManager.addToBottom(
                            new ApplyPowerAction(p, p, copiedPower, power.amount));
                    } catch (Exception e) {
                        // 忽略无法复制的power
                    }
                }
            }
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(0);
            initializeDescription();
        }
    }
}
