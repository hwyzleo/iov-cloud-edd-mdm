package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 零件 supplierCode 指向不存在的 Supplier 异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class PartSupplierInvalidException extends MdmBaseException {

    private final String supplierCode;

    public PartSupplierInvalidException(String supplierCode) {
        super(MdmErrorCode.PART_SUPPLIER_INVALID, String.format("供应商无效: %s", supplierCode));
        this.supplierCode = supplierCode;
        log.warn("供应商无效: {}", supplierCode);
    }
}
