package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 车系下存在活跃车型异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class CarLineHasActiveChildrenException extends MdmBaseException {

    private final String carLineCode;
    private final long activeChildCount;

    public CarLineHasActiveChildrenException(String carLineCode, long activeChildCount) {
        super(MdmErrorCode.CARLINE_HAS_ACTIVE_CHILDREN, String.format("车系 %s 下存在活跃车型，失效被拒绝（活跃车型数量: %d）", carLineCode, activeChildCount));
        this.carLineCode = carLineCode;
        this.activeChildCount = activeChildCount;
        log.warn("车系[{}]下存在活跃车型[{}]，失效被拒绝", carLineCode, activeChildCount);
    }
}
