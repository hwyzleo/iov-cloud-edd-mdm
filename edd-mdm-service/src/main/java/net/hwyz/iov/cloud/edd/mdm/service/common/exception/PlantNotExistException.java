package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 工厂不存在异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class PlantNotExistException extends MdmBaseException {

    public PlantNotExistException(String message) {
        super(MdmErrorCode.PLANT_NOT_EXIST, message);
        log.warn("工厂不存在: {}", message);
    }

    public PlantNotExistException(String message, Throwable cause) {
        super(MdmErrorCode.PLANT_NOT_EXIST, message);
        log.warn("工厂不存在: {}", message, cause);
    }
}
