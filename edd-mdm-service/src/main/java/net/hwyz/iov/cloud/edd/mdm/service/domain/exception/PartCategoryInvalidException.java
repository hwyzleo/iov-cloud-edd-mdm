package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

@Getter
public class PartCategoryInvalidException extends RuntimeException {
    public static final int ERROR_CODE = 814011;
    private final String categoryCode;

    public PartCategoryInvalidException(String categoryCode) {
        super(String.format("物料分类无效: %s", categoryCode));
        this.categoryCode = categoryCode;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
