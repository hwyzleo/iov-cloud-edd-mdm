package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

/**
 * 车载节点 OTA 支持类型枚举（EEAD 子域）
 *
 * @author hwyz_leo
 */
public enum OtaSupportType {

    /**
     * 固件 OTA（Firmware Over-The-Air）
     */
    FOTA,

    /**
     * 软件 OTA（Software Over-The-Air）
     */
    SOTA,

    /**
     * 同时支持 FOTA 与 SOTA
     */
    BOTH,

    /**
     * 不支持 OTA
     */
    NOT_SUPPORTED
}
