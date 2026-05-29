package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

@Getter
public class MaterialCategoryParentNotExistException extends RuntimeException {
    public static final int ERROR_CODE = 814005;
    private final String parentCode;

    public MaterialCategoryParentNotExistException(String parentCode) {
        super(String.format("父级物料分类不存在: %s", parentCode));
        this.parentCode = parentCode;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
