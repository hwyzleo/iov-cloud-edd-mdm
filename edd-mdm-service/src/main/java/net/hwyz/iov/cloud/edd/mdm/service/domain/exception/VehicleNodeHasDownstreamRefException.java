package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * 车载节点存在下游引用异常（EEAD 子域）
 * <p>
 * 错误码：812003 VEHICLE_NODE_HAS_DOWNSTREAM_REF
 * 错误响应中 SHALL 包含引用记录的统计信息（引用方服务名、引用数量、至多前 10 条引用方业务标识）（requirements US-045）。
 *
 * @author hwyz_leo
 */
@Getter
public class VehicleNodeHasDownstreamRefException extends RuntimeException {

    /**
     * 错误码常量
     */
    public static final int ERROR_CODE = 812003;

    /**
     * 拒绝删除的 nodeCode
     */
    private final String nodeCode;

    /**
     * 引用方服务名（首期范围：edd-vmd）
     */
    private final String referencingService;

    /**
     * 引用数量
     */
    private final long referenceCount;

    /**
     * 引用样本（至多前 10 条引用方业务标识，可空）
     */
    private final List<String> samples;

    public VehicleNodeHasDownstreamRefException(String nodeCode, String referencingService,
                                                long referenceCount, List<String> samples) {
        super(String.format("车载节点 %s 存在下游引用，删除被拒绝（引用方=%s，引用数量=%d）",
                nodeCode, referencingService, referenceCount));
        this.nodeCode = nodeCode;
        this.referencingService = referencingService;
        this.referenceCount = referenceCount;
        this.samples = samples != null ? samples : Collections.emptyList();
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
