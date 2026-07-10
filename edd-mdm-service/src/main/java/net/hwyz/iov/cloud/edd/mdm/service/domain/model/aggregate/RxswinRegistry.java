package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * RXSWIN登记聚合根（EEAD 子域）
 * <p>
 * 以 manifestCode 为幂等键的不可变登记记录，维护 manifestCode ↔ SWIN ↔ RXSWIN 松引用关系。
 * 登记后不可修改或删除，采用 append-only 模式。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RxswinRegistry {

    private Long id;
    private String manifestCode;
    private String manifestDigest;
    private String swinCode;
    private String rxswinValue;
    private String softwareBaselineCode;
    private String status;
    private Date approvedAt;
    private Date registeredAt;
    private String requestSource;
    private String traceId;
    private Integer version;
    private String createBy;
    private Date createTime;
    private Integer rowVersion;
    private Boolean rowValid;

    /**
     * 创建新的RXSWIN登记记录
     *
     * @param manifestCode          OTA manifest业务键
     * @param manifestDigest        manifest摘要
     * @param swinCode              SWIN代码
     * @param rxswinValue           MDM生成的RXSWIN值
     * @param softwareBaselineCode  软件基线代码（可选）
     * @param approvedAt            批准时间（可选）
     * @param requestSource         调用方标识
     * @param traceId               链路追踪ID
     * @return 新创建的RxswinRegistry实例
     */
    public static RxswinRegistry create(String manifestCode, String manifestDigest, String swinCode,
                                         String rxswinValue, String softwareBaselineCode, Date approvedAt,
                                         String requestSource, String traceId) {
        if (manifestCode == null || manifestCode.isBlank()) {
            throw new IllegalArgumentException("manifestCode不能为空");
        }
        if (manifestDigest == null || manifestDigest.isBlank()) {
            throw new IllegalArgumentException("manifestDigest不能为空");
        }
        if (swinCode == null || swinCode.isBlank()) {
            throw new IllegalArgumentException("swinCode不能为空");
        }
        if (rxswinValue == null || rxswinValue.isBlank()) {
            throw new IllegalArgumentException("rxswinValue不能为空");
        }
        if (requestSource == null || requestSource.isBlank()) {
            throw new IllegalArgumentException("requestSource不能为空");
        }
        Date now = new Date();
        return RxswinRegistry.builder()
                .manifestCode(manifestCode)
                .manifestDigest(manifestDigest)
                .swinCode(swinCode)
                .rxswinValue(rxswinValue)
                .softwareBaselineCode(softwareBaselineCode)
                .status("REGISTERED")
                .approvedAt(approvedAt)
                .registeredAt(now)
                .requestSource(requestSource)
                .traceId(traceId)
                .version(1)
                .createBy(requestSource)
                .createTime(now)
                .rowVersion(0)
                .rowValid(true)
                .build();
    }
}
