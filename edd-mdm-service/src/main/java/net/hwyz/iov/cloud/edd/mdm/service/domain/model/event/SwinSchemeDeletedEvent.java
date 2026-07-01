package net.hwyz.iov.cloud.edd.mdm.service.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * SWIN编码方案删除事件
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinSchemeDeletedEvent {
    private String code;
    private String operator;
    private Date eventTime;
}
