package theBalance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 零和博弈 - Zero Sum Game
// 偷取敌人力量，回合结束归还
public class ZeroSumGamePower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("ZeroSumGamePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("SpecialPower84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("SpecialPower32.png"));

    private AbstractMonster target;

    public ZeroSumGamePower(final AbstractCreature owner, final AbstractMonster target, final int stolenStr) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.target = target;
        this.amount = stolenStr;
        type = PowerType.BUFF;
        isTurnBased = true;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public void atEndOfRound() {
        flash();
        // 归还力量给敌人
        if (target != null && !target.isDeadOrEscaped()) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(target, owner, new StrengthPower(target, amount), amount));
        }
        // 减少玩家力量
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(owner, owner, new StrengthPower(owner, -amount), -amount));
        // 移除此Power
        AbstractDungeon.actionManager.addToBottom(
            new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
    }

    @Override
    public AbstractPower makeCopy() {
        return new ZeroSumGamePower(owner, target, amount);
    }
}
