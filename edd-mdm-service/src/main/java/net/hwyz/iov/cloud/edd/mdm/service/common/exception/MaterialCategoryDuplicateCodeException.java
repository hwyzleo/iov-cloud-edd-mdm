package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 物料品类 code 重复异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class MaterialCategoryDuplicateCodeException extends MdmBaseException {

    private final String conflictCode;
    private final String existingStatus;

    public MaterialCategoryDuplicateCodeException(String conflictCode, String existingStatus) {
        super(MdmErrorCode.MATERIAL_CATEGORY_CODE_EXIST, String.format("物料分类 code 已存在: %s（已占用记录状态: %s）", conflictCode, existingStatus));
        this.conflictCode = conflictCode;
        this.existingStatus = existingStatus;
        log.warn("物料分类code已存在: {}，已占用记录状态: {}", conflictCode, existingStatus);
    }

    public MaterialCategoryDuplicateCodeException(String conflictCode, String existingStatus, Throwable cause) {
        super(MdmErrorCode.MATERIAL_CATEGORY_CODE_EXIST, String.format("物料分类 code 已存在: %s（已占用记录状态: %s）", conflictCode, existingStatus));
        this.conflictCode = conflictCode;
        this.existingStatus = existingStatus;
        log.warn("物料分类code已存在: {}，已占用记录状态: {}", conflictCode, existingStatus, cause);
    }
}
