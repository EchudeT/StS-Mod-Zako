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

// 稳健经营：不再获得力量，每回合开始获得敏捷
public class StableManagementPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("StableManagementPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("DefensePower84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("DefensePower32.png"));

    public StableManagementPower(AbstractCreature owner, int amount) {
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
    public void atStartOfTurn() {
        flash();
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(owner, owner, new DexterityPower(owner, amount), amount));
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        // 核心逻辑：监听力量获取
        // 1. 目标是玩家
        // 2. 获得的是力量 (StrengthPower)
        // 3. 数值大于0 (避免死循环，因为我们下面要扣除力量，那是负数)
        if (target == this.owner && StrengthPower.POWER_ID.equals(power.ID) && power.amount > 0) {
            flash();

            // 1. 抵消获得的力量 (施加等量的负力量)
            addToBot(new ApplyPowerAction(owner, owner,
                    new StrengthPower(owner, -power.amount), -power.amount));

            // 2. 获得等量的敏捷 (转化逻辑)
            addToBot(new ApplyPowerAction(owner, owner,
                    new DexterityPower(owner, power.amount), power.amount));
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public AbstractPower makeCopy() {
        return new StableManagementPower(owner, amount);
    }
}
