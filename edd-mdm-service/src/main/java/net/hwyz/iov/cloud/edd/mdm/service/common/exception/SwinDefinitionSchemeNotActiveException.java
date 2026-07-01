package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * SWIN定义关联的编码方案非 ACTIVE 状态异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class SwinDefinitionSchemeNotActiveException extends MdmBaseException {

    private final String schemeCode;
    private final String schemeStatus;

    public SwinDefinitionSchemeNotActiveException(String schemeCode, String schemeStatus) {
        super(MdmErrorCode.SWIN_DEFINITION_SCHEME_NOT_ACTIVE,
                String.format("SWIN定义关联的编码方案 %s 非 ACTIVE 状态（当前: %s）", schemeCode, schemeStatus));
        this.schemeCode = schemeCode;
        this.schemeStatus = schemeStatus;
        log.warn("SWIN定义关联的编码方案[{}]非ACTIVE状态（当前: {}）", schemeCode, schemeStatus);
    }
}
