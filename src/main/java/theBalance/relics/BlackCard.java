package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 黑卡 - Black Card
// 替换初始遗物。每回合开始获得 [E]。每打出一张非攻击牌，失去 5 金币（不足则扣 2 血）
public class BlackCard extends CustomRelic {
    public static final String ID = BalanceMod.makeID("BlackCard");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("BlackCard.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    private static final int GOLD_COST = 5;
    private static final int HP_COST = 2;

    public BlackCard() {
        super(ID, IMG, OUTLINE, RelicTier.BOSS, LandingSound.CLINK);
    }

    @Override
    public void onEquip() {
        AbstractDungeon.player.energy.energyMaster += 1;
    }

    @Override
    public void onUnequip() {
        AbstractDungeon.player.energy.energyMaster -= 1;
    }

    @Override
    public void atTurnStart() {
        flash();
    }

    @Override
    public void onPlayCard(AbstractCard card, com.megacrit.cardcrawl.monsters.AbstractMonster m) {
        // If not an attack card, charge the cost
        if (card.type != AbstractCard.CardType.ATTACK) {
            flash();
            if (AbstractDungeon.player.gold >= GOLD_COST) {
                AbstractDungeon.player.loseGold(GOLD_COST);
            } else {
                AbstractDungeon.actionManager.addToBottom(
                    new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, HP_COST));
            }
            AbstractDungeon.actionManager.addToBottom(
                new RelicAboveCreatureAction(AbstractDungeon.player, this));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
