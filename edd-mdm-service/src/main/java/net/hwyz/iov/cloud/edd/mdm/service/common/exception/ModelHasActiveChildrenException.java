package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 车型下存在活跃版本异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class ModelHasActiveChildrenException extends MdmBaseException {

    private final String modelCode;
    private final long activeChildCount;

    public ModelHasActiveChildrenException(String modelCode, long activeChildCount) {
        super(MdmErrorCode.MODEL_HAS_ACTIVE_CHILDREN, String.format("车型 %s 下存在活跃版本，失效被拒绝（活跃版本数量: %d）", modelCode, activeChildCount));
        this.modelCode = modelCode;
        this.activeChildCount = activeChildCount;
        log.warn("车型[{}]下存在活跃版本[{}]，失效被拒绝", modelCode, activeChildCount);
    }
}
