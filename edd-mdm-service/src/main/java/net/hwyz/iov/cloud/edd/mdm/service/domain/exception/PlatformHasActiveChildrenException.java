package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

@Getter
public class PlatformHasActiveChildrenException extends RuntimeException {
    public static final int ERROR_CODE = 807018;
    private final String platformCode;
    private final long activeChildCount;

    public PlatformHasActiveChildrenException(String platformCode, long activeChildCount) {
        super(String.format("平台 %s 下存在活跃车型，失效被拒绝（活跃车型数量: %d）", platformCode, activeChildCount));
        this.platformCode = platformCode;
        this.activeChildCount = activeChildCount;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
