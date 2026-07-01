package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 车型被SWIN定义引用异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class ModelHasSwinRefException extends MdmBaseException {

    private final String modelCode;
    private final long referenceCount;

    public ModelHasSwinRefException(String modelCode, long referenceCount) {
        super(MdmErrorCode.MODEL_HAS_SWIN_REF,
                String.format("车型 %s 被SWIN定义引用，删除被拒绝（引用数量: %d）", modelCode, referenceCount));
        this.modelCode = modelCode;
        this.referenceCount = referenceCount;
        log.warn("车型[{}]被SWIN定义引用，删除被拒绝（引用数量={}）", modelCode, referenceCount);
    }
}
