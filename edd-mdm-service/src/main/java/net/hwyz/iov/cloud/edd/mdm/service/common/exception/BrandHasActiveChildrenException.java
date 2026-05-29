package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 品牌下存在活跃车系异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class BrandHasActiveChildrenException extends MdmBaseException {

    private final String brandCode;
    private final long activeChildCount;

    public BrandHasActiveChildrenException(String brandCode, long activeChildCount) {
        super(MdmErrorCode.BRAND_HAS_ACTIVE_CHILDREN, String.format("品牌 %s 下存在活跃车系，失效被拒绝（活跃车系数量: %d）", brandCode, activeChildCount));
        this.brandCode = brandCode;
        this.activeChildCount = activeChildCount;
        log.warn("品牌[{}]下存在活跃车系[{}]，失效被拒绝", brandCode, activeChildCount);
    }
}
