package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

@Getter
public class OptionFamilyHasActiveChildrenException extends RuntimeException {
    public static final int ERROR_CODE = 807022;
    private final String optionFamilyCode;
    private final long activeChildCount;

    public OptionFamilyHasActiveChildrenException(String optionFamilyCode, long activeChildCount) {
        super(String.format("选项族 %s 下存在活跃选项码，失效被拒绝（活跃选项码数量: %d）", optionFamilyCode, activeChildCount));
        this.optionFamilyCode = optionFamilyCode;
        this.activeChildCount = activeChildCount;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
