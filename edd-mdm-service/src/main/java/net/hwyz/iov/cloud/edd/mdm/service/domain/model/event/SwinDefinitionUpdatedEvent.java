package net.hwyz.iov.cloud.edd.mdm.service.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * SWIN定义更新事件
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinDefinitionUpdatedEvent {
    private String swinCode;
    private String schemeCode;
    private Integer version;
    private String operator;
    private Date eventTime;
}
