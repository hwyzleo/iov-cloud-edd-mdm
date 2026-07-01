package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * SWIN定义被其他实体引用异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class SwinDefinitionHasReferenceException extends MdmBaseException {

    private final String swinCode;
    private final long referenceCount;

    public SwinDefinitionHasReferenceException(String swinCode, long referenceCount) {
        super(MdmErrorCode.SWIN_DEFINITION_HAS_REFERENCE,
                String.format("SWIN定义 %s 被其他实体引用，删除被拒绝（引用数量: %d）", swinCode, referenceCount));
        this.swinCode = swinCode;
        this.referenceCount = referenceCount;
        log.warn("SWIN定义[{}]被其他实体引用，删除被拒绝（引用数量={}）", swinCode, referenceCount);
    }
}
