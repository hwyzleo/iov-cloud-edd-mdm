package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

@Getter
public class PartSupplierInvalidException extends RuntimeException {
    public static final int ERROR_CODE = 814013;
    private final String supplierCode;

    public PartSupplierInvalidException(String supplierCode) {
        super(String.format("供应商无效: %s", supplierCode));
        this.supplierCode = supplierCode;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
