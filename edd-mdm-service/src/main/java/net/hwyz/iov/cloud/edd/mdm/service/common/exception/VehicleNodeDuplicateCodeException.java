package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 车载节点 nodeCode 重复异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class VehicleNodeDuplicateCodeException extends MdmBaseException {

    private final String conflictNodeCode;
    private final String existingStatus;

    public VehicleNodeDuplicateCodeException(String conflictNodeCode, String existingStatus) {
        super(MdmErrorCode.VEHICLE_NODE_CODE_EXIST, String.format("车载节点 nodeCode 已存在: %s（已占用记录状态: %s）", conflictNodeCode, existingStatus));
        this.conflictNodeCode = conflictNodeCode;
        this.existingStatus = existingStatus;
        log.warn("车载节点nodeCode已存在: {}，已占用记录状态: {}", conflictNodeCode, existingStatus);
    }

    public VehicleNodeDuplicateCodeException(String conflictNodeCode, String existingStatus, Throwable cause) {
        super(MdmErrorCode.VEHICLE_NODE_CODE_EXIST, String.format("车载节点 nodeCode 已存在: %s（已占用记录状态: %s）", conflictNodeCode, existingStatus));
        this.conflictNodeCode = conflictNodeCode;
        this.existingStatus = existingStatus;
        log.warn("车载节点nodeCode已存在: {}，已占用记录状态: {}", conflictNodeCode, existingStatus, cause);
    }
}
