package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 选项码 code 格式校验异常
 * <p>
 * 新建 code 不符合 OC_&lt;CATEGORY_PREFIX&gt;_&lt;FAMILY_SEMANTIC&gt;_&lt;VALUE&gt; 标准格式、
 * 字符集（仅允许大写字母/数字/单下划线）、VALUE 为空或长度超过 64 字符时抛出。
 * CR-040 新增，映射错误码 812127。
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class OptionCodeFormatInvalidException extends MdmBaseException {

    private final String code;

    public OptionCodeFormatInvalidException(String code) {
        super(MdmErrorCode.OPTION_CODE_FORMAT_INVALID,
                String.format("选项码 code 不符合 OC_ 统一编码格式: %s", code));
        this.code = code;
        log.warn("选项码 code 格式校验失败: code={}", code);
    }
}
