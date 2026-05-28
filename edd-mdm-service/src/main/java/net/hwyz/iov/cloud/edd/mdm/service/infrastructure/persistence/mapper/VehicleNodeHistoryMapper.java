package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.VehicleNodeHistoryPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车载节点历史快照Mapper接口
 *
 * @author hwyz_leo
 */
@Mapper
public interface VehicleNodeHistoryMapper extends BaseMapper<VehicleNodeHistoryPo> {
}
