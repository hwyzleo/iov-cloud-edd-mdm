package net.hwyz.iov.cloud.edd.mdm.service.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * SWIN编码方案更新事件
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinSchemeUpdatedEvent {
    private String code;
    private String name;
    private String route;
    private Integer version;
    private String operator;
    private Date eventTime;
}
