package theBalance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

public class TimeArbitragePower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("TimeArbitragePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));

    public TimeArbitragePower(final AbstractCreature owner, int amount) {
        this.name = powerStrings.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount; // 总计4层
        this.type = PowerType.BUFF;
        this.isTurnBased = true;
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
        updateDescription();
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer && this.amount >= 2) {
            // 优势阶段：在玩家结束回合时，强制设定房间跳过怪物回合
            this.flash();
            AbstractDungeon.getCurrRoom().skipMonsterTurn = true;
        }
    }

    @Override
    public void onEnergyRecharge() {
        // 当能量恢复时（每回合开始时），如果是惩罚阶段
        if (this.amount <= 1) {
            this.flash();
            // 将玩家能量强制设为 0
            AbstractDungeon.player.energy.use(999);
        }
    }

    @Override
    public void atStartOfTurn() {
        // 每个玩家回合开始，层数减 1
        this.amount--;

        // 动态改变 Power 类型，方便玩家视觉区分
        if (this.amount <= 2) {
            this.type = PowerType.DEBUFF;
        }

        if (this.amount <= 0) {
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
        }
        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (this.amount > 2) {
            // 描述：敌人还将跳过 X 个回合行动
            description = powerStrings.DESCRIPTIONS[0] + (this.amount - 2) + powerStrings.DESCRIPTIONS[1];
        } else {
            // 描述：惩罚阶段！能量将在下 X 个回合设为 0
            description = powerStrings.DESCRIPTIONS[2] + this.amount + powerStrings.DESCRIPTIONS[3];
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new TimeArbitragePower(owner, amount);
    }
}