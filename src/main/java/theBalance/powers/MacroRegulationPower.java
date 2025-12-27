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
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 宏观调控：力量和敏捷永远取两者的平均值
public class MacroRegulationPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("MacroRegulationPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power32.png"));

    public MacroRegulationPower(AbstractCreature owner) {
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
    public void atEndOfTurn(boolean isPlayer) {
        balanceAttributes();
    }

    @Override
    public void onInitialApplication() {
        balanceAttributes();
    }

    private void balanceAttributes() {
        int str = 0;
        int dex = 0;

        if (owner.hasPower("Strength")) {
            str = owner.getPower("Strength").amount;
        }
        if (owner.hasPower("Dexterity")) {
            dex = owner.getPower("Dexterity").amount;
        }

        int avg = (str + dex + 1) / 2;
        int strDiff = avg - str;
        int dexDiff = avg - dex;

        if (strDiff != 0) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(owner, owner, new StrengthPower(owner, strDiff), strDiff));
        }
        if (dexDiff != 0) {
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(owner, owner, new DexterityPower(owner, dexDiff), dexDiff));
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public AbstractPower makeCopy() {
        return new MacroRegulationPower(owner);
    }
}
