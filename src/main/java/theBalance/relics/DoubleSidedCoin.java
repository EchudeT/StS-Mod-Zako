package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import java.util.ArrayList;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 双面硬币 - Double Sided Coin
// 获得 [E]。每进入商店，必须选择移除一张非打击/防御的卡牌
public class DoubleSidedCoin extends CustomRelic {
    public static final String ID = BalanceMod.makeID("DoubleSidedCoin");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("placeholder_relic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    private boolean cardSelected = true;

    public DoubleSidedCoin() {
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
    public void onEnterRoom(AbstractRoom room) {
        cardSelected = false;
    }

    @Override
    public void update() {
        super.update();

        // Check if we're in a shop and haven't removed a card yet
        if (AbstractDungeon.getCurrRoom() instanceof ShopRoom && !cardSelected &&
            AbstractDungeon.screen == AbstractDungeon.CurrentScreen.NONE) {

            cardSelected = true;

            // Get all non-strike/defend cards
            CardGroup tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                if (card.type != AbstractCard.CardType.ATTACK || !card.hasTag(AbstractCard.CardTags.STARTER_STRIKE)) {
                    if (card.type != AbstractCard.CardType.SKILL || !card.hasTag(AbstractCard.CardTags.STARTER_DEFEND)) {
                        tmp.addToTop(card);
                    }
                }
            }

            if (!tmp.isEmpty()) {
                flash();
                AbstractDungeon.gridSelectScreen.open(tmp, 1,
                    DESCRIPTIONS[1], false, false, false, true);
            }
        }

        // Handle card removal
        if (AbstractDungeon.gridSelectScreen.selectedCards.size() > 0) {
            AbstractCard card = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.player.masterDeck.removeCard(card);
            AbstractDungeon.effectList.add(new PurgeCardEffect(card));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
