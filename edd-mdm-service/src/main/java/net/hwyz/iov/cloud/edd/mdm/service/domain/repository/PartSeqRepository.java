package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

/**
 * 零件号序列仓储接口
 * CR-023 新增
 */
public interface PartSeqRepository {

    /**
     * 分配下一个序号（全局单一计数器）
     * @return 新序号
     */
    long allocateNextSeq();
}
