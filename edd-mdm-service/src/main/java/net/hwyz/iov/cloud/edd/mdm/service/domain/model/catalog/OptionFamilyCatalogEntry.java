package net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionFamilyCategory;

/**
 * 选项族标准目录条目（CR-035 §5.1）
 * <p>
 * 每条记录包含：tier / code / name / nameLocal / category / description / activationCondition（仅 CONDITIONAL）。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionFamilyCatalogEntry {

    /**
     * 目录层级（CORE / CONDITIONAL）
     */
    private OptionFamilyCatalogTier tier;

    /**
     * 标准选项族 code
     */
    private String code;

    /**
     * 英文名称（Title Case）
     */
    private String name;

    /**
     * 中文名称
     */
    private String nameLocal;

    /**
     * 所属分类
     */
    private OptionFamilyCategory category;

    /**
     * 描述（可选）
     */
    private String description;

    /**
     * 启用条件（仅 CONDITIONAL 必填）
     */
    private String activationCondition;
}
