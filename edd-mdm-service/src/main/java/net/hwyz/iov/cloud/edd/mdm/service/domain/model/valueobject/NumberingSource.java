package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

/**
 * 零件号发号来源枚举
 * CR-023 新增
 */
public enum NumberingSource {
    MDM_GEN,  // MDM兜底自动生成（默认）
    PLM,      // PLM发号同步
    IMPORT;   // 存量迁移导入带号

    public static NumberingSource of(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
