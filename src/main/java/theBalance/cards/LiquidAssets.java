package theBalance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import theBalance.BalanceMod;
import theBalance.characters.Zako;
import theBalance.powers.CombatGoldPower;

import static theBalance.BalanceMod.makeCardPath;

public class LiquidAssets extends AbstractDynamicCard {
    public static final String ID = BalanceMod.makeID(LiquidAssets.class.getSimpleName());
    public static final String IMG = makeCardPath("LiquidAssets.png");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Zako.Enums.COLOR_GRAY;

    private static final int COST = 1;
    private static final int BLOCK_AMT = 5;
    private static final int UPGRADE_PLUS_BLOCK = 3;

    // 消耗的津贴数量
    private static final int ALLOWANCE_COST = 5;

    public LiquidAssets() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        this.baseBlock = BLOCK_AMT;
        this.baseMagicNumber = this.magicNumber = ALLOWANCE_COST;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 第一次获得格挡 (无条件)
        addToBot(new GainBlockAction(p, p, block));

        // 2. 尝试消耗津贴获得第二次格挡
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                // 检查是否有战斗津贴
                if (p.hasPower(CombatGoldPower.POWER_ID)) {
                    int currentAllowance = p.getPower(CombatGoldPower.POWER_ID).amount;

                    // 如果津贴足够
                    if (currentAllowance >= magicNumber) {
                        // 扣除津贴 (这会触发"消耗津贴时"的特效，如抽牌)
                        addToTop(new ReducePowerAction(p, p, CombatGoldPower.POWER_ID, magicNumber));

                        // 再次获得格挡 (视觉上会有两次跳字，很有打击感)
                        addToTop(new GainBlockAction(p, p, block));

                        // 可选：加一个特效让玩家知道触发了增幅
                        // p.getPower(CombatGoldPower.POWER_ID).flash();
                    }
                }
                this.isDone = true;
            }
        });
    }

    // 可选：让卡牌在手牌中发光，提示玩家当前津贴足够触发额外效果
    @Override
    public void triggerOnGlowCheck() {
        this.glowColor = AbstractDynamicCard.BLUE_BORDER_GLOW_COLOR.cpy();
        if (AbstractDungeon.player.hasPower(CombatGoldPower.POWER_ID)) {
            if (AbstractDungeon.player.getPower(CombatGoldPower.POWER_ID).amount >= this.magicNumber) {
                this.glowColor = AbstractDynamicCard.GOLD_BORDER_GLOW_COLOR.cpy();
            }
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBlock(UPGRADE_PLUS_BLOCK);
            initializeDescription();
        }
    }
}