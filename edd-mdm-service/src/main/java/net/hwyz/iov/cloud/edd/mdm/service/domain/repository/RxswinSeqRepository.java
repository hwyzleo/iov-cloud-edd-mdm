package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

/**
 * RXSWIN序列仓储接口
 * <p>
 * 基于InnoDB行锁的原子取号，与PartSeqRepository模式一致。
 *
 * @author hwyz_leo
 */
public interface RxswinSeqRepository {

    /**
     * 分配下一个序号
     * <p>
     * 原子递增 next_seq 并返回递增后的值。
     * 必须在当前业务事务内执行，依赖InnoDB行锁保证并发安全。
     *
     * @return 分配的序号
     */
    long allocateNextSeq();
}
