package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 观察笔记 - Observation Notes
// 每当你对敌人施加负面状态（Debuff），下一次攻击伤害提升 3 点
public class ObservationNotes extends CustomRelic {
    public static final String ID = BalanceMod.makeID("ObservationNotes");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("ObservationNotes.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    private static final int BONUS_AMT = 3;

    public ObservationNotes() {
        super(ID, IMG, OUTLINE, RelicTier.COMMON, LandingSound.FLAT);
        this.counter = 0; // 使用 counter 记录积累的额外伤害
    }

    @Override
    public void atBattleStart() {
        this.counter = 0;
    }

    // --- 1. 供 Patch 调用的逻辑：施加了 Debuff ---
    public void onDebuffApplied() {
        flash();
        this.counter += BONUS_AMT;
    }

    // --- 2. 视觉效果：修改手牌中的伤害显示 ---
    @Override
    public float atDamageModify(float damage, AbstractCard c) {
        // 如果有积累的层数，且卡牌是攻击牌，显示加成后的伤害
        if (this.counter > 0 && c.type == AbstractCard.CardType.ATTACK) {
            return damage + this.counter;
        }
        return damage;
    }

    // --- 3. 实际效果：造成伤害时生效 ---
    @Override
    public int onAttackToChangeDamage(DamageInfo info, int damageAmount) {
        // 必须是玩家的攻击，且必须是普通伤害（防止被荆棘等被动伤害消耗）
        if (info.owner == AbstractDungeon.player &&
                info.type == DamageInfo.DamageType.NORMAL &&
                this.counter > 0) {

            flash();
            // 加上伤害
            int totalDamage = damageAmount + this.counter;

            // 消耗层数 ("下一次"意味着只生效一次)
            // 如果是多段攻击，这只会加成第一段。这是Spire的标准"下一次攻击"逻辑(如手里剑)。
            this.counter = 0;

            return totalDamage;
        }
        return damageAmount;
    }

    @Override
    public void onVictory() {
        this.counter = -1; // 战斗结束重置
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}