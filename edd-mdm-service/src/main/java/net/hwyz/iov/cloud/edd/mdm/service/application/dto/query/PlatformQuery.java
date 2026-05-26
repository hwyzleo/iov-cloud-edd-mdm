package net.hwyz.iov.cloud.edd.mdm.service.application.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 平台查询条件
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformQuery {

    /**
     * 页码
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;

    /**
     * 是否包含失效记录
     */
    private boolean includeInactive;
}
