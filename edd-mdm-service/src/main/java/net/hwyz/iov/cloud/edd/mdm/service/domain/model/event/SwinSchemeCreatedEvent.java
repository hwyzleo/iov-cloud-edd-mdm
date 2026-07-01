package net.hwyz.iov.cloud.edd.mdm.service.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * SWIN编码方案创建事件
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinSchemeCreatedEvent {
    private String code;
    private String name;
    private String route;
    private String status;
    private String operator;
    private Date eventTime;
}
