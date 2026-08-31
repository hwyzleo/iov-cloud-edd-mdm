package net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog;

/**
 * 选项族标准目录层级（CR-035）
 * <p>
 * 三级目录：Core 核心标准族 / Conditional 条件标准族 / Extension 企业扩展族。
 * Extension 不进入标准目录资源，仅作为命名指导。
 *
 * @author hwyz_leo
 */
public enum OptionFamilyCatalogTier {

    /**
     * 核心标准族：跨传统车企和新能源车型普遍存在、语义稳定，受控初始化导入为 ACTIVE
     */
    CORE,

    /**
     * 条件标准族：行业中较常见但仅适用于特定能源形式/车型定位/区域法规，按需启用，不默认初始化
     */
    CONDITIONAL
}
