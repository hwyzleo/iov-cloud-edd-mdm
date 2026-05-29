package net.hwyz.iov.cloud.edd.mdm.service.application.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 物料分类查询条件
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCategoryQuery {

    /**
     * 页码
     */
    private int page;

    /**
     * 每页大小
     */
    private int size;

    /**
     * 父分类编码
     */
    private String parentCode;

    /**
     * 是否包含失效记录
     */
    private boolean includeInactive;
}
