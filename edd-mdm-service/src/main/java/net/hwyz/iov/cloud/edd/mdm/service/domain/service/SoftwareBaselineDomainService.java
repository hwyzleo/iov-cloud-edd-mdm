package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SoftwareBaselineAnchorInvalidException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SoftwareBaselineDuplicateException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SoftwareBaselineItemNotSoftwareException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SoftwareBaselineItemPartInvalidException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Configuration;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Part;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Variant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.AnchorType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.ConfigurationStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.VariantStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ConfigurationRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.PartRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SoftwareBaselineRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.VariantRepository;
import org.springframework.stereotype.Service;

/**
 * 软件基线领域服务（Material 子域）
 * <p>
 * 负责发号、锚点引用完整性校验、基线项引用校验、状态机校验。
 * 设计参考：design §2 DDD 类清单 / §3 F22 流程。
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SoftwareBaselineDomainService {

    private final SoftwareBaselineRepository softwareBaselineRepository;
    private final ConfigurationRepository configurationRepository;
    private final VariantRepository variantRepository;
    private final PartRepository partRepository;

    /**
     * 系统发号：按 {anchorCode} + baselineVersion 拼装 code
     *
     * @param anchorType      锚定层级
     * @param anchorCode      锚点编码
     * @param baselineVersion 基线版本
     * @return 拼装后的 code
     */
    public String generateCode(AnchorType anchorType, String anchorCode, String baselineVersion) {
        return anchorCode + "-" + baselineVersion;
    }

    /**
     * 校验锚点引用完整性（anchorCode 按 anchorType 指向 ACTIVE 的 Configuration/Variant）
     *
     * @param anchorType 锚定层级
     * @param anchorCode 锚点编码
     */
    public void validateAnchor(AnchorType anchorType, String anchorCode) {
        if (anchorType == AnchorType.CONFIGURATION) {
            Configuration config = configurationRepository.findByCode(anchorCode)
                    .orElseThrow(() -> new SoftwareBaselineAnchorInvalidException(anchorType.name(), anchorCode));
            if (config.getStatus() != ConfigurationStatus.ACTIVE) {
                throw new SoftwareBaselineAnchorInvalidException(anchorType.name(), anchorCode);
            }
        } else if (anchorType == AnchorType.VARIANT) {
            Variant variant = variantRepository.findByCode(anchorCode)
                    .orElseThrow(() -> new SoftwareBaselineAnchorInvalidException(anchorType.name(), anchorCode));
            if (variant.getStatus() != VariantStatus.ACTIVE) {
                throw new SoftwareBaselineAnchorInvalidException(anchorType.name(), anchorCode);
            }
        } else {
            throw new SoftwareBaselineAnchorInvalidException(anchorType.name(), anchorCode);
        }
    }

    /**
     * 校验基线唯一性（code 和 anchor+version）
     *
     * @param code            业务主键
     * @param anchorType      锚定层级
     * @param anchorCode      锚点编码
     * @param baselineVersion 基线版本
     */
    public void validateUniqueness(String code, AnchorType anchorType, String anchorCode, String baselineVersion) {
        if (softwareBaselineRepository.existsByCode(code)) {
            throw new SoftwareBaselineDuplicateException(String.format("软件基线 code 已存在: %s", code));
        }
        if (softwareBaselineRepository.existsByAnchorAndVersion(anchorType, anchorCode, baselineVersion)) {
            throw new SoftwareBaselineDuplicateException(
                    String.format("软件基线 (锚点+版本) 已存在: anchorType=%s, anchorCode=%s, baselineVersion=%s",
                            anchorType, anchorCode, baselineVersion));
        }
    }

    /**
     * 校验基线项零件引用（须存在、ACTIVE 且 is_software=true）
     *
     * @param partCode 零件编码
     * @return 校验通过的 Part 聚合（用于冗余字段取值）
     */
    public Part validateBaselineItemPart(String partCode) {
        Part part = partRepository.findByCode(partCode)
                .orElseThrow(() -> new SoftwareBaselineItemPartInvalidException(partCode));
        if (part.getStatus() != PartStatus.ACTIVE) {
            throw new SoftwareBaselineItemPartInvalidException(partCode);
        }
        if (!Boolean.TRUE.equals(part.getIsSoftware())) {
            throw new SoftwareBaselineItemNotSoftwareException(partCode);
        }
        return part;
    }
}
