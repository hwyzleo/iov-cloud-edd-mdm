package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PlatformHistoryPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 平台历史快照Mapper接口
 *
 * @author hwyz_leo
 */
@Mapper
public interface PlatformHistoryMapper extends BaseMapper<PlatformHistoryPo> {
}
