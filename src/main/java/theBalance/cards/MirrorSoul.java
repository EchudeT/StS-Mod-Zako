package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import static theBalance.BalanceMod.makeCardPath;

public class MirrorSoul extends AbstractDynamicCard {

    // 镜像之魂 - Mirror Soul
    // 消耗。移除负面效果。复制指定敌人Buff给自己。 (升级：0费)

    public static final String ID = BalanceMod.makeID(MirrorSoul.class.getSimpleName());
    public static final String IMG = makeCardPath("MirrorSoul.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;

    public MirrorSoul() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (AbstractPower pow : p.powers) {
            if (pow.type == AbstractPower.PowerType.DEBUFF) {
                AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(p, p, pow.ID));
            }
        }

        if (m != null) {
            for (AbstractPower pow : m.powers) {
                if (pow.type == AbstractPower.PowerType.BUFF) {
                    AbstractPower copiedPower = null;

                    // 方法 A: 如果是支持 BaseMod 的 Mod Power，它们通常实现了 CloneablePowerInterface
                    if (pow instanceof basemod.interfaces.CloneablePowerInterface) {
                        copiedPower = ((basemod.interfaces.CloneablePowerInterface) pow).makeCopy();
                        copiedPower.owner = p; // 将拥有者改为玩家
                    }
                    else {
                        // 方法 B: 针对原版 Power，使用反射尝试调用标准构造函数 (Creature, int)
                        try {
                            // 尝试寻找 (拥有者, 层数) 这种构造函数
                            java.lang.reflect.Constructor<? extends AbstractPower> c =
                                    pow.getClass().getConstructor(com.megacrit.cardcrawl.core.AbstractCreature.class, int.class);
                            copiedPower = c.newInstance(p, pow.amount);
                        } catch (Exception e) {
                            // 如果没有标准构造函数（比如某些复杂的 Power），尝试 (拥有者) 构造函数
                            try {
                                java.lang.reflect.Constructor<? extends AbstractPower> c =
                                        pow.getClass().getConstructor(com.megacrit.cardcrawl.core.AbstractCreature.class);
                                copiedPower = c.newInstance(p);
                                copiedPower.amount = pow.amount;
                            } catch (Exception e2) {
                                // 实在复制不了的特殊 Power 只能手动 hardcode 或者放弃
                                System.out.println("无法复制能力: " + pow.ID);
                            }
                        }
                    }

                    if (copiedPower != null) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, copiedPower, pow.amount));
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
