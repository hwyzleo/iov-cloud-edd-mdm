package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MaterialCategoryDepthExceededException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MaterialCategoryLoopDetectedException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.MaterialCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.MaterialCategoryStatus;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 物料品类层级策略（CR-039 §2）
 * <p>
 * 校验 L1/L2/L3 父子关系、最大深度与环路：
 * <ul>
 *   <li>层级由 parentCode 链实时判定，最大深度为 3；是否叶子由 ACTIVE 子节点存在性判定</li>
 *   <li>L1 parentCode 为空；L2 只能指向 L1；L3 只能指向 L2</li>
 *   <li>禁止创建第四层或更深节点（返回 812926）</li>
 *   <li>更新父级时校验不形成环路（返回 812906）</li>
 * </ul>
 *
 * @author hwyz_leo
 */
@Component
public class MaterialCategoryHierarchyPolicy {

    /**
     * 标准目录最大深度（三级）
     */
    public static final int MAX_DEPTH = 3;

    /**
     * 沿 parentCode 链计算节点深度（L1=1，L2=2，L3=3）。
     * 链断裂（父节点不存在）时停止计算；链路成环时抛出 812906。
     *
     * @param byCode 现存品类索引（code → 品类，row_valid=1）
     * @param code   目标品类 code
     * @return 深度；code 为空返回 0
     */
    public int computeDepth(Map<String, MaterialCategory> byCode, String code) {
        if (code == null) {
            return 0;
        }
        int depth = 0;
        Set<String> visited = new HashSet<>();
        String current = code;
        while (current != null && !current.isBlank()) {
            if (!visited.add(current)) {
                throw new MaterialCategoryLoopDetectedException(code, current);
            }
            MaterialCategory category = byCode == null ? null : byCode.get(current);
            if (category == null) {
                break;
            }
            depth++;
            current = category.getParentCode();
        }
        return depth;
    }

    /**
     * 校验在指定父级下创建子节点不会超过最大深度（创建第四层返回 812926）
     *
     * @param byCode     现存品类索引
     * @param parentCode 目标父级 code（L1 为空）
     */
    public void assertNotExceedingDepth(Map<String, MaterialCategory> byCode, String parentCode) {
        if (parentCode == null || parentCode.isBlank()) {
            return;
        }
        int parentDepth = computeDepth(byCode, parentCode);
        if (parentDepth >= MAX_DEPTH) {
            throw new MaterialCategoryDepthExceededException("", parentCode);
        }
    }

    /**
     * 校验把父级改为 newParentCode 后不形成环路（更新场景，返回 812906）
     *
     * @param byCode        现存品类索引
     * @param code          被更新品类 code
     * @param newParentCode 新父级 code
     */
    public void assertNoLoop(Map<String, MaterialCategory> byCode, String code, String newParentCode) {
        if (newParentCode == null || newParentCode.isBlank() || code == null) {
            return;
        }
        Set<String> visited = new HashSet<>();
        visited.add(code);
        String current = newParentCode;
        while (current != null && !current.isBlank()) {
            if (visited.contains(current)) {
                throw new MaterialCategoryLoopDetectedException(code, newParentCode);
            }
            visited.add(current);
            MaterialCategory category = byCode == null ? null : byCode.get(current);
            if (category == null) {
                break;
            }
            current = category.getParentCode();
        }
    }

    /**
     * 是否存在 ACTIVE 子节点（叶子判定依据）
     */
    public boolean hasActiveChildren(Map<String, MaterialCategory> byCode, String code) {
        if (byCode == null || code == null) {
            return false;
        }
        for (MaterialCategory category : byCode.values()) {
            if (code.equals(category.getParentCode())
                    && category.getStatus() == MaterialCategoryStatus.ACTIVE) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否为可归类叶子：深度=3 且无 ACTIVE 子节点
     */
    public boolean isLeaf(Map<String, MaterialCategory> byCode, String code) {
        return computeDepth(byCode, code) == MAX_DEPTH && !hasActiveChildren(byCode, code);
    }
}
