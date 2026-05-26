package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.IngestionLogPo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IngestionLogMapper extends BaseMapper<IngestionLogPo> {
}
