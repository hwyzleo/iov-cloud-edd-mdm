package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 零件 substitutePartCode 指向不存在或指向自身异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class PartSubstituteInvalidException extends MdmBaseException {

    private final String substitutePartCode;

    public PartSubstituteInvalidException(String substitutePartCode) {
        super(MdmErrorCode.PART_SUBSTITUTE_INVALID, String.format("替代零件无效: %s", substitutePartCode));
        this.substitutePartCode = substitutePartCode;
        log.warn("替代零件无效: {}", substitutePartCode);
    }
}
