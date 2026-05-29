package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

@Getter
public class VariantHasActiveChildrenException extends RuntimeException {
    public static final int ERROR_CODE = 807021;
    private final String variantCode;
    private final long activeChildCount;

    public VariantHasActiveChildrenException(String variantCode, long activeChildCount) {
        super(String.format("版本 %s 下存在活跃配置，失效被拒绝（活跃配置数量: %d）", variantCode, activeChildCount));
        this.variantCode = variantCode;
        this.activeChildCount = activeChildCount;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
