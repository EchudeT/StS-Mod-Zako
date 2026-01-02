package theBalance.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 焦点中心延迟 - Magnifying Lens Delayed
// 回合开始时：获得 X 点力量，然后移除自身
public class MagnifyingLensDelayedPower extends AbstractPower {
    public static final String POWER_ID = BalanceMod.makeID("MagnifyingLensDelayedPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("AttackPower84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("AttackPower32.png"));

    public MagnifyingLensDelayedPower(final AbstractCreature owner, final int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;

        this.isTurnBased = true;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        // description: 下回合获得 !A! 点力量。
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void atStartOfTurn() {
        flash();
        // 给拥有者（敌人）施加力量
        addToBot(new ApplyPowerAction(this.owner, this.owner,
                new StrengthPower(this.owner, this.amount), this.amount));

        // 移除自身
        addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
    }
}