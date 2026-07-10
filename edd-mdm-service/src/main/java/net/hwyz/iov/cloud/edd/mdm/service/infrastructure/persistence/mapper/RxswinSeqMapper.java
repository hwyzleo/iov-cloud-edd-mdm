package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.RxswinSeqPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * RXSWIN序列表Mapper接口
 * CR-028 新增
 *
 * @author hwyz_leo
 */
@Mapper
public interface RxswinSeqMapper extends BaseMapper<RxswinSeqPo> {

    /**
     * 原子自增next_seq
     *
     * @param seqName 序列名称
     * @return 影响行数
     */
    @Update("UPDATE mdm_eead_rxswin_seq SET next_seq = next_seq + 1, modify_time = NOW() WHERE seq_name = #{seqName}")
    int incrementNextSeq(@Param("seqName") String seqName);

    /**
     * 查询当前next_seq值
     *
     * @param seqName 序列名称
     * @return 当前序号
     */
    @Select("SELECT next_seq FROM mdm_eead_rxswin_seq WHERE seq_name = #{seqName}")
    Long selectNextSeq(@Param("seqName") String seqName);
}
