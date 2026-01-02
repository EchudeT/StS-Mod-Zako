package theBalance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 暂时性免疫 - Temporary Immunity
// 下回合失去15%当前生命
public class TemporaryImmunityPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("TemporaryImmunityPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("SpecialPower84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("SpecialPower32.png"));

    public TemporaryImmunityPower(final AbstractCreature owner,int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;  // 15%
        type = PowerType.DEBUFF;
        isTurnBased = true;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public void atStartOfTurn() {
        flash();

        // 计算需要扣除的血量
        final int hpLoss = (int) (owner.maxHealth * (this.amount / 100.0f));

        if (hpLoss > 0) {
            AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
                @Override
                public void update() {
                    // 1. 如果当前生命值足够扣除 (非致死情况)
                    if (owner.currentHealth > hpLoss) {
                        // 直接修改数值，无视灵体、缓冲、格挡
                        owner.currentHealth -= (hpLoss - 1);
                        owner.healthBarUpdatedEvent();
                        addToTop(new LoseHPAction(owner, owner, 1));
                    }
                    // 2. 如果当前生命值不足 (致死情况)
                    else {
                        // 先把血锁到 1 (防止直接改到负数不触发复活)
                        owner.currentHealth = 1;
                        owner.healthBarUpdatedEvent();

                        addToTop(new LoseHPAction(owner, owner, 99999));
                    }

                    this.isDone = true;
                }
            });
        }

        // 移除自身
        AbstractDungeon.actionManager.addToBottom(
                new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
    }

    @Override
    public AbstractPower makeCopy() {
        return new TemporaryImmunityPower(owner, this.amount);
    }
}
