package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 父级物料品类不存在异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class MaterialCategoryParentNotExistException extends MdmBaseException {

    private final String parentCode;

    public MaterialCategoryParentNotExistException(String parentCode) {
        super(MdmErrorCode.MATERIAL_CATEGORY_PARENT_NOT_EXIST, String.format("父级物料分类不存在: %s", parentCode));
        this.parentCode = parentCode;
        log.warn("父级物料分类不存在: {}", parentCode);
    }
}
