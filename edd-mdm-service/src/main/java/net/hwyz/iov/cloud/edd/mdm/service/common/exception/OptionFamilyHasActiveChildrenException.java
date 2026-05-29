package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 选项族下存在活跃选项码异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class OptionFamilyHasActiveChildrenException extends MdmBaseException {

    private final String optionFamilyCode;
    private final long activeChildCount;

    public OptionFamilyHasActiveChildrenException(String optionFamilyCode, long activeChildCount) {
        super(MdmErrorCode.OPTION_FAMILY_HAS_ACTIVE_CHILDREN, String.format("选项族 %s 下存在活跃选项码，失效被拒绝（活跃选项码数量: %d）", optionFamilyCode, activeChildCount));
        this.optionFamilyCode = optionFamilyCode;
        this.activeChildCount = activeChildCount;
        log.warn("选项族[{}]下存在活跃选项码[{}]，失效被拒绝", optionFamilyCode, activeChildCount);
    }
}
