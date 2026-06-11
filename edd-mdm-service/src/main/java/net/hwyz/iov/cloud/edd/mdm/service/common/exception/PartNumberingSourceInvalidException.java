package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 发号来源无效异常
 * CR-023 新增
 */
@Slf4j
@Getter
public class PartNumberingSourceInvalidException extends MdmBaseException {

    private final String invalidSource;

    public PartNumberingSourceInvalidException(String invalidSource) {
        super(MdmErrorCode.PART_NUMBERING_SOURCE_INVALID, String.format("发号来源为空或取值不在枚举范围: %s", invalidSource));
        this.invalidSource = invalidSource;
        log.warn("发号来源为空或取值不在枚举范围: {}", invalidSource);
    }

    public PartNumberingSourceInvalidException(String invalidSource, Throwable cause) {
        super(MdmErrorCode.PART_NUMBERING_SOURCE_INVALID, String.format("发号来源为空或取值不在枚举范围: %s", invalidSource));
        this.invalidSource = invalidSource;
        log.warn("发号来源为空或取值不在枚举范围: {}", invalidSource, cause);
    }
}
