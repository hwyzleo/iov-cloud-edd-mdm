package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MaterialCategoryNotLeafException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.PartCategoryInvalidException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.MaterialCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.MaterialCategoryStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.MaterialCategoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 物料品类叶子校验策略（CR-039 §7）
 * <p>
 * 供 Part 创建/更新（categoryCode 实际变化时）复用：目标必须为存在、ACTIVE、深度=3
 * 且无 ACTIVE 子节点的 L3 叶子。
 * <ol>
 *   <li>查询目标，不存在/非 ACTIVE 返回 812911（PART_CATEGORY_INVALID）</li>
 *   <li>沿 parentCode 向上计算深度并验证链完整、无环；深度非 3 返回 812923</li>
 *   <li>查询 ACTIVE/row_valid=1 子节点；存在则返回 812923</li>
 * </ol>
 * 仅在 Part 新建或 categoryCode 实际变化时执行，避免追溯阻断 legacy 普通更新。
 *
 * @author hwyz_leo
 */
@Component
@RequiredArgsConstructor
public class MaterialCategoryLeafPolicy {

    private final MaterialCategoryRepository materialCategoryRepository;
    private final MaterialCategoryHierarchyPolicy hierarchyPolicy;

    /**
     * 校验 Part.categoryCode 指向可归类 L3 叶子，非法时抛出业务异常
     *
     * @param categoryCode Part.categoryCode
     */
    public void assertAssignable(String categoryCode) {
        if (categoryCode == null || categoryCode.isBlank()) {
            throw new PartCategoryInvalidException(String.valueOf(categoryCode));
        }
        MaterialCategory category = materialCategoryRepository.findByCode(categoryCode)
                .orElseThrow(() -> new PartCategoryInvalidException(categoryCode));
        if (category.getStatus() != MaterialCategoryStatus.ACTIVE) {
            throw new PartCategoryInvalidException(categoryCode);
        }
        Map<String, MaterialCategory> byCode = indexByCode();
        int depth = hierarchyPolicy.computeDepth(byCode, categoryCode);
        long activeChildCount = countActiveChildren(byCode, categoryCode);
        if (depth != MaterialCategoryHierarchyPolicy.MAX_DEPTH || activeChildCount > 0) {
            throw new MaterialCategoryNotLeafException(categoryCode, depth, activeChildCount);
        }
    }

    /**
     * 是否存在（无 ACTIVE 子节点的）叶子可用作 Part 归类
     *
     * @param categoryCode 品类 code
     * @return true 表示是可归类叶子
     */
    public boolean isAssignable(String categoryCode) {
        try {
            assertAssignable(categoryCode);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, MaterialCategory> indexByCode() {
        List<MaterialCategory> all = materialCategoryRepository.findAll();
        return all.stream().collect(Collectors.toMap(MaterialCategory::getCode, c -> c, (a, b) -> a));
    }

    private long countActiveChildren(Map<String, MaterialCategory> byCode, String code) {
        long count = 0;
        for (MaterialCategory category : byCode.values()) {
            if (code.equals(category.getParentCode())
                    && category.getStatus() == MaterialCategoryStatus.ACTIVE) {
                count++;
            }
        }
        return count;
    }
}
