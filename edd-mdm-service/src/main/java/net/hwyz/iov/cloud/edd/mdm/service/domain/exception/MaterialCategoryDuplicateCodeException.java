package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

@Getter
public class MaterialCategoryDuplicateCodeException extends RuntimeException {
    public static final int ERROR_CODE = 814002;
    private final String conflictCode;
    private final String existingStatus;

    public MaterialCategoryDuplicateCodeException(String conflictCode, String existingStatus) {
        super(String.format("物料分类 code 已存在: %s（已占用记录状态: %s）", conflictCode, existingStatus));
        this.conflictCode = conflictCode;
        this.existingStatus = existingStatus;
    }

    public MaterialCategoryDuplicateCodeException(String conflictCode, String existingStatus, Throwable cause) {
        super(String.format("物料分类 code 已存在: %s（已占用记录状态: %s）", conflictCode, existingStatus), cause);
        this.conflictCode = conflictCode;
        this.existingStatus = existingStatus;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
