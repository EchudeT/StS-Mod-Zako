package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 精神损失费 - Compensatory Damages
// 受伤获得金币
public class HushMoney extends CustomRelic {
    public static final String ID = BalanceMod.makeID("HushMoney");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("HushMoney.png")); // 记得换个哭脸的图标
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    private static final int GOLD_PERCENTAGE = 100; // 100% 转化率，如果觉得太强可以降到 50%

    public HushMoney() {
        super(ID, IMG, OUTLINE, RelicTier.RARE, LandingSound.CLINK);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        // 必须是战斗中，且伤害来源是敌人（防止自残卡刷钱，如果想允许自残刷钱可以去掉 info.owner check）
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT &&
                info.type == DamageInfo.DamageType.NORMAL &&
                damageAmount > 0) {

            flash();
            // 计算获得的金币量
            int goldGain = (damageAmount * GOLD_PERCENTAGE) / 100;
            if (goldGain > 0) {
                AbstractDungeon.actionManager.addToBottom(
                        new GainGoldAction(goldGain)
                );
            }
        }
        return damageAmount;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}