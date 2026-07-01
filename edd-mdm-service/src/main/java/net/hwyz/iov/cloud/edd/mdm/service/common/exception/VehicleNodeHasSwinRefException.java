package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 车载节点被SWIN管理软件系统引用异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class VehicleNodeHasSwinRefException extends MdmBaseException {

    private final String nodeCode;
    private final long referenceCount;

    public VehicleNodeHasSwinRefException(String nodeCode, long referenceCount) {
        super(MdmErrorCode.VEHICLE_NODE_HAS_SWIN_REF,
                String.format("车载节点 %s 被SWIN管理软件系统引用，删除被拒绝（引用数量: %d）", nodeCode, referenceCount));
        this.nodeCode = nodeCode;
        this.referenceCount = referenceCount;
        log.warn("车载节点[{}]被SWIN管理软件系统引用，删除被拒绝（引用数量={}）", nodeCode, referenceCount);
    }
}
