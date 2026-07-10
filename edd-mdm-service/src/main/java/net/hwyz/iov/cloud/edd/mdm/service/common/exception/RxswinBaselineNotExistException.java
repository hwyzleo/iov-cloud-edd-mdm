package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * RXSWIN softwareBaselineCode不存在异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class RxswinBaselineNotExistException extends MdmBaseException {

    private final String softwareBaselineCode;

    public RxswinBaselineNotExistException(String softwareBaselineCode) {
        super(MdmErrorCode.RXSWIN_BASELINE_NOT_EXIST,
                String.format("softwareBaselineCode对应的软件基线不存在: %s", softwareBaselineCode));
        this.softwareBaselineCode = softwareBaselineCode;
        log.warn("RXSWIN softwareBaselineCode不存在: {}", softwareBaselineCode);
    }
}
