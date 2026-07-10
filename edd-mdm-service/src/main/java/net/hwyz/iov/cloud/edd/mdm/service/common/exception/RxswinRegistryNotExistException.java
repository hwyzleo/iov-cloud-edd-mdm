package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * RXSWIN登记记录不存在异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class RxswinRegistryNotExistException extends MdmBaseException {

    private final String manifestCode;

    public RxswinRegistryNotExistException(String manifestCode) {
        super(MdmErrorCode.RXSWIN_REGISTRY_NOT_EXIST,
                String.format("RXSWIN登记记录不存在: manifestCode=%s", manifestCode));
        this.manifestCode = manifestCode;
        log.warn("RXSWIN登记记录不存在: manifestCode={}", manifestCode);
    }
}
