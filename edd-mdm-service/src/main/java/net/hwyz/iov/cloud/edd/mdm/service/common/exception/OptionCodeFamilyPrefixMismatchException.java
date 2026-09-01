package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 选项码 code 与所属选项族派生主干不一致异常
 * <p>
 * 新建 code 格式合法，但未以所属 OptionFamily.code 派生主干
 * （将 OF_ 替换为 OC_ 并追加 _&lt;VALUE&gt;）开头时抛出。
 * CR-040 新增，映射错误码 812128。
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class OptionCodeFamilyPrefixMismatchException extends MdmBaseException {

    private final String code;
    private final String optionFamilyCode;

    public OptionCodeFamilyPrefixMismatchException(String code, String optionFamilyCode) {
        super(MdmErrorCode.OPTION_CODE_FAMILY_PREFIX_MISMATCH,
                String.format("选项码 code 派生主干与所属选项族不一致: code=%s, optionFamilyCode=%s",
                        code, optionFamilyCode));
        this.code = code;
        this.optionFamilyCode = optionFamilyCode;
        log.warn("选项码 code 与所属选项族派生主干不一致: code={}, optionFamilyCode={}",
                code, optionFamilyCode);
    }
}
