package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 选项族名称重复异常
 * <p>
 * 中英文名称标准化（trim/折叠空白/case-fold/全半角空格归一）后与现存族完全相同时抛出。
 * CR-035 新增。
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class OptionFamilyNameDuplicateException extends MdmBaseException {

    private final String code;
    private final String duplicateCode;
    private final String name;
    private final String nameLocal;

    public OptionFamilyNameDuplicateException(String code, String duplicateCode, String name, String nameLocal) {
        super(MdmErrorCode.OPTION_FAMILY_NAME_DUPLICATE,
                String.format("选项族中英文名称标准化后与现存族重复: code=%s, name=%s, nameLocal=%s, 现存族=%s",
                        code, name, nameLocal, duplicateCode));
        this.code = code;
        this.duplicateCode = duplicateCode;
        this.name = name;
        this.nameLocal = nameLocal;
        log.warn("选项族名称重复: code={}, name={}, nameLocal={}, 现存族={}",
                code, name, nameLocal, duplicateCode);
    }
}
