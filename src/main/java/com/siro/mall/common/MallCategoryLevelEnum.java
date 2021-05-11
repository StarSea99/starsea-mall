package com.siro.mall.common;

/**
 * 分类级别
 * @author starsea
 * @date 2021-01-22
 */
public enum MallCategoryLevelEnum {

    DEFAULT(0, "ERROR"),
    LEVEL_ONE(1, "一级分类"),
    LEVEL_TWO(2, "二级分类"),
    LEVEL_THREE(3, "三级分类");

    private int level;
    private String name;

    MallCategoryLevelEnum(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public static MallCategoryLevelEnum getMallOrderStatusEnumByLevel(int level) {
        for (MallCategoryLevelEnum mallCategoryLevelEnum : MallCategoryLevelEnum.values()) {
            if (mallCategoryLevelEnum.getLevel() == level) {
                return mallCategoryLevelEnum;
            }
        }
        return DEFAULT;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
