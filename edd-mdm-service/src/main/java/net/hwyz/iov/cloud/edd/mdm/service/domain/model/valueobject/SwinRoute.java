package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

/**
 * SWIN路由类型枚举
 * <p>
 * SINGLE_SWIN: 单SWIN路由，每个类型引用只能有一个ACTIVE的SWIN定义
 * MULTI_SWIN: 多SWIN路由，每个类型引用可以有多个ACTIVE的SWIN定义
 *
 * @author hwyz_leo
 */
public enum SwinRoute {
    SINGLE_SWIN,
    MULTI_SWIN;

    /**
     * 从字符串解析枚举值
     *
     * @param value 字符串值
     * @return 枚举值
     * @throws IllegalArgumentException 如果值无效
     */
    public static SwinRoute fromValue(String value) {
        for (SwinRoute route : values()) {
            if (route.name().equals(value)) {
                return route;
            }
        }
        throw new IllegalArgumentException("无效的SWIN路由类型: " + value);
    }
}
