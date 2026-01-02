package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import theBalance.BalanceMod;
import theBalance.characters.Zako;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static theBalance.BalanceMod.makeCardPath;

public class MirrorSoul extends AbstractDynamicCard {

    // 镜像之魂 - Mirror Soul
    public static final String ID = BalanceMod.makeID(MirrorSoul.class.getSimpleName());
    public static final String IMG = makeCardPath("MirrorSoul.png");

    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;

    // =================================================================================
    // ★ 黑名单列表：在此处添加所有会导致崩溃或无意义的怪物专属能力 ID
    // =================================================================================
    private static final Set<String> BLACKLIST = new HashSet<>(Arrays.asList(
            CurlUpPower.POWER_ID,       // 蜷缩 (会导致崩溃，除非你有 Patch)
            TimeWarpPower.POWER_ID,     // 时光扭曲 (UI显示问题，逻辑混乱)
            "Split",                    // 史莱姆分裂
            MinionPower.POWER_ID,       // 爪牙 (玩家变成爪牙没意义)
            ModeShiftPower.POWER_ID,    // 形态转换 (守护者)
            "Unawakened",               // 未觉醒 (觉醒者)
            "Life Link",                //以此类推，黑灵链接
            "Fading",                   // 瞬逝 (你不想玩家回合结束就死吧)
            InvinciblePower.POWER_ID,   // 无敌 (心脏的锁血，虽然玩家拿了很强，但UI可能会坏)
            BeatOfDeathPower.POWER_ID,  // 死之律动 (这是Buff? 它是正面效果吗? 通常不算Buff但以防万一)
            "Shifting",                 // 守护者变身
            "Anger",                    // 老头的愤怒 (可能会导致贴图错误)
            "Spore Cloud"               // 蘑菇鼠的死后爆炸
    ));

    public MirrorSoul() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 移除自身的负面效果
        for (AbstractPower pow : p.powers) {
            if (pow.type == AbstractPower.PowerType.DEBUFF) {
                addToBot(new RemoveSpecificPowerAction(p, p, pow.ID));
            }
        }

        // 2. 复制敌人的正面效果
        if (m != null) {
            for (AbstractPower pow : m.powers) {
                // 只复制 BUFF
                if (pow.type == AbstractPower.PowerType.BUFF) {

                    // ★ 核心逻辑：黑名单检查 ★
                    if (BLACKLIST.contains(pow.ID)) {
                        System.out.println("MirrorSoul: 跳过黑名单能力 -> " + pow.ID + " (" + pow.name + ")");
                        continue;
                    }

                    AbstractPower copiedPower = null;

                    // 方法 A: Mod通用接口复制
                    if (pow instanceof basemod.interfaces.CloneablePowerInterface) {
                        copiedPower = ((basemod.interfaces.CloneablePowerInterface) pow).makeCopy();
                        copiedPower.owner = p;
                    }
                    else {
                        // 方法 B: 反射复制
                        try {
                            // 尝试 (Owner, Amount)
                            java.lang.reflect.Constructor<? extends AbstractPower> c =
                                    pow.getClass().getConstructor(AbstractCreature.class, int.class);
                            copiedPower = c.newInstance(p, pow.amount);
                        } catch (Exception e) {
                            try {
                                // 尝试 (Owner)
                                java.lang.reflect.Constructor<? extends AbstractPower> c =
                                        pow.getClass().getConstructor(AbstractCreature.class);
                                copiedPower = c.newInstance(p);
                                copiedPower.amount = pow.amount;
                            } catch (Exception e2) {
                                // 尝试 (Owner, Amount, boolean) - 针对某些特殊 Power
                                try {
                                    java.lang.reflect.Constructor<? extends AbstractPower> c =
                                            pow.getClass().getConstructor(AbstractCreature.class, int.class, boolean.class);
                                    copiedPower = c.newInstance(p, pow.amount, false);
                                } catch (Exception e3) {
                                    System.out.println("MirrorSoul: 无法复制能力 (构造函数不匹配) -> " + pow.ID);
                                }
                            }
                        }
                    }

                    // 最终应用
                    if (copiedPower != null) {
                        addToBot(new ApplyPowerAction(p, p, copiedPower, pow.amount));
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