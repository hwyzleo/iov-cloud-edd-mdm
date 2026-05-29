package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 版本下存在活跃配置异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class VariantHasActiveChildrenException extends MdmBaseException {

    private final String variantCode;
    private final long activeChildCount;

    public VariantHasActiveChildrenException(String variantCode, long activeChildCount) {
        super(MdmErrorCode.VARIANT_HAS_ACTIVE_CHILDREN, String.format("版本 %s 下存在活跃配置，失效被拒绝（活跃配置数量: %d）", variantCode, activeChildCount));
        this.variantCode = variantCode;
        this.activeChildCount = activeChildCount;
        log.warn("版本[{}]下存在活跃配置[{}]，失效被拒绝", variantCode, activeChildCount);
    }
}
