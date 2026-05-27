package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.ModelPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车型Mapper接口
 *
 * @author hwyz_leo
 */
@Mapper
public interface ModelMapper extends BaseMapper<ModelPo> {
}
