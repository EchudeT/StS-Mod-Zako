package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.*;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import java.lang.reflect.Constructor;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 涂鸦画纸 - Doodle Paper
// 机制修改：每回合第一次获得负面状态时，将该状态(1层)施加给全体敌人
public class DoodlePaper extends CustomRelic {
    public static final String ID = BalanceMod.makeID("DoodlePaper");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("DoodlePaper.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    // 记录本回合是否触发过
    public boolean triggeredThisTurn = false;

    public DoodlePaper() {
        super(ID, IMG, OUTLINE, RelicTier.COMMON, LandingSound.FLAT);
    }

    @Override
    public void atTurnStart() {
        // 回合开始重置
        this.triggeredThisTurn = false;
        this.grayscale = false;
    }

    @Override
    public void onVictory() {
        this.triggeredThisTurn = false;
        this.grayscale = false;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    public static AbstractPower createPowerCopy(AbstractPower original, AbstractCreature target) {
        int amount = original.amount;
        String id = original.ID;

        // 1. 常见 Debuff 硬编码 (最稳妥)
        if (VulnerablePower.POWER_ID.equals(id)) return new VulnerablePower(target, amount, false);
        if (WeakPower.POWER_ID.equals(id)) return new WeakPower(target, amount, false);
        if (FrailPower.POWER_ID.equals(id)) return new FrailPower(target, amount, false);
        if (StrengthPower.POWER_ID.equals(id)) return new StrengthPower(target, amount); // 力量作为Debuff时通常amount是负数，直接传即可
        if (DexterityPower.POWER_ID.equals(id)) return new DexterityPower(target, amount);
        if (ConstrictedPower.POWER_ID.equals(id)) return new ConstrictedPower(target, AbstractDungeon.player, amount);
        if (PoisonPower.POWER_ID.equals(id)) return new PoisonPower(target, AbstractDungeon.player, amount);

        // 2. 反射尝试
        Class<? extends AbstractPower> clazz = original.getClass();

        // 尝试 (Owner, Amount, boolean isSourceMonster) - 针对易伤/虚弱等
        try {
            Constructor<?> c = clazz.getConstructor(AbstractCreature.class, int.class, boolean.class);
            return (AbstractPower) c.newInstance(target, amount, false);
        } catch (Exception ignored) {}

        // 尝试 (Owner, Amount) - 最常见的通用构造函数
        try {
            Constructor<?> c = clazz.getConstructor(AbstractCreature.class, int.class);
            return (AbstractPower) c.newInstance(target, amount);
        } catch (Exception ignored) {}

        // 尝试 (Owner, Source, Amount) - 针对中毒/缠绕
        try {
            Constructor<?> c = clazz.getConstructor(AbstractCreature.class, AbstractCreature.class, int.class);
            return (AbstractPower) c.newInstance(target, AbstractDungeon.player, amount);
        } catch (Exception ignored) {}

        System.out.println("TheBalance: 无法复制 Power " + id);
        return null;
    }
}