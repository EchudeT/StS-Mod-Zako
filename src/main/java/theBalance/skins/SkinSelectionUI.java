package theBalance.skins;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import theBalance.util.TextureLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 角色选择界面的皮肤切换UI
 */
public class SkinSelectionUI {
    public static final Logger logger = LogManager.getLogger(SkinSelectionUI.class.getName());

    private static final float BUTTON_SIZE = 40.0f * Settings.scale;
    private static final float BUTTON_X_OFFSET = 240f * Settings.scale; // 调整到更下方，避免与其他按钮重叠
    private static final float BUTTON_Y_OFFSET = 100f * Settings.scale; // 调整到更下方，避免与其他按钮重叠
    private static final float BUTTON_SPACING = 50.0f * Settings.scale;

    private static final Color BUTTON_COLOR = new Color(1.0f, 1.0f, 1.0f, 0.8f);
    private static final Color BUTTON_HOVER_COLOR = new Color(1.0f, 1.0f, 0.5f, 1.0f);
    private static final Color TEXT_COLOR = Settings.GOLD_COLOR;

    private static boolean leftButtonHovered = false;
    private static boolean rightButtonHovered = false;
    private static float leftButtonX;
    private static float leftButtonY;
    private static float rightButtonX;
    private static float rightButtonY;

    private static CharacterOption currentCharacterOption = null;
    private static Map<String, Texture> buttonTextureCache = new HashMap<>();
    private static Map<String, Texture> portraitTextureCache = new HashMap<>();
    private static boolean hasInitializedBackground = false;

    /**
     * 在角色选择界面渲染皮肤切换按钮
     */
    public static void render(SpriteBatch sb, CharacterOption characterOption) {
        if (characterOption == null) {
            return;
        }

        // 只有当角色被选中时才显示按钮
        if (!characterOption.selected) {
            hasInitializedBackground = false; // 重置标志
            return;
        }

        currentCharacterOption = characterOption;

        // 获取当前皮肤
        CharacterSkin currentSkin = SkinManager.getCurrentSkin();
        if (currentSkin == null) {
            return;
        }

        // 第一次选中角色时，立即设置背景图
        if (!hasInitializedBackground) {
            updateBackgroundImage();
            hasInitializedBackground = true;
        }

        // 计算按钮位置（在角色选项下方）
        float centerX = characterOption.c.hb.cX + BUTTON_X_OFFSET;
        float centerY = characterOption.c.hb.cY + BUTTON_Y_OFFSET;

        leftButtonX = centerX - BUTTON_SPACING;
        leftButtonY = centerY;
        rightButtonX = centerX + BUTTON_SPACING;
        rightButtonY = centerY;

        // 检测鼠标悬停
        leftButtonHovered = isHovered(leftButtonX, leftButtonY);
        rightButtonHovered = isHovered(rightButtonX, rightButtonY);

        // 绘制左箭头按钮
        Color leftColor = leftButtonHovered ? BUTTON_HOVER_COLOR : BUTTON_COLOR;
        sb.setColor(leftColor);
        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, "<",
            leftButtonX, leftButtonY, leftColor);

        // 绘制右箭头按钮
        Color rightColor = rightButtonHovered ? BUTTON_HOVER_COLOR : BUTTON_COLOR;
        sb.setColor(rightColor);
        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, ">",
            rightButtonX, rightButtonY, rightColor);

        // 绘制当前皮肤名称
        FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont,
            currentSkin.name, centerX, centerY - 40.0f * Settings.scale, TEXT_COLOR);

        sb.setColor(Color.WHITE);
    }

    /**
     * 处理输入
     */
    public static void update() {
        if (InputHelper.justClickedLeft) {
            if (leftButtonHovered) {
                switchToPreviousSkin();
            } else if (rightButtonHovered) {
                switchToNextSkin();
            }
        }
    }

    /**
     * 检测鼠标是否悬停在按钮上
     */
    private static boolean isHovered(float x, float y) {
        float mouseX = InputHelper.mX;
        float mouseY = InputHelper.mY;
        float halfSize = BUTTON_SIZE / 2.0f;

        return mouseX >= x - halfSize && mouseX <= x + halfSize &&
               mouseY >= y - halfSize && mouseY <= y + halfSize;
    }

    /**
     * 切换到上一个皮肤
     */
    private static void switchToPreviousSkin() {
        SkinManager.switchToPreviousSkin();
        CardCrawlGame.sound.play("UI_CLICK_1");
        updateBackgroundImage();
        logger.info("Switched to previous skin: " + SkinManager.getCurrentSkin().name);
    }

    /**
     * 切换到下一个皮肤
     */
    private static void switchToNextSkin() {
        SkinManager.switchToNextSkin();
        CardCrawlGame.sound.play("UI_CLICK_1");
        updateBackgroundImage();
        logger.info("Switched to next skin: " + SkinManager.getCurrentSkin().name);
    }

    /**
     * 更新角色选择界面的背景图
     * 模仿 CharacterOption 第162-166行的逻辑
     */
    private static void updateBackgroundImage() {
        CharacterSkin currentSkin = SkinManager.getCurrentSkin();
        if (currentSkin == null) {
            return;
        }

        try {
            // 获取或加载portrait贴图
            Texture portraitTexture = getOrLoadTexture(portraitTextureCache, currentSkin.portraitPath);

            if (portraitTexture != null) {
                // 直接设置 CharacterSelectScreen.bgCharImg
                // 这是底层代码在第165行做的事情
                CardCrawlGame.mainMenuScreen.charSelectScreen.bgCharImg = portraitTexture;
                logger.info("Updated bgCharImg to: " + currentSkin.portraitPath);
            }
        } catch (Exception e) {
            logger.error("Failed to update background image", e);
        }
    }

    /**
     * 从缓存获取或加载贴图
     */
    private static Texture getOrLoadTexture(Map<String, Texture> cache, String path) {
        if (cache.containsKey(path)) {
            return cache.get(path);
        }

        try {
            Texture texture = TextureLoader.getTexture(path);
            cache.put(path, texture);
            return texture;
        } catch (Exception e) {
            logger.warn("Failed to load texture: " + path, e);
            return null;
        }
    }

    /**
     * 清空贴图缓存
     */
    public static void clearCache() {
        buttonTextureCache.clear();
        portraitTextureCache.clear();
    }

    /**
     * 设置当前角色选项
     */
    public static void setCharacterOption(CharacterOption option) {
        currentCharacterOption = option;
    }
}
