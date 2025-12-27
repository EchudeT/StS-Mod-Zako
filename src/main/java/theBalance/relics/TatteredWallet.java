package theBalance.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import theBalance.BalanceMod;
import theBalance.powers.CombatGoldPower;
import theBalance.util.TextureLoader;

import static theBalance.BalanceMod.makeRelicOutlinePath;
import static theBalance.BalanceMod.makeRelicPath;

// 破烂的钱包 - Tattered Wallet
// 战斗开始时，获得 15 点战斗津贴
public class TatteredWallet extends CustomRelic {
    public static final String ID = BalanceMod.makeID("TatteredWallet");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("placeholder_relic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("placeholder_relic.png"));

    private static final int COMBAT_GOLD_AMOUNT = 15;

    public TatteredWallet() {
        super(ID, IMG, OUTLINE, RelicTier.STARTER, LandingSound.FLAT);
    }

    @Override
    public void atBattleStart() {
        flash();
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                new CombatGoldPower(AbstractDungeon.player, COMBAT_GOLD_AMOUNT), COMBAT_GOLD_AMOUNT));
        AbstractDungeon.actionManager.addToBottom(
            new RelicAboveCreatureAction(AbstractDungeon.player, this));
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
