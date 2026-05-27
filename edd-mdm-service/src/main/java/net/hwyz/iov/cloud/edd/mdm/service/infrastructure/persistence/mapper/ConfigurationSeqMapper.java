package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.ConfigurationSeqPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Configuration code 自增序列 Mapper 接口（CR-005）
 *
 * @author hwyz_leo
 */
@Mapper
public interface ConfigurationSeqMapper extends BaseMapper<ConfigurationSeqPo> {

    /**
     * 原子自增 next_seq 并返回受影响行数。
     * 与业务事务在同一本地事务内执行，依赖 InnoDB 行锁保证并发安全。
     *
     * @param variantCode 版本 code
     * @return 受影响行数（0 表示该 variant_code 行不存在，需先 INSERT）
     */
    @Update("UPDATE mdm_configuration_seq " +
            "SET next_seq = next_seq + 1, modify_time = NOW(), modify_by = 'system' " +
            "WHERE variant_code = #{variantCode}")
    int incrementNextSeq(@Param("variantCode") String variantCode);

    /**
     * 查询当前 next_seq 值（已自增后的值）。
     *
     * @param variantCode 版本 code
     * @return 当前 next_seq；若行不存在返回 null
     */
    @Select("SELECT next_seq FROM mdm_configuration_seq WHERE variant_code = #{variantCode}")
    Long selectNextSeq(@Param("variantCode") String variantCode);
}
