package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MaterialCategoryCodeFormatInvalidException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * 物料品类编码策略（CR-039 §5.1）
 * <p>
 * 统一层级语义 code 规则：
 * <ul>
 *   <li>L1：MC_&lt;SCOPE_ABBR&gt;；L2：MC_&lt;SCOPE_ABBR&gt;_&lt;DOMAIN_ABBR&gt;；
 *       L3：MC_&lt;SCOPE_ABBR&gt;_&lt;DOMAIN_ABBR&gt;_&lt;FAMILY_SHORT_NAME&gt;；
 *       扩展 L3：MC_&lt;SCOPE_ABBR&gt;_&lt;DOMAIN_ABBR&gt;_X_&lt;SHORT_NAME&gt;</li>
 *   <li>正则：^MC_[A-Z0-9]+(?:_[A-Z0-9]+)*$，新 code 总长度不超过 32 字符</li>
 *   <li>子节点 code 必须以 parentCode + "_" 开头（含跨 scope 挂接拒绝）</li>
 *   <li>Scope/Domain 使用受控缩写（MaterialCategoryAbbreviationRegistry），L3 为简短稳定词</li>
 *   <li>code 创建后不可修改；标准 code 只随 CR/目录版本增加</li>
 * </ul>
 * 输入不自动改写；未知缩写或格式非法抛出 MaterialCategoryCodeFormatInvalidException（812924）。
 *
 * @author hwyz_leo
 */
@Component
@RequiredArgsConstructor
public class MaterialCategoryCodePolicy {

    /**
     * 新建标准/扩展 code 总长度硬限制（CR-039 §5.1；数据库 VARCHAR(64) 保留用于兼容 legacy）
     */
    public static final int CODE_MAX_LENGTH = 32;

    /**
     * 层级语义统一格式：MC_ 前缀，大写字母/数字/下划线分段
     */
    private static final Pattern STANDARD_PATTERN = Pattern.compile(
            "^MC_[A-Z0-9]+(?:_[A-Z0-9]+)*$");

    private final MaterialCategoryAbbreviationRegistry abbreviationRegistry;

    /**
     * 是否为层级语义标准格式（含长度校验）
     */
    public boolean isStandardFormat(String code) {
        return code != null && code.length() <= CODE_MAX_LENGTH && STANDARD_PATTERN.matcher(code).matches();
    }

    /**
     * 是否为扩展叶子 code（包含 _X_ 段）
     */
    public boolean isExtensionCode(String code) {
        if (code == null) {
            return false;
        }
        String[] tokens = code.split("_");
        return tokens.length >= 5 && MaterialCategoryAbbreviationRegistry.EXTENSION_MARKER.equals(tokens[3]);
    }

    /**
     * 解析 Scope 缩写（L1 第二个 token）
     */
    public String resolveScopeAbbreviation(String code) {
        if (code == null) {
            return null;
        }
        String[] tokens = code.split("_");
        return tokens.length >= 2 ? tokens[1] : null;
    }

    /**
     * 解析 Domain 缩写（L2 第三个 token）
     */
    public String resolveDomainAbbreviation(String code) {
        if (code == null) {
            return null;
        }
        String[] tokens = code.split("_");
        return tokens.length >= 3 ? tokens[2] : null;
    }

    /**
     * 校验 code 格式（含父子前缀与受控缩写）
     *
     * @param code      新建品类 code
     * @param parentCode 父品类 code（L1 为空）
     */
    public void validateCodeFormat(String code, String parentCode) {
        if (!isStandardFormat(code)) {
            throw new MaterialCategoryCodeFormatInvalidException(code,
                    "基础格式非法或总长度超过 " + CODE_MAX_LENGTH + " 字符");
        }
        String scope = resolveScopeAbbreviation(code);
        if (!abbreviationRegistry.isScopeAbbreviation(scope)) {
            throw new MaterialCategoryCodeFormatInvalidException(code, "Scope 缩写未受控: " + scope);
        }
        if (parentCode == null || parentCode.isBlank()) {
            // L1：仅 MC_<SCOPE>
            if (code.split("_").length != 2) {
                throw new MaterialCategoryCodeFormatInvalidException(code, "L1 应为 MC_<SCOPE_ABBR>");
            }
            return;
        }
        if (!code.startsWith(parentCode + "_")) {
            throw new MaterialCategoryCodeFormatInvalidException(code, "子节点 code 必须以 parentCode + '_' 开头");
        }
        String[] tokens = code.split("_");
        String domain = resolveDomainAbbreviation(code);
        if (!abbreviationRegistry.isDomainAbbreviation(scope, domain)) {
            throw new MaterialCategoryCodeFormatInvalidException(code, "Domain 缩写未受控: " + domain);
        }
        // L2：MC_<SCOPE>_<DOMAIN>
        if (tokens.length == 3) {
            return;
        }
        // L3 / 扩展 L3：family 为 MC_<SCOPE>_<DOMAIN> 之后的全部 token（允许如 WHEEL_TIRE / TRACTION_BATTERY 的受控多段简短词）
        boolean extension = tokens.length >= 5
                && MaterialCategoryAbbreviationRegistry.EXTENSION_MARKER.equals(tokens[3]);
        String family;
        if (extension) {
            family = String.join("_", Arrays.copyOfRange(tokens, 4, tokens.length));
        } else {
            family = String.join("_", Arrays.copyOfRange(tokens, 3, tokens.length));
        }
        if (!abbreviationRegistry.isValidFamilyShortName(family)) {
            throw new MaterialCategoryCodeFormatInvalidException(code,
                    "L3 Family 词格式非法或长度超限: " + family);
        }
    }
}
