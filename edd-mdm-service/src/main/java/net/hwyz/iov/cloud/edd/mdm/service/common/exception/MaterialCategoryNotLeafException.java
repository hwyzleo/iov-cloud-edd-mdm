package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 物料分类非叶子异常（CR-039 §6/§7）
 * <p>
 * Part.categoryCode 指向 L1/L2 或其他非叶子物料分类（深度 != 3 或存在 ACTIVE 子节点）时抛出。
 * 叶子判定：深度=3 且无 ACTIVE 子节点。
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class MaterialCategoryNotLeafException extends MdmBaseException {

    private final String categoryCode;
    private final int depth;
    private final long activeChildCount;

    public MaterialCategoryNotLeafException(String categoryCode, int depth, long activeChildCount) {
        super(MdmErrorCode.MATERIAL_CATEGORY_NOT_LEAF,
                String.format("物料分类 %s 不是可归类叶子（深度=%d，ACTIVE 子节点=%d），Part 仅可引用 L3 叶子",
                        categoryCode, depth, activeChildCount));
        this.categoryCode = categoryCode;
        this.depth = depth;
        this.activeChildCount = activeChildCount;
        log.warn("物料分类[{}]非叶子: depth={}, activeChildCount={}", categoryCode, depth, activeChildCount);
    }
}
