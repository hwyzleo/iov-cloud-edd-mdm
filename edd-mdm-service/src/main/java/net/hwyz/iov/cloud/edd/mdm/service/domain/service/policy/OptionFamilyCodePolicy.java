package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionFamilyCategoryPrefixMismatchException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionFamilyCodeFormatInvalidException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionFamilyCategory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * 选项族编码策略（CR-035）
 * <p>
 * 统一编码、分类、语义粒度规则：
 * <ul>
 *   <li>标准族：OF_&lt;PREFIX&gt;_&lt;SEMANTIC_NAME&gt;</li>
 *   <li>企业扩展族：OF_&lt;PREFIX&gt;_X_&lt;SEMANTIC_NAME&gt;</li>
 *   <li>仅允许大写英文字母、数字和下划线；禁止连续下划线、首尾下划线和非 ASCII 字符</li>
 *   <li>code 前缀与 category 固定映射，创建时校验一致性</li>
 * </ul>
 * code 必须为全大写；接口不自动改写输入，故小写输入直接判定格式非法。
 *
 * @author hwyz_leo
 */
@Component
public class OptionFamilyCodePolicy {

    /**
     * code 长度沿用现有字段上限
     */
    public static final int CODE_MAX_LENGTH = 64;

    /**
     * 标准格式：OF_&lt;PREFIX&gt;_&lt;SEMANTIC&gt;
     */
    private static final Pattern STANDARD_PATTERN = Pattern.compile(
            "^OF_(EXT|INT|PWR|SMART|COMF|SAFE|ACC|OTH|CHS)_[A-Z0-9]+(?:_[A-Z0-9]+)*$");

    /**
     * 企业扩展格式：OF_&lt;PREFIX&gt;_X_&lt;SEMANTIC&gt;
     */
    private static final Pattern EXTENSION_PATTERN = Pattern.compile(
            "^OF_(EXT|INT|PWR|SMART|COMF|SAFE|ACC|OTH|CHS)_X_[A-Z0-9]+(?:_[A-Z0-9]+)*$");

    /**
     * 分类前缀固定映射（CR-035 §3.2）
     */
    private static final Map<String, OptionFamilyCategory> PREFIX_CATEGORY_MAP = Map.of(
            "EXT", OptionFamilyCategory.EXTERIOR,
            "INT", OptionFamilyCategory.INTERIOR,
            "PWR", OptionFamilyCategory.POWERTRAIN,
            "CHS", OptionFamilyCategory.CHASSIS,
            "SMART", OptionFamilyCategory.INTELLIGENT,
            "COMF", OptionFamilyCategory.COMFORT,
            "SAFE", OptionFamilyCategory.SAFETY,
            "ACC", OptionFamilyCategory.ACCESSORY,
            "OTH", OptionFamilyCategory.OTHER);

    /**
     * 是否为标准格式 code
     */
    public boolean isStandardFormat(String code) {
        return code != null && STANDARD_PATTERN.matcher(code).matches();
    }

    /**
     * 是否为企业扩展格式 code（包含 _X_ 命名空间）
     */
    public boolean isExtensionFormat(String code) {
        return code != null && EXTENSION_PATTERN.matcher(code).matches();
    }

    /**
     * 是否为合法格式 code（标准或扩展）
     */
    public boolean isValidFormat(String code) {
        return isStandardFormat(code) || isExtensionFormat(code);
    }

    /**
     * 校验 code 格式，非法时抛出 OptionFamilyCodeFormatInvalidException（812124）
     */
    public void validateCodeFormat(String code) {
        if (code == null || code.isBlank() || code.length() > CODE_MAX_LENGTH || !isValidFormat(code)) {
            throw new OptionFamilyCodeFormatInvalidException(code);
        }
    }

    /**
     * 提取 code 分类前缀（OF_&lt;PREFIX&gt;_... 中的第二段），非法 code 返回 null
     */
    public String extractPrefix(String code) {
        if (code == null) {
            return null;
        }
        String[] segments = code.split("_");
        return segments.length >= 2 ? segments[1] : null;
    }

    /**
     * 根据 code 前缀解析期望 category，非法前缀返回 null
     */
    public OptionFamilyCategory categoryForCode(String code) {
        String prefix = extractPrefix(code);
        return prefix == null ? null : PREFIX_CATEGORY_MAP.get(prefix);
    }

    /**
     * 校验 code 前缀与 category 一致性，不一致时抛出 OptionFamilyCategoryPrefixMismatchException（812125）
     */
    public void validateCategoryConsistency(String code, OptionFamilyCategory category) {
        OptionFamilyCategory expected = categoryForCode(code);
        if (expected == null || category == null || expected != category) {
            throw new OptionFamilyCategoryPrefixMismatchException(code, category);
        }
    }
}
