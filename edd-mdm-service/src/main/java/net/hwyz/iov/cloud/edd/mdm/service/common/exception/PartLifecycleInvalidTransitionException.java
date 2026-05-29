package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 零件 lifecycleStage 状态机逆向跳转或 OBSOLETE 终态变更异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class PartLifecycleInvalidTransitionException extends MdmBaseException {

    private final String currentStage;
    private final String targetStage;

    public PartLifecycleInvalidTransitionException(String currentStage, String targetStage) {
        super(MdmErrorCode.PART_LIFECYCLE_INVALID_TRANSITION, String.format("零件生命周期状态转换无效: %s -> %s", currentStage, targetStage));
        this.currentStage = currentStage;
        this.targetStage = targetStage;
        log.warn("零件生命周期状态转换无效: {} -> {}", currentStage, targetStage);
    }
}
