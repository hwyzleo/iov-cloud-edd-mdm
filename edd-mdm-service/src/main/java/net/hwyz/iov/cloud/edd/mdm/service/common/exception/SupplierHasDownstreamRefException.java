package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 供应商存在下游引用异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class SupplierHasDownstreamRefException extends MdmBaseException {

    private final String supplierCode;
    private final long referenceCount;

    public SupplierHasDownstreamRefException(String supplierCode, long referenceCount) {
        super(MdmErrorCode.SUPPLIER_HAS_DOWNSTREAM_REF, String.format("供应商 %s 存在下游引用，删除被拒绝（引用数量: %d）", supplierCode, referenceCount));
        this.supplierCode = supplierCode;
        this.referenceCount = referenceCount;
        log.warn("供应商[{}]存在下游引用[{}]，删除被拒绝", supplierCode, referenceCount);
    }
}
