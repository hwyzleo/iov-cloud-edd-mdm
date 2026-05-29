package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

@Getter
public class PartLifecycleInvalidTransitionException extends RuntimeException {
    public static final int ERROR_CODE = 814015;
    private final String currentStage;
    private final String targetStage;

    public PartLifecycleInvalidTransitionException(String currentStage, String targetStage) {
        super(String.format("零件生命周期状态转换无效: %s -> %s", currentStage, targetStage));
        this.currentStage = currentStage;
        this.targetStage = targetStage;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
