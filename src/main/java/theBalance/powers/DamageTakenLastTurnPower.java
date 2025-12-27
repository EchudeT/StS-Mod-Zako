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

// 上回合受伤追踪 - Damage Taken Last Turn
// 用于追踪上回合是否受到过伤害
public class DamageTakenLastTurnPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("DamageTakenLastTurnPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));

    private int damageTakenThisTurn = 0;  // 本回合受伤状态
    private int damageTakenLastTurn = 0;  // 上回合受伤状态

    public DamageTakenLastTurnPower(final AbstractCreature owner) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = 0;  // 显示上回合是否受伤：0=未受伤，1=受伤
        type = PowerType.BUFF;
        isTurnBased = false;
        priority = 99;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (amount > 0) {
            description = DESCRIPTIONS[0];  // "上回合受到过伤害"
        } else {
            description = DESCRIPTIONS[1];  // "上回合未受到伤害"
        }
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        // 当本回合受到伤害时，标记本回合状态
        if (damageAmount > 0 && info.type != DamageInfo.DamageType.HP_LOSS) {
            this.damageTakenThisTurn = 1;
        }
        return damageAmount;
    }

    @Override
    public void atStartOfTurn() {
        // 回合开始时，将上回合的状态更新为前一回合的状态
        this.damageTakenLastTurn = this.damageTakenThisTurn;
        this.amount = this.damageTakenLastTurn;
        // 重置本回合状态
        this.damageTakenThisTurn = 0;
        updateDescription();
    }

    // 提供公共方法检查上回合是否受伤
    public boolean wasDamagedLastTurn() {
        return this.damageTakenLastTurn > 0;
    }

    @Override
    public AbstractPower makeCopy() {
        return new DamageTakenLastTurnPower(owner);
    }
}
