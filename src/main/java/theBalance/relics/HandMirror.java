package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 手持小镜子 - Hand Mirror
// 战斗开始时，选择一名敌人。该敌人每有 2 点力量，你获得 1 点敏捷
public class HandMirror extends CustomRelic {
    public static final String ID = BalanceMod.makeID("HandMirror");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("placeholder_relic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    public HandMirror() {
        super(ID, IMG, OUTLINE, RelicTier.COMMON, LandingSound.MAGICAL);
    }

    @Override
    public void atBattleStart() {
        flash();

        // Find first non-dead enemy
        AbstractMonster targetEnemy = null;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped()) {
                targetEnemy = m;
                break;
            }
        }

        if (targetEnemy != null && targetEnemy.hasPower(StrengthPower.POWER_ID)) {
            int enemyStr = targetEnemy.getPower(StrengthPower.POWER_ID).amount;
            int dexToGain = enemyStr / 2;

            if (dexToGain > 0) {
                AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                        new DexterityPower(AbstractDungeon.player, dexToGain), dexToGain));
                AbstractDungeon.actionManager.addToBottom(
                    new RelicAboveCreatureAction(AbstractDungeon.player, this));
            }
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
