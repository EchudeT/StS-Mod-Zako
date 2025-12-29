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

// 对称美学 - Symmetrical Aesthetics
// 玩家拥有此能力时，你与所有敌人同步获得 Debuff。
public class SymmetricalAestheticsPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("SymmetricalAestheticsPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));

    public SymmetricalAestheticsPower(final AbstractCreature owner) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = -1; // -1 表示无层数，单纯标记
        type = PowerType.BUFF;
        isTurnBased = false;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public AbstractPower makeCopy() {
        return new SymmetricalAestheticsPower(owner);
    }
}