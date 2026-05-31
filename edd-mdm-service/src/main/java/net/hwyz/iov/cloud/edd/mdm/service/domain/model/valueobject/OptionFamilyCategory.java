package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

/**
 * 选项族商品分类枚举
 * <p>
 * 商品/销售视角的分组维度，区别于 EEAD functionalDomain（工程视角）。
 * CR-010 纳入。
 *
 * @author hwyz_leo
 */
public enum OptionFamilyCategory {

    /**
     * 外饰
     */
    EXTERIOR,

    /**
     * 内饰
     */
    INTERIOR,

    /**
     * 动力总成
     */
    POWERTRAIN,

    /**
     * 智能化
     */
    INTELLIGENT,

    /**
     * 舒适便利
     */
    COMFORT,

    /**
     * 安全
     */
    SAFETY,

    /**
     * 选装附件
     */
    ACCESSORY,

    /**
     * 其他
     */
    OTHER
}
