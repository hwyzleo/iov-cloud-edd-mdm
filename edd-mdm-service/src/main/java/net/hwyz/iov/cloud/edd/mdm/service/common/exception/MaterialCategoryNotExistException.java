package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 物料品类不存在异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class MaterialCategoryNotExistException extends MdmBaseException {

    public MaterialCategoryNotExistException(String message) {
        super(MdmErrorCode.MATERIAL_CATEGORY_NOT_EXIST, message);
        log.warn("物料品类不存在: {}", message);
    }

    public MaterialCategoryNotExistException(String message, Throwable cause) {
        super(MdmErrorCode.MATERIAL_CATEGORY_NOT_EXIST, message);
        log.warn("物料品类不存在: {}", message, cause);
    }
}
