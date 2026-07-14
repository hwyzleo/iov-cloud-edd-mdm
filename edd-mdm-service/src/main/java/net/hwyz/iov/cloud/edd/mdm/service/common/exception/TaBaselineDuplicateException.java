package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * TA基线重复异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class TaBaselineDuplicateException extends MdmBaseException {

    public TaBaselineDuplicateException(String swinCode, String anchorType, String anchorCode, String projectionDigest) {
        super(MdmErrorCode.TA_BASELINE_DUPLICATE,
                String.format("同锚点+同projection_digest重复: swinCode=%s, anchor=%s:%s, digest=%s",
                        swinCode, anchorType, anchorCode, projectionDigest));
        log.warn("TA基线重复: swinCode={}, anchor={}:{}, digest={}", swinCode, anchorType, anchorCode, projectionDigest);
    }
}
