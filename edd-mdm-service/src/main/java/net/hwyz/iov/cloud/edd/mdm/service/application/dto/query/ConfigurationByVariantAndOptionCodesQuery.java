package net.hwyz.iov.cloud.edd.mdm.service.application.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 根据版本和选项码组合查询配置
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationByVariantAndOptionCodesQuery {

    private String variantCode;
    private List<String> optionCodes;
}