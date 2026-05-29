package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 平台下存在活跃车型异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class PlatformHasActiveChildrenException extends MdmBaseException {

    private final String platformCode;
    private final long activeChildCount;

    public PlatformHasActiveChildrenException(String platformCode, long activeChildCount) {
        super(MdmErrorCode.PLATFORM_HAS_ACTIVE_CHILDREN, String.format("平台 %s 下存在活跃车型，失效被拒绝（活跃车型数量: %d）", platformCode, activeChildCount));
        this.platformCode = platformCode;
        this.activeChildCount = activeChildCount;
        log.warn("平台[{}]下存在活跃车型[{}]，失效被拒绝", platformCode, activeChildCount);
    }
}
