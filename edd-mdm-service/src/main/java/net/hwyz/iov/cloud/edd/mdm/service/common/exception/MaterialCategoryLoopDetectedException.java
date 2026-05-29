package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 物料品类层级形成环路异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class MaterialCategoryLoopDetectedException extends MdmBaseException {

    private final String categoryCode;
    private final String parentCode;

    public MaterialCategoryLoopDetectedException(String categoryCode, String parentCode) {
        super(MdmErrorCode.MATERIAL_CATEGORY_LOOP_DETECTED, String.format("物料分类层级存在循环: 分类 %s 不能将 %s 设为父级", categoryCode, parentCode));
        this.categoryCode = categoryCode;
        this.parentCode = parentCode;
        log.warn("物料分类层级存在循环: 分类[{}]不能将[{}]设为父级", categoryCode, parentCode);
    }
}
