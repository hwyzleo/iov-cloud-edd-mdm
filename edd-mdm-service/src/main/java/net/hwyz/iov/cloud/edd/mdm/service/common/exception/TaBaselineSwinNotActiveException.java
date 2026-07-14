package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * TA基线引用的SWIN不存在或非ACTIVE异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class TaBaselineSwinNotActiveException extends MdmBaseException {

    public TaBaselineSwinNotActiveException(String swinCode) {
        super(MdmErrorCode.TA_BASELINE_SWIN_NOT_ACTIVE,
                String.format("引用的SWIN %s 不存在或非ACTIVE", swinCode));
        log.warn("TA基线引用的SWIN不存在或非ACTIVE: {}", swinCode);
    }
}
