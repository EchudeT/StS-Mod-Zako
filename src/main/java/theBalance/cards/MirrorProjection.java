package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import java.lang.reflect.Field;
import static theBalance.BalanceMod.makeCardPath;

public class MirrorProjection extends AbstractDynamicCard {

    // 镜像投射 - Mirror Projection
    // 造成等同于敌人意图伤害 1.2(1.5) 倍的伤害。

    public static final String ID = BalanceMod.makeID(MirrorProjection.class.getSimpleName());
    public static final String IMG = makeCardPath("MirrorProjection.png");

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 2;

    public MirrorProjection() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        baseMagicNumber = magicNumber = 120;  // 倍率 * 100 (1.2 -> 120)
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int enemyDamage = 0;

        // 获取敌人意图伤害
        if (m != null && m.intent != null) {
            try {
                Field moveInfo = AbstractMonster.class.getDeclaredField("move");
                moveInfo.setAccessible(true);
                EnemyMoveInfo info = (EnemyMoveInfo) moveInfo.get(m);
                if (info != null) {
                    int multi = Math.max(1, info.multiplier);
                    enemyDamage = info.baseDamage * multi;
                }
            } catch (Exception e) {
                // 如果无法获取，使用默认值
                enemyDamage = 10;
            }
        }

        // 计算伤害 (倍率应用)
        int totalDamage = (int) (enemyDamage * magicNumber / 100.0f);

        AbstractDungeon.actionManager.addToBottom(
            new DamageAction(m, new DamageInfo(p, totalDamage, DamageInfo.DamageType.NORMAL),
                AbstractGameAction.AttackEffect.SLASH_HEAVY));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(30);  // 1.2 -> 1.5 (升级增加0.3)
            initializeDescription();
        }
    }
}
