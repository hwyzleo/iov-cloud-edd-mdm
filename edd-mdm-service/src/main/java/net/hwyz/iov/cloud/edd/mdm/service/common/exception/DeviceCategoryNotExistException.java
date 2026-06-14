package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 设备类别不存在异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class DeviceCategoryNotExistException extends MdmBaseException {

    public DeviceCategoryNotExistException(String code) {
        super(MdmErrorCode.DEVICE_CATEGORY_NOT_EXIST, String.format("设备类别不存在: %s", code));
        log.warn("设备类别不存在: {}", code);
    }

    public DeviceCategoryNotExistException(String message, Throwable cause) {
        super(MdmErrorCode.DEVICE_CATEGORY_NOT_EXIST, message);
        log.warn("设备类别不存在: {}", message, cause);
    }
}
