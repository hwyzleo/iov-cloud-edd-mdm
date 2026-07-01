package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * SWIN定义引用的Variant或Model非 ACTIVE 状态异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class SwinDefinitionTypeRefNotActiveException extends MdmBaseException {

    private final String refType;
    private final String refCode;
    private final String refStatus;

    public SwinDefinitionTypeRefNotActiveException(String refType, String refCode, String refStatus) {
        super(MdmErrorCode.SWIN_DEFINITION_TYPE_REF_NOT_ACTIVE,
                String.format("SWIN定义引用的%s %s 非 ACTIVE 状态（当前: %s）", refType, refCode, refStatus));
        this.refType = refType;
        this.refCode = refCode;
        this.refStatus = refStatus;
        log.warn("SWIN定义引用的{}[{}]非ACTIVE状态（当前: {}）", refType, refCode, refStatus);
    }
}
