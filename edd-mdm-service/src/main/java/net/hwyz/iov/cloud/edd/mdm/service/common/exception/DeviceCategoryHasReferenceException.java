package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 设备类别被车载节点引用异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class DeviceCategoryHasReferenceException extends MdmBaseException {

    private final String categoryCode;
    private final long referenceCount;

    public DeviceCategoryHasReferenceException(String categoryCode, long referenceCount) {
        super(MdmErrorCode.DEVICE_CATEGORY_HAS_REFERENCE,
                String.format("设备类别 %s 被车载节点引用，删除被拒绝（引用数量: %d）", categoryCode, referenceCount));
        this.categoryCode = categoryCode;
        this.referenceCount = referenceCount;
        log.warn("设备类别[{}]被车载节点引用，删除被拒绝（引用数量={}）", categoryCode, referenceCount);
    }
}
