package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Configuration code 自增序列持久化对象（CR-005）
 * <p>
 * 按 variant_code 维度独立计数，next_seq 单调递增、不回收。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mdm_configuration_seq")
public class ConfigurationSeqPo {

    /**
     * 主键：版本code（逻辑引用 mdm_variant.code）
     */
    @TableId(type = IdType.INPUT)
    private String variantCode;

    /**
     * 下一个待分配序号（默认 0）；分配时 UPDATE SET next_seq=next_seq+1，使用更新后的值拼 7 位零填充 code 后缀
     */
    private Long nextSeq;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifyBy;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 乐观锁版本号
     */
    private Integer rowVersion;

    /**
     * 行有效标记
     */
    private Boolean rowValid;
}
