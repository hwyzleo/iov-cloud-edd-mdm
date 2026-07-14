package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.TaBaselineSeqPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 型式批准基线序列表 MyBatis Mapper
 *
 * @author hwyz_leo
 */
@Mapper
public interface TaBaselineSeqMapper extends BaseMapper<TaBaselineSeqPo> {

    /**
     * 原子递增序列值
     *
     * @param seqName 序列名称
     * @return 影响行数
     */
    @Update("UPDATE mdm_eead_ta_baseline_seq SET next_val = next_val + 1, modify_time = NOW() WHERE seq_name = #{seqName}")
    int incrementNextSeq(@Param("seqName") String seqName);

    /**
     * 查询序列
     *
     * @param seqName 序列名称
     * @return 序列对象
     */
    @org.apache.ibatis.annotations.Select("SELECT id, seq_name, next_val, create_time, modify_time FROM mdm_eead_ta_baseline_seq WHERE seq_name = #{seqName}")
    TaBaselineSeqPo selectBySeqName(@Param("seqName") String seqName);
}
