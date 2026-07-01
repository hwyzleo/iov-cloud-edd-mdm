package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * SWIN编码方案非 ACTIVE 状态异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class SwinSchemeNotActiveException extends MdmBaseException {

    private final String code;
    private final String status;

    public SwinSchemeNotActiveException(String code, String status) {
        super(MdmErrorCode.SWIN_SCHEME_NOT_ACTIVE,
                String.format("SWIN编码方案 %s 非 ACTIVE 状态（当前: %s），不允许被引用", code, status));
        this.code = code;
        this.status = status;
        log.warn("SWIN编码方案[{}]非ACTIVE状态（当前: {}），不允许被引用", code, status);
    }
}
