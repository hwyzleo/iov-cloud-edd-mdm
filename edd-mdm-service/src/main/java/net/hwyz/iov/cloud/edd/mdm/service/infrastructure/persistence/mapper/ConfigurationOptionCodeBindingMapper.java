package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.ConfigurationOptionCodeBindingPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 配置选项码绑定Mapper接口
 *
 * @author hwyz_leo
 */
@Mapper
public interface ConfigurationOptionCodeBindingMapper extends BaseMapper<ConfigurationOptionCodeBindingPo> {

    @Select("<script>" +
            "SELECT configuration_code FROM mdm_configuration_option_code_binding " +
            "WHERE option_code_code IN " +
            "<foreach collection='codes' item='code' open='(' separator=',' close=')'>" +
            "#{code}" +
            "</foreach>" +
            " AND row_valid=1 " +
            "GROUP BY configuration_code " +
            "HAVING COUNT(DISTINCT option_code_code) &gt;= #{size}" +
            "</script>")
    List<String> findConfigurationCodesByOptionCodes(@Param("codes") List<String> codes, @Param("size") int size);

    @Select("<script>" +
            "SELECT binding.configuration_code FROM mdm_configuration_option_code_binding binding " +
            "INNER JOIN mdm_configuration config ON binding.configuration_code = config.code " +
            "WHERE binding.option_code_code IN " +
            "<foreach collection='codes' item='code' open='(' separator=',' close=')'>" +
            "#{code}" +
            "</foreach>" +
            " AND binding.row_valid=1 " +
            " AND config.variant_code = #{variantCode} " +
            " AND config.status = 'ACTIVE' " +
            "GROUP BY binding.configuration_code " +
            "HAVING COUNT(DISTINCT binding.option_code_code) &gt;= #{size}" +
            "</script>")
    List<String> findConfigurationCodeByVariantAndOptionCodes(@Param("variantCode") String variantCode, @Param("codes") List<String> codes, @Param("size") int size);
}
