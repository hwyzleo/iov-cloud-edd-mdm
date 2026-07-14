package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.TaBaselineHistoryPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 型式批准基线历史快照 MyBatis Mapper
 *
 * @author hwyz_leo
 */
@Mapper
public interface TaBaselineHistoryMapper extends BaseMapper<TaBaselineHistoryPo> {
}
