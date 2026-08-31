package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.DeviceCategory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 设备类别名称策略（CR-037 §3.2）
 * <p>
 * 中英文名称标准化与精确防重：
 * <ol>
 *   <li>trim 首尾空白</li>
 *   <li>连续空白折叠为一个空格</li>
 *   <li>英文名称使用 Unicode case-fold（全角空格/不间断空格先归一为半角）</li>
 *   <li>中文名称移除全角/半角空格差异</li>
 *   <li>与 DRAFT/ACTIVE/INACTIVE 且 row_valid=1 的现存类别比对，标准化后完全相同则拒绝创建</li>
 * </ol>
 * 常用缩写仅用于候选提示，不做机器自动合并（治理人员确认后决定复用或新建）。
 *
 * @author hwyz_leo
 */
@Component
public class DeviceCategoryNamePolicy {

    /**
     * 全角空格
     */
    private static final char FULL_WIDTH_SPACE = '\u3000';

    /**
     * 不间断空格
     */
    private static final char NON_BREAKING_SPACE = '\u00A0';

    /**
     * CJK 统一表意文字（用于识别中文名称）
     */
    private static final Pattern CJK_PATTERN = Pattern.compile("[\\p{IsHan}]");

    /**
     * 名称标准化
     *
     * @param name 原始名称（可为 null）
     * @return 标准化名称；入参为 null 时返回 null
     */
    public String normalize(String name) {
        if (name == null) {
            return null;
        }
        // 1. 全角/不间断空格归一为半角空格
        String normalized = name.replace(FULL_WIDTH_SPACE, ' ').replace(NON_BREAKING_SPACE, ' ');
        // 2. trim 首尾空白
        normalized = normalized.trim();
        // 3. 连续空白折叠为一个空格
        normalized = normalized.replaceAll("\\s+", " ");
        // 4. Unicode case-fold（近似 toLowerCase(ROOT)）
        normalized = normalized.toLowerCase(Locale.ROOT);
        // 5. 中文名称移除空格差异（中文名不使用空格分词）
        if (CJK_PATTERN.matcher(normalized).find()) {
            normalized = normalized.replace(" ", "");
        }
        return normalized;
    }

    /**
     * 在现存类别中查找标准化后名称重复的类别（英文名比对英文名、中文名比对中文名）。
     * 重复时返回被命中的现存类别，否则返回空。
     *
     * @param existingCategories 现存类别（DRAFT/ACTIVE/INACTIVE 且 row_valid=1）
     * @param name               新类别英文名
     * @param nameLocal          新类别中文名
     */
    public Optional<DeviceCategory> findDuplicate(List<DeviceCategory> existingCategories, String name, String nameLocal) {
        if (existingCategories == null || existingCategories.isEmpty()) {
            return Optional.empty();
        }
        String normalizedName = normalize(name);
        String normalizedNameLocal = normalize(nameLocal);
        for (DeviceCategory category : existingCategories) {
            String existingName = normalize(category.getName());
            String existingNameLocal = normalize(category.getNameLocal());
            if (normalizedName != null && normalizedName.equals(existingName)) {
                return Optional.of(category);
            }
            if (normalizedNameLocal != null && normalizedNameLocal.equals(existingNameLocal)) {
                return Optional.of(category);
            }
        }
        return Optional.empty();
    }
}
