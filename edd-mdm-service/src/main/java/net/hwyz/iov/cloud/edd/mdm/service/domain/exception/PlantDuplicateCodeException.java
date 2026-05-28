package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

/**
 * 工厂 code 重复异常（Org 子域）
 * <p>
 * 错误码：813002 PLANT_CODE_EXIST
 *
 * @author hwyz_leo
 */
@Getter
public class PlantDuplicateCodeException extends RuntimeException {

    public static final int ERROR_CODE = 813002;

    private final String conflictCode;
    private final String existingStatus;

    public PlantDuplicateCodeException(String conflictCode, String existingStatus) {
        super(String.format("工厂 code 已存在: %s（已占用记录状态: %s）", conflictCode, existingStatus));
        this.conflictCode = conflictCode;
        this.existingStatus = existingStatus;
    }

    public PlantDuplicateCodeException(String conflictCode, String existingStatus, Throwable cause) {
        super(String.format("工厂 code 已存在: %s（已占用记录状态: %s）", conflictCode, existingStatus), cause);
        this.conflictCode = conflictCode;
        this.existingStatus = existingStatus;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
