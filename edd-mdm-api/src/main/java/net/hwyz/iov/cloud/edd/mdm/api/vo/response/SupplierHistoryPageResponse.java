package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 供应商历史版本分页响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierHistoryPageResponse {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 历史版本列表
     */
    private List<SupplierHistoryResponse> rows;
}
