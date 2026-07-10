package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 软件基线已发布冻结异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class SoftwareBaselineFrozenException extends MdmBaseException {

    public SoftwareBaselineFrozenException(String code) {
        super(MdmErrorCode.SW_BASELINE_FROZEN,
                String.format("软件基线 %s 已发布冻结，不可修改", code));
        log.warn("软件基线已冻结: {}", code);
    }
}
