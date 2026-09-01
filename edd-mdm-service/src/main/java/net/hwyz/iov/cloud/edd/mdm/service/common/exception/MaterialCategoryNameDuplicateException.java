package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 物料分类名称重复异常（CR-039 §5.2）
 * <p>
 * 中英文名称标准化（trim/折叠空白/英文 case-fold/中文全半角空白统一）后与现存品类完全相同时抛出。
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class MaterialCategoryNameDuplicateException extends MdmBaseException {

    private final String code;
    private final String duplicateCode;
    private final String name;
    private final String nameLocal;

    public MaterialCategoryNameDuplicateException(String code, String duplicateCode, String name, String nameLocal) {
        super(MdmErrorCode.MATERIAL_CATEGORY_NAME_DUPLICATE,
                String.format("物料分类中英文名称标准化后与现存分类重复: code=%s, name=%s, nameLocal=%s, 现存分类=%s",
                        code, name, nameLocal, duplicateCode));
        this.code = code;
        this.duplicateCode = duplicateCode;
        this.name = name;
        this.nameLocal = nameLocal;
        log.warn("物料分类名称重复: code={}, name={}, nameLocal={}, 现存分类={}",
                code, name, nameLocal, duplicateCode);
    }
}
