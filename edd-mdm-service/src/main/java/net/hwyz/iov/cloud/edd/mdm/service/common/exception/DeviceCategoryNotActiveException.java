package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 设备类别非 ACTIVE 状态异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class DeviceCategoryNotActiveException extends MdmBaseException {

    private final String code;
    private final String status;

    public DeviceCategoryNotActiveException(String code, String status) {
        super(MdmErrorCode.DEVICE_CATEGORY_NOT_ACTIVE,
                String.format("设备类别 %s 非 ACTIVE 状态（当前: %s），不允许被引用", code, status));
        this.code = code;
        this.status = status;
        log.warn("设备类别[{}]非ACTIVE状态（当前: {}），不允许被引用", code, status);
    }
}
