package theBalance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 坚固 - Solid
// 脆弱的反面，增加获得的格挡
public class SolidPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("SolidPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("DefensePower84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("DefensePower32.png"));
    public SolidPower(final AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        type = PowerType.BUFF;
        isTurnBased = true;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public float modifyBlock(float blockAmount) {
        // 增加33%获得的格挡（类似脆弱减少25%，这里反向）
        return blockAmount * 1.33f;
    }

    @Override
    public void atEndOfRound() {
        // 每回合结束减少1层
        if (this.amount == 0) {
            this.addToBot(new com.megacrit.cardcrawl.actions.common.ReducePowerAction(
                this.owner, this.owner, this.ID, 1));
        } else {
            this.amount--;
            if (this.amount == 0) {
                this.addToBot(new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(
                    this.owner, this.owner, this.ID));
            }
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new SolidPower(owner, amount);
    }
}
