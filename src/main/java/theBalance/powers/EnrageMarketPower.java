package theBalance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.AngerPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makePowerPath;

// 激怒市场 - Enrage Market
// 当敌人通过激怒获得力量时，玩家也获得力量
public class EnrageMarketPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = BalanceMod.makeID("EnrageMarketPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power.png"));

    public EnrageMarketPower(final AbstractCreature owner, int amount) {
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
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        // 1. 激怒只对技能牌生效
        if (card.type == AbstractCard.CardType.SKILL) {

            int totalStrengthToGain = 0;

            // 2. 遍历所有活着的敌人
            if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().monsters != null) {
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    // 3. 检查敌人是否有 [激怒] 状态
                    if (!m.isDeadOrEscaped() && m.hasPower(AngerPower.POWER_ID)) {

                        // 逻辑：每有一个敌人触发激怒，玩家就获得 [this.amount] 点力量
                        totalStrengthToGain += this.amount;
                    }
                }
            }

            // 4. 如果有收益，施加力量
            if (totalStrengthToGain > 0) {
                this.flash(); // 闪烁激怒市场图标
                this.addToBot(new ApplyPowerAction(
                        this.owner,
                        this.owner,
                        new StrengthPower(this.owner, totalStrengthToGain),
                        totalStrengthToGain
                ));
            }
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new EnrageMarketPower(owner, amount);
    }
}
