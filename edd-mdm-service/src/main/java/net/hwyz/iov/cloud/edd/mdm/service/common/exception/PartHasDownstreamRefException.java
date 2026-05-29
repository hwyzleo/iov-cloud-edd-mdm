package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 零件存在下游引用异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class PartHasDownstreamRefException extends MdmBaseException {

    private final String partCode;
    private final long referenceCount;

    public PartHasDownstreamRefException(String partCode, long referenceCount) {
        super(MdmErrorCode.PART_HAS_DOWNSTREAM_REF, String.format("零件 %s 存在下游引用，删除被拒绝（引用数量: %d）", partCode, referenceCount));
        this.partCode = partCode;
        this.referenceCount = referenceCount;
        log.warn("零件[{}]存在下游引用[{}]，删除被拒绝", partCode, referenceCount);
    }
}
