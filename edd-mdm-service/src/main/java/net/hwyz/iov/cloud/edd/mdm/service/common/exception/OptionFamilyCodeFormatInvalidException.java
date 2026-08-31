package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 选项族 code 格式校验异常
 * <p>
 * 新建 code 不符合标准格式（OF_&lt;PREFIX&gt;_&lt;SEMANTIC&gt;）或企业扩展格式（OF_&lt;PREFIX&gt;_X_&lt;SEMANTIC&gt;）时抛出。
 * CR-035 新增。
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class OptionFamilyCodeFormatInvalidException extends MdmBaseException {

    private final String code;

    public OptionFamilyCodeFormatInvalidException(String code) {
        super(MdmErrorCode.OPTION_FAMILY_CODE_FORMAT_INVALID,
                String.format("选项族 code 不符合标准/扩展格式: %s", code));
        this.code = code;
        log.warn("选项族 code 格式校验失败: code={}", code);
    }
}
