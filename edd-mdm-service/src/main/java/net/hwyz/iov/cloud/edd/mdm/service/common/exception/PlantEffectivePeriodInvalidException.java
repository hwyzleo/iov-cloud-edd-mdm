package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 工厂生效期非法异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class PlantEffectivePeriodInvalidException extends MdmBaseException {

    public PlantEffectivePeriodInvalidException(String message) {
        super(MdmErrorCode.PLANT_EFFECTIVE_PERIOD_INVALID, message);
        log.warn("工厂生效期非法: {}", message);
    }
}
