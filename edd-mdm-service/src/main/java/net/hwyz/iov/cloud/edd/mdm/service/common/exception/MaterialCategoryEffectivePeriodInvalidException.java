package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 物料品类生效期非法异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class MaterialCategoryEffectivePeriodInvalidException extends MdmBaseException {

    public MaterialCategoryEffectivePeriodInvalidException(String message) {
        super(MdmErrorCode.MATERIAL_CATEGORY_EFFECTIVE_PERIOD_INVALID, message);
        log.warn("物料品类生效期非法: {}", message);
    }

    public MaterialCategoryEffectivePeriodInvalidException(String message, Throwable cause) {
        super(MdmErrorCode.MATERIAL_CATEGORY_EFFECTIVE_PERIOD_INVALID, message);
        log.warn("物料品类生效期非法: {}", message, cause);
    }
}
