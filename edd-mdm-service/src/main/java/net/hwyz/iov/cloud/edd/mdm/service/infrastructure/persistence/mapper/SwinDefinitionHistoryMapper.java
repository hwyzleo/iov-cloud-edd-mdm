package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SwinDefinitionHistoryPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * SWIN编码定义历史快照Mapper接口
 *
 * @author hwyz_leo
 */
@Mapper
public interface SwinDefinitionHistoryMapper extends BaseMapper<SwinDefinitionHistoryPo> {
}
