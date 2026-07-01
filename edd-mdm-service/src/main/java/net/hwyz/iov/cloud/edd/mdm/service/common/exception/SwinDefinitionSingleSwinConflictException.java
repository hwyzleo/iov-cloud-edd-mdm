package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 单SWIN路由下已存在同类型的 ACTIVE SWIN定义异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class SwinDefinitionSingleSwinConflictException extends MdmBaseException {

    private final String schemeCode;
    private final String swinType;
    private final String existingSwinCode;

    public SwinDefinitionSingleSwinConflictException(String schemeCode, String swinType, String existingSwinCode) {
        super(MdmErrorCode.SWIN_DEFINITION_SINGLE_SWIN_CONFLICT,
                String.format("单SWIN路由方案 %s 下已存在同类型(%s)的 ACTIVE SWIN定义: %s", schemeCode, swinType, existingSwinCode));
        this.schemeCode = schemeCode;
        this.swinType = swinType;
        this.existingSwinCode = existingSwinCode;
        log.warn("单SWIN路由方案[{}]下已存在同类型({})的ACTIVE SWIN定义: {}", schemeCode, swinType, existingSwinCode);
    }
}
