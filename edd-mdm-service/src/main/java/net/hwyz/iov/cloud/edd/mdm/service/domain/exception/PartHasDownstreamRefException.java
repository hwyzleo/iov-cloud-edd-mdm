package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

@Getter
public class PartHasDownstreamRefException extends RuntimeException {
    public static final int ERROR_CODE = 814016;
    private final String partCode;
    private final long referenceCount;

    public PartHasDownstreamRefException(String partCode, long referenceCount) {
        super(String.format("零件 %s 存在下游引用，删除被拒绝（引用数量: %d）", partCode, referenceCount));
        this.partCode = partCode;
        this.referenceCount = referenceCount;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
