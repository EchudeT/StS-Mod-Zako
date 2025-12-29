package theBalance.relics;

import basemod.ReflectionHacks; // 必须导入这个用于反射
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import theBalance.BalanceMod;
import theBalance.util.TextureLoader;

import java.util.ArrayList;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 双面硬币 - Double Sided Coin
// 获得 [E]。每进入商店，自动购买最便宜的那张卡（如果不缺钱）。
public class DoubleSidedCoin extends CustomRelic {
    public static final String ID = BalanceMod.makeID("DoubleSidedCoin");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("placeholder_relic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    private boolean shopTriggered = false;

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
        // 每次进入新房间，重置触发标记
        shopTriggered = false;
    }

    @Override
    public void update() {
        super.update();

        // 检查：在商店房间 + 尚未触发 + 商店界面已初始化
        if (AbstractDungeon.getCurrRoom() instanceof ShopRoom && !shopTriggered) {

            // 为了安全起见，稍作延迟确保商店初始化完毕（通常进入房间即初始化）
            // 这里直接操作，因为ShopRoom的onPlayerEntry会初始化ShopScreen

            forceBuyCheapestCard();
            shopTriggered = true; // 标记为已触发，防止无限买
        }
    }

    private void forceBuyCheapestCard() {
        ShopScreen shop = AbstractDungeon.shopScreen;
        if (shop == null) return;

        // 1. 获取商店里的卡牌 (使用反射，因为 cards 列表是 private 的)
        ArrayList<AbstractCard> coloredCards = ReflectionHacks.getPrivate(shop, ShopScreen.class, "coloredCards");
        ArrayList<AbstractCard> colorlessCards = ReflectionHacks.getPrivate(shop, ShopScreen.class, "colorlessCards");

        // 将所有卡牌汇总到一个列表里查找
        ArrayList<AbstractCard> allShopCards = new ArrayList<>();
        if (coloredCards != null) allShopCards.addAll(coloredCards);
        if (colorlessCards != null) allShopCards.addAll(colorlessCards);

        if (allShopCards.isEmpty()) return;

        // 2. 寻找最便宜的卡
        AbstractCard cheapestCard = null;
        int minPrice = Integer.MAX_VALUE;

        for (AbstractCard c : allShopCards) {
            if (c.price < minPrice) {
                minPrice = c.price;
                cheapestCard = c;
            }
        }

        // 3. 执行购买逻辑
        if (cheapestCard != null) {
            // 检查钱够不够
            if (AbstractDungeon.player.gold >= cheapestCard.price) {
                flash();

                // 扣钱
                AbstractDungeon.player.loseGold(cheapestCard.price);

                // 播放音效
                CardCrawlGame.sound.play("SHOP_PURCHASE", 0.1F);

                // 从商店列表中移除该卡 (这样它就在地毯上消失了)
                if (coloredCards != null) coloredCards.remove(cheapestCard);
                if (colorlessCards != null) colorlessCards.remove(cheapestCard);

                // 视觉特效：卡牌飞入卡组
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(
                        cheapestCard,
                        Settings.WIDTH / 2.0F,
                        Settings.HEIGHT / 2.0F
                ));

                AbstractDungeon.player.dialogX = AbstractDungeon.player.drawX;
                AbstractDungeon.player.dialogY = AbstractDungeon.player.drawY + 200.0F * Settings.scale;
//                AbstractDungeon.player.speak("身体...不受控制...买了！");
                AbstractDungeon.effectList.add(new SpeechBubble(
                        AbstractDungeon.player.dialogX,
                        AbstractDungeon.player.dialogY,
                        2.0F, // 持续时间
                        "身体...不受控制...买了！",
                        true // 是否是玩家说话（true=玩家，false=敌人）
                ));
            } else {
                // 钱不够时的处理（可选）
                AbstractDungeon.player.dialogX = AbstractDungeon.player.drawX;
                AbstractDungeon.player.dialogY = AbstractDungeon.player.drawY + 200.0F * Settings.scale;
//                AbstractDungeon.player.speak("切，买不起...");
                AbstractDungeon.effectList.add(new SpeechBubble(
                        AbstractDungeon.player.dialogX,
                        AbstractDungeon.player.dialogY,
                        2.0F, // 持续时间
                        "切，买不起...",
                        true // 是否是玩家说话（true=玩家，false=敌人）
                ));
            }
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}