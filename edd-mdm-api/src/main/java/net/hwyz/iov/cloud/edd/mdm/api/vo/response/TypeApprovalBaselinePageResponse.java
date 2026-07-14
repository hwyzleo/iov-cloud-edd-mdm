package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 型式批准基线分页响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypeApprovalBaselinePageResponse {

    private List<TypeApprovalBaselineResponse> list;
    private Long total;

    public static TypeApprovalBaselinePageResponse empty() {
        return TypeApprovalBaselinePageResponse.builder()
                .list(List.of())
                .total(0L)
                .build();
    }
}
