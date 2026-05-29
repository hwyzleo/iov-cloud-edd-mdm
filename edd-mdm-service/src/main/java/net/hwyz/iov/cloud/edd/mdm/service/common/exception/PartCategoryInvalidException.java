package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 零件 categoryCode 指向不存在或非 ACTIVE 的 MaterialCategory 异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class PartCategoryInvalidException extends MdmBaseException {

    private final String categoryCode;

    public PartCategoryInvalidException(String categoryCode) {
        super(MdmErrorCode.PART_CATEGORY_INVALID, String.format("物料分类无效: %s", categoryCode));
        this.categoryCode = categoryCode;
        log.warn("物料分类无效: {}", categoryCode);
    }
}
