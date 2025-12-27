package theBalance.skins;

/**
 * 角色皮肤配置类
 * 存储每个皮肤的资源路径
 */
public class CharacterSkin {
    public final int cid;
    public final String id;
    public final String name;
    public final String description;

    // 角色资源路径
    public final String shoulderPath1;
    public final String shoulderPath2;
    public final String corpsePath;
    public final String atlasPath;
    public final String jsonPath;

    // 选择界面资源
    public final String buttonPath;
    public final String portraitPath;

    public CharacterSkin(int cid, String id, String name, String description,
                        String shoulderPath1, String shoulderPath2, String corpsePath,
                        String atlasPath, String jsonPath,
                        String buttonPath, String portraitPath) {
        this.cid = cid;
        this.id = id;
        this.name = name;
        this.description = description;
        this.shoulderPath1 = shoulderPath1;
        this.shoulderPath2 = shoulderPath2;
        this.corpsePath = corpsePath;
        this.atlasPath = atlasPath;
        this.jsonPath = jsonPath;
        this.buttonPath = buttonPath;
        this.portraitPath = portraitPath;
    }

    @Override
    public String toString() {
        return name;
    }
}
