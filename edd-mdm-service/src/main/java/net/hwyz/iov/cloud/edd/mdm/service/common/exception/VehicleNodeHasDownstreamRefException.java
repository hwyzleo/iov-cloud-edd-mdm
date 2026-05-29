package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * 车载节点存在下游引用异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class VehicleNodeHasDownstreamRefException extends MdmBaseException {

    private final String nodeCode;
    private final String referencingService;
    private final long referenceCount;
    private final List<String> samples;

    public VehicleNodeHasDownstreamRefException(String nodeCode, String referencingService,
                                                long referenceCount, List<String> samples) {
        super(MdmErrorCode.VEHICLE_NODE_HAS_DOWNSTREAM_REF, String.format("车载节点 %s 存在下游引用，删除被拒绝（引用方=%s，引用数量=%d）",
                nodeCode, referencingService, referenceCount));
        this.nodeCode = nodeCode;
        this.referencingService = referencingService;
        this.referenceCount = referenceCount;
        this.samples = samples != null ? samples : Collections.emptyList();
        log.warn("车载节点[{}]存在下游引用，删除被拒绝（引用方={}，引用数量={}）", nodeCode, referencingService, referenceCount);
    }
}
