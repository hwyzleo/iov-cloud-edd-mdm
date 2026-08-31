package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SupplierSeqPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Supplier 全局流水序列表Mapper
 * CR-036 新增
 */
@Mapper
public interface SupplierSeqMapper extends BaseMapper<SupplierSeqPo> {

    /**
     * 行锁读取当前 next_seq（不改变值）
     *
     * @param seqName 序列名称
     * @return 当前 next_seq；行不存在返回 null
     */
    @Select("SELECT next_seq FROM mdm_supplier_seq WHERE seq_name = #{seqName} FOR UPDATE")
    Long selectNextSeqForUpdate(@Param("seqName") String seqName);

    /**
     * next_seq 自增 +1
     *
     * @param seqName 序列名称
     * @return 影响行数
     */
    @Update("UPDATE mdm_supplier_seq SET next_seq = next_seq + 1, modify_time = NOW(), modify_by = 'system' WHERE seq_name = #{seqName}")
    int incrementNextSeq(@Param("seqName") String seqName);
}
