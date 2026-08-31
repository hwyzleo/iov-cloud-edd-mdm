package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 设备类别名称重复异常（CR-037 §3.2）
 * <p>
 * 中英文名称标准化（trim/折叠空白/case-fold/全半角空格归一）后与现存类别完全相同时抛出。
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class DeviceCategoryNameDuplicateException extends MdmBaseException {

    private final String code;
    private final String duplicateCode;
    private final String name;
    private final String nameLocal;

    public DeviceCategoryNameDuplicateException(String code, String duplicateCode, String name, String nameLocal) {
        super(MdmErrorCode.DEVICE_CATEGORY_NAME_DUPLICATE,
                String.format("设备类别中英文名称标准化后与现存类别重复: code=%s, name=%s, nameLocal=%s, 现存类别=%s",
                        code, name, nameLocal, duplicateCode));
        this.code = code;
        this.duplicateCode = duplicateCode;
        this.name = name;
        this.nameLocal = nameLocal;
        log.warn("设备类别名称重复: code={}, name={}, nameLocal={}, 现存类别={}",
                code, name, nameLocal, duplicateCode);
    }
}
