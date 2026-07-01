package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * SWIN编码方案被SWIN定义引用异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class SwinSchemeHasReferenceException extends MdmBaseException {

    private final String schemeCode;
    private final long referenceCount;

    public SwinSchemeHasReferenceException(String schemeCode, long referenceCount) {
        super(MdmErrorCode.SWIN_SCHEME_HAS_REFERENCE,
                String.format("SWIN编码方案 %s 被SWIN定义引用，删除被拒绝（引用数量: %d）", schemeCode, referenceCount));
        this.schemeCode = schemeCode;
        this.referenceCount = referenceCount;
        log.warn("SWIN编码方案[{}]被SWIN定义引用，删除被拒绝（引用数量={}）", schemeCode, referenceCount);
    }
}
