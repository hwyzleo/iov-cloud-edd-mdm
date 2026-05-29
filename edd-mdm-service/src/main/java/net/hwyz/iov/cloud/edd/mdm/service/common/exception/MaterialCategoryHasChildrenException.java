package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 物料品类存在子项异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class MaterialCategoryHasChildrenException extends MdmBaseException {

    private final String categoryCode;
    private final long childCount;

    public MaterialCategoryHasChildrenException(String categoryCode, long childCount) {
        super(MdmErrorCode.MATERIAL_CATEGORY_HAS_CHILDREN, String.format("物料分类 %s 存在子分类，删除被拒绝（子分类数量: %d）", categoryCode, childCount));
        this.categoryCode = categoryCode;
        this.childCount = childCount;
        log.warn("物料分类[{}]存在子分类[{}]，删除被拒绝", categoryCode, childCount);
    }
}
