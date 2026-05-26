package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.CarLine;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.CarLineStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.CarLineType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.LifecycleStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.TargetMarket;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.CarLinePo;
import org.springframework.stereotype.Component;

/**
 * 车系转换器
 *
 * @author hwyz_leo
 */
@Component
public class CarLineConverter {

    /**
     * 持久化对象转换为领域模型
     *
     * @param po 持久化对象
     * @return 领域模型
     */
    public CarLine toDomain(CarLinePo po) {
        if (po == null) {
            return null;
        }
        return CarLine.builder()
                .id(po.getId())
                .code(po.getCode())
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .brandCode(po.getBrandCode())
                .carLineType(po.getCarLineType() != null ? CarLineType.valueOf(po.getCarLineType()) : null)
                .lifecycleStatus(po.getLifecycleStatus() != null ? LifecycleStatus.valueOf(po.getLifecycleStatus()) : null)
                .targetMarket(po.getTargetMarket() != null ? TargetMarket.valueOf(po.getTargetMarket()) : null)
                .sourceSystem(po.getSourceSystem())
                .sourceId(po.getSourceId())
                .sourceVersion(po.getSourceVersion())
                .ingestionChannel(po.getIngestionChannel())
                .ingestionTime(po.getIngestionTime())
                .sourcePayloadHash(po.getSourcePayloadHash())
                .version(po.getVersion())
                .effectiveFrom(po.getEffectiveFrom())
                .effectiveTo(po.getEffectiveTo())
                .status(CarLineStatus.valueOf(po.getStatus()))
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy())
                .modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion())
                .rowValid(po.getRowValid())
                .build();
    }

    /**
     * 领域模型转换为持久化对象
     *
     * @param domain 领域模型
     * @return 持久化对象
     */
    public CarLinePo toPo(CarLine domain) {
        if (domain == null) {
            return null;
        }
        return CarLinePo.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .name(domain.getName())
                .nameLocal(domain.getNameLocal())
                .brandCode(domain.getBrandCode())
                .carLineType(domain.getCarLineType() != null ? domain.getCarLineType().name() : null)
                .lifecycleStatus(domain.getLifecycleStatus() != null ? domain.getLifecycleStatus().name() : null)
                .targetMarket(domain.getTargetMarket() != null ? domain.getTargetMarket().name() : null)
                .sourceSystem(domain.getSourceSystem())
                .sourceId(domain.getSourceId())
                .sourceVersion(domain.getSourceVersion())
                .ingestionChannel(domain.getIngestionChannel())
                .ingestionTime(domain.getIngestionTime())
                .sourcePayloadHash(domain.getSourcePayloadHash())
                .version(domain.getVersion())
                .effectiveFrom(domain.getEffectiveFrom())
                .effectiveTo(domain.getEffectiveTo())
                .status(domain.getStatus().name())
                .createBy(domain.getCreateBy())
                .createTime(domain.getCreateTime())
                .modifyBy(domain.getModifyBy())
                .modifyTime(domain.getModifyTime())
                .rowVersion(domain.getRowVersion())
                .rowValid(domain.getRowValid())
                .build();
    }
}
