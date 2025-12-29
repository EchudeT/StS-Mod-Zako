package theBalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

@SpirePatch(
        clz = AbstractCreature.class,
        method = "addPower"
)
public class testPatch {
    @SpirePostfixPatch
    public static void Postfix(AbstractCreature __instance, AbstractPower power) {
        System.out.println(">>> 成功捕获 addPower! 目标: " + __instance.name + " | 获得能力: " + power.name);
    }
}
