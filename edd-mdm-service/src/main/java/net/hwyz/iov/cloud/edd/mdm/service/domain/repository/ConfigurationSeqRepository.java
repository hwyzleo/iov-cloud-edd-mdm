package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

/**
 * Configuration code 自增序列仓储接口（CR-005）
 * <p>
 * 提供按 variant_code 维度的原子序号分配能力。
 * 实现需与业务事务在同一本地事务内执行（InnoDB 行锁），保证并发安全且回滚一致。
 *
 * @author hwyz_leo
 */
public interface ConfigurationSeqRepository {

    /**
     * 为指定 variant 分配下一个序号。
     * <p>
     * 实现要求：
     * <ul>
     *   <li>若 variant_code 行不存在则先初始化（next_seq=0）再自增</li>
     *   <li>原子自增 next_seq（依赖行锁）并返回更新后的值</li>
     *   <li>不回退序号（即便事务外的 Configuration 物理删除）</li>
     * </ul>
     *
     * @param variantCode 版本 code
     * @return 分配到的新序号（已自增）；若超过 9,999,999 由调用方按 807014 处理
     */
    long allocateNextSeq(String variantCode);
}
