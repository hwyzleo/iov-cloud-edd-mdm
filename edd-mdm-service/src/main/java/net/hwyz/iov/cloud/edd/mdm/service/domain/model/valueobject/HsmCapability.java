package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

/**
 * 车载节点 HSM（硬件安全模块）能力枚举（EEAD 子域）
 * <p>
 * 对齐 AUTOSAR / EVITA 行业实践。
 *
 * @author hwyz_leo
 */
public enum HsmCapability {

    /**
     * 无硬件安全能力
     */
    NONE,

    /**
     * SHE（Secure Hardware Extension）：最基本的 AES-128 + 受保护密钥存储
     */
    SHE,

    /**
     * HSM Lite/Mid：支持 AES、HMAC、RSA/ECC 等基础非对称运算
     */
    HSM_LIGHT,

    /**
     * HSM Full：支持完整 PKI 生命周期、独立 CPU、防侧信道攻击
     */
    HSM_FULL
}
