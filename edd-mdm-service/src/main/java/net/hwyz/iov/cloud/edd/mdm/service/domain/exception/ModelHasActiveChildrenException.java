package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

@Getter
public class ModelHasActiveChildrenException extends RuntimeException {
    public static final int ERROR_CODE = 807019;
    private final String modelCode;
    private final long activeChildCount;

    public ModelHasActiveChildrenException(String modelCode, long activeChildCount) {
        super(String.format("车型 %s 下存在活跃版本，失效被拒绝（活跃版本数量: %d）", modelCode, activeChildCount));
        this.modelCode = modelCode;
        this.activeChildCount = activeChildCount;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
