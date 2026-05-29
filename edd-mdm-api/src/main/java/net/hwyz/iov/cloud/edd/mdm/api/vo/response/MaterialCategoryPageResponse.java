package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 物料分类分页响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCategoryPageResponse {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页数据
     */
    private List<MaterialCategoryResponse> rows;

    /**
     * 创建空的分页响应
     *
     * @return 空分页响应
     */
    public static MaterialCategoryPageResponse empty() {
        return MaterialCategoryPageResponse.builder()
                .total(0L)
                .rows(Collections.emptyList())
                .build();
    }
}
