package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * 工厂存在下游引用异常（Org 子域）
 * <p>
 * 错误码：813003 PLANT_HAS_DOWNSTREAM_REF
 *
 * @author hwyz_leo
 */
@Getter
public class PlantHasDownstreamRefException extends RuntimeException {

    public static final int ERROR_CODE = 813003;

    private final String plantCode;
    private final String referencingService;
    private final long referenceCount;
    private final List<String> samples;

    public PlantHasDownstreamRefException(String plantCode, String referencingService,
                                          long referenceCount, List<String> samples) {
        super(String.format("工厂 %s 存在下游引用，删除被拒绝（引用方=%s，引用数量=%d）",
                plantCode, referencingService, referenceCount));
        this.plantCode = plantCode;
        this.referencingService = referencingService;
        this.referenceCount = referenceCount;
        this.samples = samples != null ? samples : Collections.emptyList();
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
