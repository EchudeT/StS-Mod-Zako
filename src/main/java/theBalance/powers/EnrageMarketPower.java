package theBalance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.AngerPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 激怒市场 - Enrage Market
// 当敌人通过激怒获得力量时，玩家也获得力量
public class EnrageMarketPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("EnrageMarketPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));

    public EnrageMarketPower(final AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        type = PowerType.BUFF;
        isTurnBased = false;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        // 检查是否是敌人通过激怒获得力量
        if (power.ID.equals(StrengthPower.POWER_ID) &&
            target != owner &&
            target.hasPower(AngerPower.POWER_ID) &&
            power.amount > 0) {

            flash();
            // 玩家获得相同数量的力量乘以激怒市场的倍率
            int strengthGain = this.amount * power.amount;
            AbstractDungeon.actionManager.addToTop(
                new ApplyPowerAction(owner, owner, new StrengthPower(owner, strengthGain), strengthGain));
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new EnrageMarketPower(owner, amount);
    }
}
