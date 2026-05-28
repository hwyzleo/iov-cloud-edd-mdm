package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

/**
 * 车载节点信息安全等级枚举（EEAD 子域）
 * <p>
 * 对齐 ISO/SAE 21434《道路车辆 - 网络安全工程》的 Cybersecurity Assurance Level（CAL）。
 * 注意：与 ISO 26262 的功能安全等级 ASIL-A~D 是不同的概念，本枚举仅描述网络安全等级。
 *
 * @author hwyz_leo
 */
public enum SecurityLevel {

    /**
     * 质量管理（无特殊网络安全要求，按一般质量管理）
     */
    QM,

    /**
     * 基础安全（潜在影响有限）
     */
    CAL1,

    /**
     * 中等安全（潜在影响中等）
     */
    CAL2,

    /**
     * 高安全（潜在影响高）
     */
    CAL3,

    /**
     * 极高安全（潜在影响极高）
     */
    CAL4
}
