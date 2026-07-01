package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SwinSchemeHistoryPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * SWIN编码方案历史快照Mapper接口
 *
 * @author hwyz_leo
 */
@Mapper
public interface SwinSchemeHistoryMapper extends BaseMapper<SwinSchemeHistoryPo> {
}
