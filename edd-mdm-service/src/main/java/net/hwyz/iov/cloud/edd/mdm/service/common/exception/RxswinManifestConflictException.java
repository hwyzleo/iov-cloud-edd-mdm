package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * RXSWIN manifest冲突异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class RxswinManifestConflictException extends MdmBaseException {

    private final String manifestCode;
    private final String existingSwinCode;
    private final String existingManifestDigest;
    private final String requestSwinCode;
    private final String requestManifestDigest;

    public RxswinManifestConflictException(String manifestCode,
                                            String existingSwinCode, String existingManifestDigest,
                                            String requestSwinCode, String requestManifestDigest) {
        super(MdmErrorCode.RXSWIN_MANIFEST_CONFLICT,
                String.format("manifestCode %s 的 swinCode/manifestDigest/softwareBaselineCode 冲突：已登记 swinCode=%s digest=%s，请求 swinCode=%s digest=%s",
                        manifestCode, existingSwinCode, existingManifestDigest, requestSwinCode, requestManifestDigest));
        this.manifestCode = manifestCode;
        this.existingSwinCode = existingSwinCode;
        this.existingManifestDigest = existingManifestDigest;
        this.requestSwinCode = requestSwinCode;
        this.requestManifestDigest = requestManifestDigest;
        log.warn("RXSWIN manifest冲突: manifestCode={}, 已登记 swinCode={} digest={}, 请求 swinCode={} digest={}",
                manifestCode, existingSwinCode, existingManifestDigest, requestSwinCode, requestManifestDigest);
    }
}
