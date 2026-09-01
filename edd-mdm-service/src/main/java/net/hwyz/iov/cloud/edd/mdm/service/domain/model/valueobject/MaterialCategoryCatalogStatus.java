package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

/**
 * 物料品类标准目录状态（CR-039 §3）
 * <p>
 * 目录资源经 MaterialCategoryCatalogLoader 静态校验：
 * VALID = 目录自身一致（101 项、4/19/78 分层、编码/名称治理全部通过），可执行 preview/bootstrap；
 * INVALID = 目录非法（如总数/分层不符、code 或名称重复、父子前缀错误、环路、四层等），
 *           禁用 preview/bootstrap 并告警，但不阻断服务 readiness。
 *
 * @author hwyz_leo
 */
public enum MaterialCategoryCatalogStatus {
    VALID,
    INVALID
}
