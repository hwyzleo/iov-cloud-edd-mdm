package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

@Getter
public class BrandHasActiveChildrenException extends RuntimeException {
    public static final int ERROR_CODE = 807016;
    private final String brandCode;
    private final long activeChildCount;

    public BrandHasActiveChildrenException(String brandCode, long activeChildCount) {
        super(String.format("品牌 %s 下存在活跃车系，失效被拒绝（活跃车系数量: %d）", brandCode, activeChildCount));
        this.brandCode = brandCode;
        this.activeChildCount = activeChildCount;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
