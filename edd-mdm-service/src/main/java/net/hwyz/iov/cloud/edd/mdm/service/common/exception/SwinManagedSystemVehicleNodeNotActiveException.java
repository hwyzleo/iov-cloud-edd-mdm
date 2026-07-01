package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * SWIN管理系统关联的车载节点非 ACTIVE 状态异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class SwinManagedSystemVehicleNodeNotActiveException extends MdmBaseException {

    private final String vehicleNodeCode;
    private final String vehicleNodeStatus;

    public SwinManagedSystemVehicleNodeNotActiveException(String vehicleNodeCode, String vehicleNodeStatus) {
        super(MdmErrorCode.SWIN_MANAGED_SYSTEM_VEHICLE_NODE_NOT_ACTIVE,
                String.format("SWIN管理系统关联的车载节点 %s 非 ACTIVE 状态（当前: %s）", vehicleNodeCode, vehicleNodeStatus));
        this.vehicleNodeCode = vehicleNodeCode;
        this.vehicleNodeStatus = vehicleNodeStatus;
        log.warn("SWIN管理系统关联的车载节点[{}]非ACTIVE状态（当前: {}）", vehicleNodeCode, vehicleNodeStatus);
    }
}
