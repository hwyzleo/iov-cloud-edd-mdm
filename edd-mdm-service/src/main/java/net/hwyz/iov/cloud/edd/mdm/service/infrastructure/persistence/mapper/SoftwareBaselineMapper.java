package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SoftwareBaselinePo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 软件基线 MyBatis Mapper
 *
 * @author hwyz_leo
 */
@Mapper
public interface SoftwareBaselineMapper extends BaseMapper<SoftwareBaselinePo> {
}
