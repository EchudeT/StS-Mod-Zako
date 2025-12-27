package theBalance.skins;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色皮肤管理器
 * 管理所有皮肤的注册、切换和持久化
 */
public class SkinManager {
    public static final Logger logger = LogManager.getLogger(SkinManager.class.getName());

    private static final Map<String, CharacterSkin> skins = new HashMap<>();
    private static final List<String> skinOrder = new ArrayList<>();
    private static String currentSkinId = null;
    private static String defaultSkinId = null;

    private static final String SKIN_CONFIG_KEY = "selectedSkin";
    private static SpireConfig config;

    /**
     * 初始化皮肤管理器，加载配置
     */
    public static void initialize() {
        try {
            config = new SpireConfig("balanceMod", "skinConfig");
            String savedSkin = config.getString(SKIN_CONFIG_KEY);
            if (savedSkin != null && !savedSkin.isEmpty()) {
                currentSkinId = savedSkin;
            }
            logger.info("Skin manager initialized. Saved skin: " + savedSkin);
        } catch (Exception e) {
            logger.error("Failed to load skin config", e);
        }
    }

    /**
     * 注册皮肤
     * @param skin 要注册的皮肤
     * @param isDefault 是否为默认皮肤
     */
    public static void registerSkin(CharacterSkin skin, boolean isDefault) {
        skins.put(skin.id, skin);
        skinOrder.add(skin.id);

        if (isDefault) {
            defaultSkinId = skin.id;
            if (currentSkinId == null) {
                currentSkinId = skin.id;
            }
        }

        logger.info("Registered skin: " + skin.id + " (" + skin.name + ")" + (isDefault ? " [DEFAULT]" : ""));
    }

    /**
     * 切换到指定ID的皮肤
     */
    public static boolean switchSkin(String skinId) {
        if (!skins.containsKey(skinId)) {
            logger.warn("Attempted to switch to non-existent skin: " + skinId);
            return false;
        }

        currentSkinId = skinId;
        saveSkinConfig();
        logger.info("Switched to skin: " + skinId);
        return true;
    }

    /**
     * 切换到下一个皮肤（循环）
     */
    public static void switchToNextSkin() {
        if (skinOrder.isEmpty()) {
            return;
        }

        int currentIndex = skinOrder.indexOf(currentSkinId);
        int nextIndex = (currentIndex + 1) % skinOrder.size();
        String nextSkinId = skinOrder.get(nextIndex);
        switchSkin(nextSkinId);
    }

    /**
     * 切换到上一个皮肤（循环）
     */
    public static void switchToPreviousSkin() {
        if (skinOrder.isEmpty()) {
            return;
        }

        int currentIndex = skinOrder.indexOf(currentSkinId);
        int previousIndex = (currentIndex - 1 + skinOrder.size()) % skinOrder.size();
        String previousSkinId = skinOrder.get(previousIndex);
        switchSkin(previousSkinId);
    }

    /**
     * 获取当前皮肤
     */
    public static CharacterSkin getCurrentSkin() {
        if (currentSkinId == null || !skins.containsKey(currentSkinId)) {
            // 如果当前皮肤无效，使用默认皮肤
            currentSkinId = defaultSkinId;
        }
        return skins.get(currentSkinId);
    }

    /**
     * 获取默认皮肤
     */
    public static CharacterSkin getDefaultSkin() {
        return skins.get(defaultSkinId);
    }

    /**
     * 获取所有皮肤
     */
    public static List<CharacterSkin> getAllSkins() {
        List<CharacterSkin> result = new ArrayList<>();
        for (String skinId : skinOrder) {
            result.add(skins.get(skinId));
        }
        return result;
    }

    /**
     * 获取皮肤总数
     */
    public static int getSkinCount() {
        return skins.size();
    }

    /**
     * 保存皮肤配置
     */
    private static void saveSkinConfig() {
        if (config != null && currentSkinId != null) {
            try {
                config.setString(SKIN_CONFIG_KEY, currentSkinId);
                config.save();
                logger.info("Saved skin config: " + currentSkinId);
            } catch (Exception e) {
                logger.error("Failed to save skin config", e);
            }
        }
    }

    /**
     * 重置为默认皮肤
     */
    public static void resetToDefault() {
        if (defaultSkinId != null) {
            switchSkin(defaultSkinId);
        }
    }
}
