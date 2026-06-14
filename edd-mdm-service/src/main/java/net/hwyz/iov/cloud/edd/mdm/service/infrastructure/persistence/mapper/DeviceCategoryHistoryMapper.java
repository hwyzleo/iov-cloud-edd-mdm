package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.DeviceCategoryHistoryPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设备类别历史快照 Mapper（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Mapper
public interface DeviceCategoryHistoryMapper extends BaseMapper<DeviceCategoryHistoryPo> {
}
