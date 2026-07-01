package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 版本被SWIN定义引用异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class VariantHasSwinRefException extends MdmBaseException {

    private final String variantCode;
    private final long referenceCount;

    public VariantHasSwinRefException(String variantCode, long referenceCount) {
        super(MdmErrorCode.VARIANT_HAS_SWIN_REF,
                String.format("版本 %s 被SWIN定义引用，删除被拒绝（引用数量: %d）", variantCode, referenceCount));
        this.variantCode = variantCode;
        this.referenceCount = referenceCount;
        log.warn("版本[{}]被SWIN定义引用，删除被拒绝（引用数量={}）", variantCode, referenceCount);
    }
}
