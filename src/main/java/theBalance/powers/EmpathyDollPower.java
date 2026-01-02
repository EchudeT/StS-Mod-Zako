package theBalance.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// Helper power for EmpathyDoll relic
public class EmpathyDollPower extends AbstractPower {
    public static final String POWER_ID = BalanceMod.makeID("EmpathyDollPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("ZakoPower84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("ZakoPower32.png"));

    public EmpathyDollPower(AbstractCreature owner) {
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

    private boolean playerHasDebuff() {
        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (p.type == PowerType.DEBUFF) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        // Check if player is applying a debuff to an enemy while having debuffs
        if (source == AbstractDungeon.player && target instanceof AbstractMonster &&
            power.type == PowerType.DEBUFF && playerHasDebuff()) {
            flash();
            // Increase the power amount by 1
            power.amount += 1;
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }
}
