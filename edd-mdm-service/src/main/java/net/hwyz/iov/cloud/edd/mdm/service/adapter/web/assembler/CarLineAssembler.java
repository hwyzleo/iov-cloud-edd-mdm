package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.CarLineHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.CarLineResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.CarLineDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.CarLineHistoryDto;
import org.springframework.stereotype.Component;

/**
 * 车系Assembler
 *
 * @author hwyz_leo
 */
@Component
public class CarLineAssembler {

    /**
     * DTO转换为响应对象
     *
     * @param dto 车系DTO
     * @return 车系响应对象
     */
    public CarLineResponse toResponse(CarLineDto dto) {
        if (dto == null) {
            return null;
        }
        return CarLineResponse.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .brandCode(dto.getBrandCode())
                .carLineType(dto.getCarLineType())
                .lifecycleStatus(dto.getLifecycleStatus())
                .targetMarket(dto.getTargetMarket())
                .sourceSystem(dto.getSourceSystem())
                .sourceId(dto.getSourceId())
                .sourceVersion(dto.getSourceVersion())
                .ingestionChannel(dto.getIngestionChannel())
                .ingestionTime(dto.getIngestionTime())
                .sourcePayloadHash(dto.getSourcePayloadHash())
                .version(dto.getVersion())
                .effectiveFrom(dto.getEffectiveFrom())
                .effectiveTo(dto.getEffectiveTo())
                .status(dto.getStatus())
                .createBy(dto.getCreateBy())
                .createTime(dto.getCreateTime())
                .modifyBy(dto.getModifyBy())
                .modifyTime(dto.getModifyTime())
                .build();
    }

    /**
     * 历史版本DTO转换为响应对象
     *
     * @param dto 车系历史版本DTO
     * @return 车系历史版本响应对象
     */
    public CarLineHistoryResponse toHistoryResponse(CarLineHistoryDto dto) {
        if (dto == null) {
            return null;
        }
        return CarLineHistoryResponse.builder()
                .snapshotId(dto.getSnapshotId())
                .entityId(dto.getEntityId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .brandCode(dto.getBrandCode())
                .carLineType(dto.getCarLineType())
                .lifecycleStatus(dto.getLifecycleStatus())
                .targetMarket(dto.getTargetMarket())
                .sourceSystem(dto.getSourceSystem())
                .sourceId(dto.getSourceId())
                .sourceVersion(dto.getSourceVersion())
                .ingestionChannel(dto.getIngestionChannel())
                .ingestionTime(dto.getIngestionTime())
                .sourcePayloadHash(dto.getSourcePayloadHash())
                .version(dto.getVersion())
                .effectiveFrom(dto.getEffectiveFrom())
                .effectiveTo(dto.getEffectiveTo())
                .status(dto.getStatus())
                .operationType(dto.getOperationType())
                .snapshotTime(dto.getSnapshotTime())
                .operator(dto.getOperator())
                .createBy(dto.getCreateBy())
                .createTime(dto.getCreateTime())
                .modifyBy(dto.getModifyBy())
                .modifyTime(dto.getModifyTime())
                .build();
    }
}
