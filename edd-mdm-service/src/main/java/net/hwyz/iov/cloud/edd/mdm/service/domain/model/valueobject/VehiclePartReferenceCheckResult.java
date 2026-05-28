package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * VMD VehiclePart 反查结果值对象（EEAD 子域）
 * <p>
 * 领域层视角描述"是否存在下游引用"+ 引用规模 + 样本，避免领域层依赖具体的 Feign 响应结构。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePartReferenceCheckResult {

    /**
     * 引用方服务名（首期范围：edd-vmd）
     */
    private String referencingService;

    /**
     * 引用数量；-1 表示反查失败（fail-safe 路径）
     */
    private long referenceCount;

    /**
     * 引用样本（至多前 10 条引用方业务标识，可空）
     */
    private List<String> samples;

    /**
     * 反查是否可用（false 表示 Feign 调用失败 / 降级 / 不可用）
     */
    private boolean available;

    /**
     * 是否存在引用（仅当 available=true 时有意义）。
     */
    public boolean hasReference() {
        return available && referenceCount > 0;
    }

    /**
     * 构造一个"反查不可用"的结果，用于 fail-safe 路径。
     */
    public static VehiclePartReferenceCheckResult unavailable(String referencingService) {
        return VehiclePartReferenceCheckResult.builder()
                .referencingService(referencingService)
                .referenceCount(-1L)
                .samples(Collections.emptyList())
                .available(false)
                .build();
    }
}
