package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.ConfigurationPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 配置Mapper接口
 *
 * @author hwyz_leo
 */
@Mapper
public interface ConfigurationMapper extends BaseMapper<ConfigurationPo> {
}
