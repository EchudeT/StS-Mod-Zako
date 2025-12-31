package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import theBalance.BalanceMod;
import theBalance.cards.Lending;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

public class BlankCard extends CustomRelic {
    public static final String ID = BalanceMod.makeID("BlankCard");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("BlankCard.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    public static final int GOLD_MULTIPLIER = 6;
    public static final int ZERO_COST_PRICE = 0;
    public static final int COMBAT_GOLD_REWARD = 0;

    public BlankCard() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public void onEquip() {
        AbstractCard card1 = new Lending();
        AbstractCard card2 = new Lending();

        float centerX = Settings.WIDTH / 2.0F;
        float centerY = Settings.HEIGHT / 2.0F;

        float offset = 250.0F * Settings.scale;

        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card1, centerX - offset, centerY));
        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card2, centerX + offset, centerY));
    }


    @Override
    public void onVictory() {
        flash();
        AbstractDungeon.player.gainGold(COMBAT_GOLD_REWARD);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
