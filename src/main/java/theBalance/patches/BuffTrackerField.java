package theBalance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

// 这个补丁会在 AbstractPlayer 类里添加一个 buffCount 变量
@SpirePatch(clz = AbstractPlayer.class, method = SpirePatch.CLASS)
public class BuffTrackerField {
    public static SpireField<Integer> buffCount = new SpireField<>(() -> 0);
    public static SpireField<Integer> atkCount = new SpireField<>(() -> 0);
}


