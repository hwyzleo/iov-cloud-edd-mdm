package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 物料分类 code 格式校验异常（CR-039 §5.1）
 * <p>
 * 新建 code 不符合受控层级语义格式（^MC_[A-Z0-9]+(?:_[A-Z0-9]+)*$、总长度不超过 32 字符、
 * 子节点须以 parentCode + "_" 开头）或未使用受控缩写（Scope/Domain/L3 缩写字典外）时抛出。
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class MaterialCategoryCodeFormatInvalidException extends MdmBaseException {

    private final String code;

    public MaterialCategoryCodeFormatInvalidException(String code) {
        super(MdmErrorCode.MATERIAL_CATEGORY_CODE_FORMAT_INVALID,
                String.format("物料分类 code 格式非法或未使用受控缩写: %s", code));
        this.code = code;
        log.warn("物料分类 code 格式校验失败: code={}", code);
    }

    public MaterialCategoryCodeFormatInvalidException(String code, String detail) {
        super(MdmErrorCode.MATERIAL_CATEGORY_CODE_FORMAT_INVALID,
                String.format("物料分类 code 格式非法或未使用受控缩写: %s（%s）", code, detail));
        this.code = code;
        log.warn("物料分类 code 格式校验失败: code={}, detail={}", code, detail);
    }
}
