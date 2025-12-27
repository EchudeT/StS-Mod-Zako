package theBalance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 无限透支 - Infinite Overdraft
// 本回合费用为0，下回合跳过
public class InfiniteOverdraftPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("InfiniteOverdraftPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));

    private boolean skipNextTurn = false;

    public InfiniteOverdraftPower(final AbstractCreature owner) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        type = PowerType.BUFF;
        isTurnBased = false;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (skipNextTurn) {
            description = DESCRIPTIONS[1];  // "下回合跳过"
        } else {
            description = DESCRIPTIONS[0];  // "本回合费用为0"
        }
    }

    @Override
    public void onEnergyRecharge() {
        if (!skipNextTurn) {
            // 第一回合：给予大量能量使所有牌0费
            AbstractDungeon.player.gainEnergy(99);
            skipNextTurn = true;
            updateDescription();
        } else {
            // 第二回合：跳过（不给能量）
            AbstractDungeon.actionManager.addToBottom(
                new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new InfiniteOverdraftPower(owner);
    }
}
