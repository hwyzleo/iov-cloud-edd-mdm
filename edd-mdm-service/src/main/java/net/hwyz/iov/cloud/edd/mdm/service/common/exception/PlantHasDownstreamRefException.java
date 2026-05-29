package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * 工厂存在下游引用异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class PlantHasDownstreamRefException extends MdmBaseException {

    private final String plantCode;
    private final String referencingService;
    private final long referenceCount;
    private final List<String> samples;

    public PlantHasDownstreamRefException(String plantCode, String referencingService,
                                          long referenceCount, List<String> samples) {
        super(MdmErrorCode.PLANT_HAS_DOWNSTREAM_REF, String.format("工厂 %s 存在下游引用，删除被拒绝（引用方=%s，引用数量=%d）",
                plantCode, referencingService, referenceCount));
        this.plantCode = plantCode;
        this.referencingService = referencingService;
        this.referenceCount = referenceCount;
        this.samples = samples != null ? samples : Collections.emptyList();
        log.warn("工厂[{}]存在下游引用，删除被拒绝（引用方={}，引用数量={}）", plantCode, referencingService, referenceCount);
    }
}
