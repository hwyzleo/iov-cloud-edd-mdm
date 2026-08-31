package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

/**
 * 供应商全局流水序列表仓储接口
 * CR-036 新增
 */
public interface SupplierSeqRepository {

    /**
     * 分配下一个流水号（全局单一计数器，行锁）
     * <p>
     * 在同一本地事务内 SELECT ... FOR UPDATE 读取当前 next_seq 后自增 +1 并返回本次分配值；
     * 行锁保证单实例及多副本部署下不重复分配。
     *
     * @return 本次分配到的流水号（自增前的 next_seq）
     */
    long allocateNextSeq();
}
