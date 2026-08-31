package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Supplier 全局流水序列表持久化对象
 * CR-036 新增
 */
@Data
@TableName("mdm_supplier_seq")
public class SupplierSeqPo {

    /**
     * 序列名称，固定SUPPLIER_GLOBAL
     */
    private String seqName;

    /**
     * 下一可分配流水号
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
