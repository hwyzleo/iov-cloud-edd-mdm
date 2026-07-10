package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 软件基线被下游引用，删除被拒绝异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class SoftwareBaselineHasDownstreamRefException extends MdmBaseException {

    private final String baselineCode;
    private final long referenceCount;

    public SoftwareBaselineHasDownstreamRefException(String baselineCode, long referenceCount) {
        super(MdmErrorCode.SW_BASELINE_HAS_DOWNSTREAM_REF,
                String.format("软件基线 %s 被下游引用，删除被拒绝（引用数量: %d）", baselineCode, referenceCount));
        this.baselineCode = baselineCode;
        this.referenceCount = referenceCount;
        log.warn("软件基线[{}]被下游引用[{}]，删除被拒绝", baselineCode, referenceCount);
    }
}
