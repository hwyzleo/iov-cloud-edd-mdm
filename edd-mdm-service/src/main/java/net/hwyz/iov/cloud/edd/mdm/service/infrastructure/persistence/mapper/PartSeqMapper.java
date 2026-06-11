package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PartSeqPo;

/**
 * Part零件号序列表Mapper
 * CR-023 新增
 */
@Mapper
public interface PartSeqMapper extends BaseMapper<PartSeqPo> {

    /**
     * 原子自增next_seq
     * @param seqName 序列名称
     * @return 影响行数
     */
    @Update("UPDATE mdm_material_part_seq SET next_seq = next_seq + 1, modify_time = NOW() WHERE seq_name = #{seqName}")
    int incrementNextSeq(@Param("seqName") String seqName);

    /**
     * 查询当前next_seq值
     * @param seqName 序列名称
     * @return 当前序号
     */
    @Select("SELECT next_seq FROM mdm_material_part_seq WHERE seq_name = #{seqName}")
    Long selectNextSeq(@Param("seqName") String seqName);
}
