package theBalance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

public class CompoundInterestPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("CompoundInterestPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power32.png"));
    private int lastGoldAmount; // 用于记录上一帧的金币数
    private int lastAllowanceAmount; // 上一帧的津贴

    public CompoundInterestPower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount; // 这里 amount 代表百分比 (50 或 80)
        type = PowerType.BUFF;
        isTurnBased = false;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        // 初始化记录
        this.lastGoldAmount = AbstractDungeon.player.gold;
        this.lastAllowanceAmount = getCurrentAllowance();

        updateDescription();
    }

    @Override
    public void update(int slot) {
        super.update(slot);

        if (AbstractDungeon.player != null) {
            // 1. 检查金币变化
            int currentGold = AbstractDungeon.player.gold;
            int goldLost = 0;
            if (currentGold < lastGoldAmount) {
                goldLost = lastGoldAmount - currentGold;
            }
            lastGoldAmount = currentGold;

            // 2. 检查战斗津贴变化
            int currentAllowance = getCurrentAllowance();
            int allowanceConsumed = 0;
            if (currentAllowance < lastAllowanceAmount) {
                allowanceConsumed = lastAllowanceAmount - currentAllowance;
            }
            lastAllowanceAmount = currentAllowance;

            // 3. 触发效果 (合计消耗量)
            int totalSpend = goldLost + allowanceConsumed;
            if (totalSpend > 0) {
                triggerEffect(totalSpend);
            }
        }
    }

    // 辅助方法：获取当前的战斗津贴层数
    private int getCurrentAllowance() {
        // 假设战斗津贴的PowerID是 CombatGoldPower.POWER_ID
        // 如果你的津贴Power类名不一样，请在这里修改
        if (owner.hasPower(theBalance.powers.CombatGoldPower.POWER_ID)) {
            return owner.getPower(theBalance.powers.CombatGoldPower.POWER_ID).amount;
        }
        return 0;
    }

    // 战斗胜利时不触发（防止结算时金币变动导致奇怪的特效）
    @Override
    public void onVictory() {
        // Do nothing
    }

    private void triggerEffect(int totalLost) {
        // 计算格挡
        int blockGain = (totalLost * this.amount) / 100;

        if (blockGain > 0) {
            this.flash();
            // 放到队列顶端，立即生效
            AbstractDungeon.actionManager.addToTop(
                    new GainBlockAction(owner, owner, blockGain)
            );
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new CompoundInterestPower(owner, amount);
    }
}
