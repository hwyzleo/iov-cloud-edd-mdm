package net.hwyz.iov.cloud.edd.mdm.api.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 根据版本和选项码组合查询配置请求
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationByVariantAndOptionCodesRequest {

    private String variantCode;
    private List<String> optionCodes;
}