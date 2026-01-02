package theBalance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;
import theBalance.BalanceMod;
import theBalance.characters.Zako;
import theBalance.powers.CombatGoldPower;

import static theBalance.BalanceMod.makeCardPath;

public class PriceOfHubris extends AbstractDynamicCard {

    // 傲慢之价 - Price of Hubris
    // 获得 1 层实体。获得当前金币 10%(15%) 的力量。失去 50% 金币。 消耗。

    public static final String ID = BalanceMod.makeID(PriceOfHubris.class.getSimpleName());
    public static final String IMG = makeCardPath("PriceOfHubris.png");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    private static final CardRarity RARITY = CardRarity.SPECIAL; // 特殊稀有度
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = CardColor.COLORLESS; // 无色牌

    private static final int COST = 0;
    private static final int INTANGIBLE_AMT = 1;
    private static final int GOLD_PERCENT_TO_STR = 5; // 10% 金币转力量
    private static final int GOLD_LOSS_PERCENT = 50; // 失去 50% 金币

    public PriceOfHubris() {
        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);
        this.baseMagicNumber = this.magicNumber = GOLD_PERCENT_TO_STR;
        this.exhaust = true; // 消耗
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获得实体 (保命)
        addToBot(new ApplyPowerAction(p, p, new IntangiblePlayerPower(p, 1), 1));

        // 2. 计算力量 (爆发)
        int currentGold = p.gold;
        int strGain = currentGold * this.magicNumber / 100;

        if (strGain > 0) {
            addToBot(new com.megacrit.cardcrawl.actions.animations.VFXAction(p, new InflameEffect(p), 1.0F));
            addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, strGain), strGain));
        }

        // 3. 失去金币 (代价)
        int totalCost = (int)(currentGold * (GOLD_LOSS_PERCENT / 100.0f));

        if (totalCost > 0) {
            int allowanceAmt = 0;
            // 检查是否有战斗津贴
            if (p.hasPower(CombatGoldPower.POWER_ID)) {
                allowanceAmt = p.getPower(CombatGoldPower.POWER_ID).amount;
            }

            // 计算分摊
            int paidFromAllowance = Math.min(allowanceAmt, totalCost);
            int paidFromGold = totalCost - paidFromAllowance;

            // A. 扣除津贴
            if (paidFromAllowance > 0) {
                addToBot(new ReducePowerAction(p, p, CombatGoldPower.POWER_ID, paidFromAllowance));
            }

            // B. 扣除金币 (封装在Action里以保证执行顺序)
            if (paidFromGold > 0) {
                // 使用 final 变量传递给匿名类
                final int goldToLose = paidFromGold;
                addToBot(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractDungeon.player.loseGold(goldToLose);
                        this.isDone = true;
                    }
                });
            }
        }
    }

    // 动态更新描述，显示当前能获得多少力量，失去多少金币
    @Override
    public void applyPowers() {
        super.applyPowers();

        AbstractPlayer p = AbstractDungeon.player;
        int currentGold = p.gold;

        int strGain = currentGold * this.magicNumber / 100;
        int totalCost = (int)(currentGold * (GOLD_LOSS_PERCENT / 100.0f));

        int allowanceAmt = 0;
        if (p.hasPower(CombatGoldPower.POWER_ID)) {
            allowanceAmt = p.getPower(CombatGoldPower.POWER_ID).amount;
        }

        int paidFromAllowance = Math.min(allowanceAmt, totalCost);
        int paidFromGold = totalCost - paidFromAllowance;

        // 构建动态描述
        StringBuilder sb = new StringBuilder();
        sb.append(this.originalRawDescription);
        sb.append(" NL (获得 ").append(strGain).append(" 力量，失去 ");

        if (paidFromAllowance > 0) {
            sb.append(paidFromAllowance).append(" 津贴");
        }

        if (paidFromAllowance > 0 && paidFromGold > 0) {
            sb.append(" 和 ");
        }

        if (paidFromGold > 0 || (paidFromAllowance == 0)) {
            sb.append(paidFromGold).append(" 金币");
        }

        sb.append(")");

        this.rawDescription = sb.toString();
        super.initializeDescription();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(3);
            initializeDescription();
        }
    }
}