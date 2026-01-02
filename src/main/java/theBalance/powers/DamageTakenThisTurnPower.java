package theBalance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 本回合受伤追踪 - Damage Taken This Turn
// 用于追踪本回合是否受到过伤害
public class DamageTakenThisTurnPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("DamageTakenThisTurnPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("AttackPower84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("AttackPower32.png"));

    public DamageTakenThisTurnPower(final AbstractCreature owner) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = 0;  // 0表示本回合未受伤，>0表示受伤
        type = PowerType.BUFF;
        isTurnBased = false;
        priority = 99;  // 高优先级，确保在其他效果之前触发

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (amount > 0) {
            description = DESCRIPTIONS[0];  // "本回合受到过伤害"
        } else {
            description = DESCRIPTIONS[1];  // "本回合未受到伤害"
        }
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        // 当受到伤害时，标记为已受伤
        if (damageAmount > 0 && info.type != DamageInfo.DamageType.HP_LOSS) {
            this.amount = 1;
            updateDescription();
        }
        return damageAmount;
    }

    @Override
    public void atEndOfRound() {
        // 回合结束时重置
        this.amount = 0;
        updateDescription();
    }

    @Override
    public AbstractPower makeCopy() {
        return new DamageTakenThisTurnPower(owner);
    }
}
