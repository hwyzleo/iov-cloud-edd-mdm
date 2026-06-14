package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

/**
 * 零件类型枚举（Material 子域）
 * 物料性质/类型维度：RAW_MATERIAL / STANDARD_PART / CUSTOM_PART
 * 软硬件判定：由 is_software 布尔字段承载
 * 总成判定：由 is_assembly 布尔字段承载
 *
 * @author hwyz_leo
 */
public enum PartType {
    RAW_MATERIAL,
    STANDARD_PART,
    CUSTOM_PART
}
