package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 工厂 code 重复异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class PlantDuplicateCodeException extends MdmBaseException {

    private final String conflictCode;
    private final String existingStatus;

    public PlantDuplicateCodeException(String conflictCode, String existingStatus) {
        super(MdmErrorCode.PLANT_CODE_EXIST, String.format("工厂 code 已存在: %s（已占用记录状态: %s）", conflictCode, existingStatus));
        this.conflictCode = conflictCode;
        this.existingStatus = existingStatus;
        log.warn("工厂code已存在: {}，已占用记录状态: {}", conflictCode, existingStatus);
    }

    public PlantDuplicateCodeException(String conflictCode, String existingStatus, Throwable cause) {
        super(MdmErrorCode.PLANT_CODE_EXIST, String.format("工厂 code 已存在: %s（已占用记录状态: %s）", conflictCode, existingStatus));
        this.conflictCode = conflictCode;
        this.existingStatus = existingStatus;
        log.warn("工厂code已存在: {}，已占用记录状态: {}", conflictCode, existingStatus, cause);
    }
}
