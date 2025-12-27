package theBalance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 流动资产 - Liquid Assets
// 格挡不再消失，每当触发格挡，失去等量战斗津贴
public class LiquidAssetsPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("LiquidAssetsPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));

    public LiquidAssetsPower(final AbstractCreature owner) {
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
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public void atEndOfRound() {
        // 格挡不消失 - 什么也不做，保留格挡
        // 通常游戏会在回合结束时清除格挡，这个Power阻止这一行为
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        // 当格挡触发时，减少战斗津贴
        if (owner.currentBlock > 0 && damageAmount > 0) {
            int blockUsed = Math.min(owner.currentBlock, damageAmount);

            // 减少等量战斗津贴
            CombatGoldPower combatGold = (CombatGoldPower) owner.getPower(CombatGoldPower.POWER_ID);
            if (combatGold != null) {
                combatGold.reducePower(blockUsed);
            }
        }
        return damageAmount;
    }

    @Override
    public AbstractPower makeCopy() {
        return new LiquidAssetsPower(owner);
    }
}
