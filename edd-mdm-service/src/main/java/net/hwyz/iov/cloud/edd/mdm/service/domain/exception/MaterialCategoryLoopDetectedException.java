package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

@Getter
public class MaterialCategoryLoopDetectedException extends RuntimeException {
    public static final int ERROR_CODE = 814006;
    private final String categoryCode;
    private final String parentCode;

    public MaterialCategoryLoopDetectedException(String categoryCode, String parentCode) {
        super(String.format("物料分类层级存在循环: 分类 %s 不能将 %s 设为父级", categoryCode, parentCode));
        this.categoryCode = categoryCode;
        this.parentCode = parentCode;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
