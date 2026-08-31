package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.DeviceCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.DeviceCategoryCatalogEntry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.DeviceCategoryLegacyAuditResult;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.DeviceCategoryCodePolicy;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.DeviceCategoryNamePolicy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备类别存量治理审计（CR-037 §4 / §8）
 * <p>
 * 识别存量 DeviceCategory 中的 legacy code、名称重复、规格化 code 和近义类别候选，
 * 输出审计报告供治理人员人工确认映射；不自动改写、不自动合并、不自动失效。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceCategoryLegacyAudit {

    private final DeviceCategoryCodePolicy codePolicy;
    private final DeviceCategoryNamePolicy namePolicy;

    /**
     * 对现存类别执行存量治理审计
     *
     * @param existing 现存类别（row_valid=1）
     * @param catalog  标准目录条目
     * @return 审计报告
     */
    public DeviceCategoryLegacyAuditResult audit(
            List<DeviceCategory> existing, List<DeviceCategoryCatalogEntry> catalog) {
        DeviceCategoryLegacyAuditResult result = DeviceCategoryLegacyAuditResult.builder()
                .totalExisting(existing == null ? 0 : existing.size())
                .build();

        Map<String, DeviceCategoryCatalogEntry> codeToEntry = new HashMap<>();
        Map<String, String> aliasToCode = new HashMap<>();
        Map<String, String> normalizedNameToCode = new HashMap<>();
        Map<String, String> normalizedNameLocalToCode = new HashMap<>();
        if (catalog != null) {
            for (DeviceCategoryCatalogEntry entry : catalog) {
                codeToEntry.put(entry.getCode(), entry);
                if (entry.getAliases() != null) {
                    for (String alias : entry.getAliases()) {
                        if (alias != null && !alias.isBlank()) {
                            aliasToCode.putIfAbsent(alias.trim().toUpperCase(), entry.getCode());
                        }
                    }
                }
                String nn = namePolicy.normalize(entry.getName());
                if (nn != null) {
                    normalizedNameToCode.put(nn, entry.getCode());
                }
                String nloc = namePolicy.normalize(entry.getNameLocal());
                if (nloc != null) {
                    normalizedNameLocalToCode.put(nloc, entry.getCode());
                }
            }
        }

        if (existing == null) {
            return result;
        }
        for (DeviceCategory category : existing) {
            String code = category.getCode();
            // 1) 近义类别：code 命中标准目录 aliases 同义词
            String byAlias = aliasToCode.get(code.toUpperCase());
            if (byAlias != null) {
                result.addFinding(finding(code, DeviceCategoryLegacyAuditResult.TYPE_NEAR_SYNONYM,
                        "code 命中标准目录 aliases 同义词", byAlias));
            }
            // 2) 规格化 code：携带节点级规格语义
            boolean spec = codePolicy.containsNodeSpec(code);
            if (spec) {
                result.addFinding(finding(code, DeviceCategoryLegacyAuditResult.TYPE_SPEC_CODE,
                        "code 携带节点规格语义（通信制式/方位/功率/分辨率/线数/代次），应作为 VehicleNode 限定词", null));
            }
            // 3) legacy code：非标准目录设备族（含历史 DC_ 前缀等自定义 code，且非规格化/近义）
            if (!codeToEntry.containsKey(code) && byAlias == null && !spec) {
                result.addFinding(finding(code, DeviceCategoryLegacyAuditResult.TYPE_LEGACY_CODE,
                        "code 不在标准目录设备族中，建议人工确认 legacy_code → standard_code 映射后收敛", byAlias));
            }
            // 4) 名称重复：标准化后名称与标准目录一致
            String nn = namePolicy.normalize(category.getName());
            String nloc = namePolicy.normalize(category.getNameLocal());
            if (nn != null && normalizedNameToCode.containsKey(nn)
                    && !normalizedNameToCode.get(nn).equals(code)) {
                result.addFinding(finding(code, DeviceCategoryLegacyAuditResult.TYPE_NAME_DUPLICATE,
                        "英文名标准化后与标准目录重复", normalizedNameToCode.get(nn)));
            } else if (nloc != null && normalizedNameLocalToCode.containsKey(nloc)
                    && !normalizedNameLocalToCode.get(nloc).equals(code)) {
                result.addFinding(finding(code, DeviceCategoryLegacyAuditResult.TYPE_NAME_DUPLICATE,
                        "中文名标准化后与标准目录重复", normalizedNameLocalToCode.get(nloc)));
            }
        }
        log.info("Device Category 存量治理审计完成: 现存={}, legacy={}, 名称重复={}, 规格化={}, 近义={}",
                result.getTotalExisting(),
                result.countByType(DeviceCategoryLegacyAuditResult.TYPE_LEGACY_CODE),
                result.countByType(DeviceCategoryLegacyAuditResult.TYPE_NAME_DUPLICATE),
                result.countByType(DeviceCategoryLegacyAuditResult.TYPE_SPEC_CODE),
                result.countByType(DeviceCategoryLegacyAuditResult.TYPE_NEAR_SYNONYM));
        return result;
    }

    private DeviceCategoryLegacyAuditResult.Finding finding(String code, String type, String detail,
                                                            String suggestedStandardCode) {
        return DeviceCategoryLegacyAuditResult.Finding.builder()
                .code(code).type(type).detail(detail).suggestedStandardCode(suggestedStandardCode)
                .build();
    }
}
