package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SupplierPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 供应商Mapper接口
 *
 * @author hwyz_leo
 */
@Mapper
public interface SupplierMapper extends BaseMapper<SupplierPo> {
}
