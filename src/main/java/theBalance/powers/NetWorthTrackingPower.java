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

// 净值突刺追踪 - Net Worth Thrust Tracking
// 追踪本回合获得了多少次正面buff
public class NetWorthTrackingPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("NetWorthTrackingPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power32.png"));


    public NetWorthTrackingPower(AbstractCreature owner) {
        this.name = powerStrings.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 0; // 记录次数
        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
        updateDescription();

    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (target == this.owner && power.type == PowerType.BUFF) {
            this.amount++; // 计数增加
            this.flash();
        }
    }

    // --- 隐藏逻辑开始 ---
    @Override
    public void renderIcons(com.badlogic.gdx.graphics.g2d.SpriteBatch sb, float x, float y, com.badlogic.gdx.graphics.Color c) {
    }

    @Override
    public void renderAmount(com.badlogic.gdx.graphics.g2d.SpriteBatch sb, float x, float y, com.badlogic.gdx.graphics.Color c) {
    }
    // --- 隐藏逻辑结束 ---

    @Override
    public void updateDescription() {
        this.description = ""; // 隐藏的 Power 不需要描述
    }

    // 每个回合开始时重置计数（如果你希望是“本回合获得过”）
    @Override
    public void atStartOfTurn() {
        this.amount = 0;
        updateDescription();
    }


    @Override
    public AbstractPower makeCopy() {
        return new NetWorthTrackingPower(owner);
    }
}
