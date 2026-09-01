package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionCodeFamilyPrefixMismatchException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionCodeFormatInvalidException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 选项码编码策略（CR-040）
 * <p>
 * 统一编码、格式与所属族主干一致性规则：
 * <ul>
 *   <li>标准格式：OC_&lt;CATEGORY_PREFIX&gt;_&lt;FAMILY_SEMANTIC&gt;_&lt;VALUE&gt;</li>
 *   <li>主干派生：将所属 OptionFamily.code 的 OF_ 替换为 OC_，再追加 _&lt;VALUE&gt;</li>
 *   <li>仅允许大写英文字母、数字和单下划线；禁止连续下划线、首尾下划线、空格、连字符和非 ASCII 字符</li>
 *   <li>code 必须为全大写；接口不自动改写输入，故小写输入直接判定格式非法</li>
 * </ul>
 * 新建路径（MPT OptionCode 创建 / Kafka / Feign 上游 ingest / 批量导入）必须复用本策略，
 * 由调用方在 Repository 写入前统一执行。
 *
 * @author hwyz_leo
 */
@Component
public class OptionCodeCodePolicy {

    /**
     * code 长度沿用现有字段上限
     */
    public static final int CODE_MAX_LENGTH = 64;

    /**
     * 标准格式：OC_&lt;PREFIX&gt;_&lt;FAMILY_SEMANTIC&gt;_&lt;VALUE&gt;
     * <p>
     * 含标准族与 _X_ 扩展族派生的 code（如 OC_EXT_X_SPECIAL_PAINT_MATTE_GRAY，
     * 因 _X_ 之后仍由 [A-Z0-9]+(?:_[A-Z0-9]+)* 覆盖）。
     */
    private static final Pattern STANDARD_PATTERN = Pattern.compile(
            "^OC_(EXT|INT|PWR|CHS|SMART|COMF|SAFE|ACC|OTH)_[A-Z0-9]+(?:_[A-Z0-9]+)*$");

    /**
     * 仅前缀无 VALUE 的结构（如 OC_EXT / OC_EXT_ / OC_PWR_），用于存量审计空 VALUE 识别
     */
    private static final Pattern PREFIX_ONLY_PATTERN = Pattern.compile(
            "^OC_(EXT|INT|PWR|CHS|SMART|COMF|SAFE|ACC|OTH)_?$");

    /**
     * 非法字符集（非 ASCII 大写字母、数字、下划线的任意字符）
     */
    private static final Pattern INVALID_CHARSET_PATTERN = Pattern.compile("[^A-Z0-9_]");

    /**
     * 连续下划线或首尾下划线
     */
    private static final Pattern CONSECUTIVE_OR_EDGE_UNDERSCORE_PATTERN = Pattern.compile(
            "_{2,}|^_|_$");

    /**
     * 是否为合法格式 code（含标准族与 _X_ 扩展族派生结果）
     */
    public boolean isValidFormat(String code) {
        return code != null && STANDARD_PATTERN.matcher(code).matches();
    }

    /**
     * 校验 code 格式，非法时抛出 OptionCodeFormatInvalidException（812127）
     * <p>
     * 校验正则、字符集、VALUE 非空及 64 字符上限；接口不自动转大写或重写输入。
     */
    public void validateFormat(String code) {
        if (code == null || code.isBlank() || code.length() > CODE_MAX_LENGTH || !isValidFormat(code)) {
            throw new OptionCodeFormatInvalidException(code);
        }
    }

    /**
     * 根据所属 OptionFamily.code 派生期望主干（OF_ → OC_，追加尾部分隔下划线）
     * <p>
     * 仅接受以 OF_ 开头的族 code；legacy 族（非 OF_ 格式）无法派生，返回 null。
     *
     * @param optionFamilyCode 所属选项族 code
     * @return 派生主干（如 OF_EXT_BODY_COLOR → OC_EXT_BODY_COLOR_）；无法派生返回 null
     */
    public String deriveExpectedStem(String optionFamilyCode) {
        if (optionFamilyCode == null || !optionFamilyCode.startsWith("OF_") || optionFamilyCode.length() <= 3) {
            return null;
        }
        return "OC_" + optionFamilyCode.substring(3) + "_";
    }

    /**
     * 校验 code 与所属族派生主干完全一致，不一致或无法派生时抛出
     * OptionCodeFamilyPrefixMismatchException（812128）
     * <p>
     * code 必须以派生主干开头且长度大于主干（保证 VALUE 非空）。
     */
    public void validateFamilyMatch(String code, String optionFamilyCode) {
        String expectedStem = deriveExpectedStem(optionFamilyCode);
        boolean matched = expectedStem != null
                && code != null
                && code.startsWith(expectedStem)
                && code.length() > expectedStem.length();
        if (!matched) {
            throw new OptionCodeFamilyPrefixMismatchException(code, optionFamilyCode);
        }
    }

    /**
     * 组合校验（格式 + 所属族主干一致性），供新建路径在 Repository 写入前统一调用
     * <p>
     * 全局唯一检查由调用方（AppService）在持有 Repository 的上下文中执行（812101）。
     */
    public void validateCreate(String code, String optionFamilyCode) {
        validateFormat(code);
        validateFamilyMatch(code, optionFamilyCode);
    }

    /**
     * 是否包含非法字符集（小写、空格、连字符、非 ASCII 等），供存量审计识别
     */
    public boolean containsInvalidCharset(String code) {
        return code != null && INVALID_CHARSET_PATTERN.matcher(code).find();
    }

    /**
     * 是否含连续下划线或首尾下划线，供存量审计识别
     */
    public boolean hasConsecutiveOrEdgeUnderscores(String code) {
        return code != null && CONSECUTIVE_OR_EDGE_UNDERSCORE_PATTERN.matcher(code).find();
    }

    /**
     * 是否仅有分类前缀而无 VALUE 段（如 OC_EXT / OC_EXT_ / OC_PWR_），供存量审计识别空 VALUE
     */
    public boolean isPrefixOnly(String code) {
        return code != null && PREFIX_ONLY_PATTERN.matcher(code).matches();
    }
}
