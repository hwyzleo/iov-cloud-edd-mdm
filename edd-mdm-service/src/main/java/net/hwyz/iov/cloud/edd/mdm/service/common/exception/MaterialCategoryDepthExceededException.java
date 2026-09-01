package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 物料分类层级深度超限异常（CR-039 §2/§5.1）
 * <p>
 * 尝试创建第四层或更深节点（新节点 parentCode 链深度 >= 3）时抛出。
 * 规格、材质、尺寸、电压、供应商与 BOM 装配位置等差异不得通过第四层表达。
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class MaterialCategoryDepthExceededException extends MdmBaseException {

    private final String categoryCode;
    private final String parentCode;

    public MaterialCategoryDepthExceededException(String categoryCode, String parentCode) {
        super(MdmErrorCode.MATERIAL_CATEGORY_DEPTH_EXCEEDED,
                String.format("物料分类层级超过最大深度（禁止创建第四层）: %s 的父级 %s 已达最大深度",
                        categoryCode, parentCode));
        this.categoryCode = categoryCode;
        this.parentCode = parentCode;
        log.warn("物料分类层级超限: code={}, parentCode={}", categoryCode, parentCode);
    }
}
