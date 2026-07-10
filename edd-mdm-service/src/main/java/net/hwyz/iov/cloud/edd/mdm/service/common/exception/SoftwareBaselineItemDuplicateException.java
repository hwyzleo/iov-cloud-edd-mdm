package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 软件基线项零件重复异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class SoftwareBaselineItemDuplicateException extends MdmBaseException {

    public SoftwareBaselineItemDuplicateException(String baselineCode, String partCode) {
        super(MdmErrorCode.SW_BASELINE_ITEM_DUPLICATE,
                String.format("基线 %s 下零件 %s 已存在", baselineCode, partCode));
        log.warn("软件基线项零件重复: baselineCode={}, partCode={}", baselineCode, partCode);
    }
}
