package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OutboxPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 事件发件箱Mapper接口
 *
 * @author hwyz_leo
 */
@Mapper
public interface OutboxMapper extends BaseMapper<OutboxPo> {
}
