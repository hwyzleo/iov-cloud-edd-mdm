package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

@Getter
public class CarLineHasActiveChildrenException extends RuntimeException {
    public static final int ERROR_CODE = 807017;
    private final String carLineCode;
    private final long activeChildCount;

    public CarLineHasActiveChildrenException(String carLineCode, long activeChildCount) {
        super(String.format("车系 %s 下存在活跃车型，失效被拒绝（活跃车型数量: %d）", carLineCode, activeChildCount));
        this.carLineCode = carLineCode;
        this.activeChildCount = activeChildCount;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
