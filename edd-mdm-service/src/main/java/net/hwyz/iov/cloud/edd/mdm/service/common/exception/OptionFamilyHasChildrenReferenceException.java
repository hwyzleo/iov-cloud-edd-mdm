package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 选项族下存在选项码，删除被拒绝异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class OptionFamilyHasChildrenReferenceException extends MdmBaseException {

    private final String optionFamilyCode;
    private final long childCount;

    public OptionFamilyHasChildrenReferenceException(String optionFamilyCode, long childCount) {
        super(MdmErrorCode.HAS_CHILDREN_REFERENCE,
                String.format("选项族 %s 下存在选项码，删除被拒绝（选项码数量: %d）", optionFamilyCode, childCount));
        this.optionFamilyCode = optionFamilyCode;
        this.childCount = childCount;
        log.warn("选项族[{}]下存在选项码[{}]，删除被拒绝", optionFamilyCode, childCount);
    }
}
