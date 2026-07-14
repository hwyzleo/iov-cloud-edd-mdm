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
 * 型式批准基线序列表持久化对象
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mdm_eead_ta_baseline_seq")
public class TaBaselineSeqPo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String seqName;
    private Long nextVal;
    private Date createTime;
    private Date modifyTime;
}
