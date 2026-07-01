package net.hwyz.iov.cloud.edd.mdm.service.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * SWIN定义创建事件
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinDefinitionCreatedEvent {
    private String swinCode;
    private String schemeCode;
    private String typeRefType;
    private String typeRefCode;
    private String name;
    private String status;
    private String operator;
    private Date eventTime;
}
