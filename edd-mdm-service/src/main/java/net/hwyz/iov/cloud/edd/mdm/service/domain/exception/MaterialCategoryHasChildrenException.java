package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

@Getter
public class MaterialCategoryHasChildrenException extends RuntimeException {
    public static final int ERROR_CODE = 814003;
    private final String categoryCode;
    private final long childCount;

    public MaterialCategoryHasChildrenException(String categoryCode, long childCount) {
        super(String.format("物料分类 %s 存在子分类，删除被拒绝（子分类数量: %d）", categoryCode, childCount));
        this.categoryCode = categoryCode;
        this.childCount = childCount;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
