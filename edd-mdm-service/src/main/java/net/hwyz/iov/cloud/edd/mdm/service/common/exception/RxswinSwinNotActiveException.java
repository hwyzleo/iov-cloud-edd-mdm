package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * RXSWIN登记引用的swinCode对应TypeSwinDefinition非ACTIVE异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class RxswinSwinNotActiveException extends MdmBaseException {

    private final String swinCode;
    private final String currentStatus;

    public RxswinSwinNotActiveException(String swinCode, String currentStatus) {
        super(MdmErrorCode.RXSWIN_SWIN_NOT_ACTIVE,
                String.format("登记引用的swinCode %s 对应TypeSwinDefinition存在但非ACTIVE（当前状态: %s）", swinCode, currentStatus));
        this.swinCode = swinCode;
        this.currentStatus = currentStatus;
        log.warn("RXSWIN登记swinCode非ACTIVE: swinCode={}, status={}", swinCode, currentStatus);
    }
}
