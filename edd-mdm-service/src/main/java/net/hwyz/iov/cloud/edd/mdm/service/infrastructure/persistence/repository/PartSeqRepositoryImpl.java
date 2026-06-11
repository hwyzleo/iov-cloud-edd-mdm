package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.PartSeqRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.PartSeqMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PartSeqPo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Part零件号序列仓储实现
 * CR-023 新增
 *
 * 序号分配流程：
 * 1. 尝试原子自增（UPDATE next_seq = next_seq + 1）
 * 2. 若行不存在（受影响行数=0），先 INSERT(next_seq=0)，再重试自增
 * 3. SELECT 查询自增后的值并返回
 *
 * 与业务事务在同一本地事务内执行，依赖 InnoDB 行锁保证并发安全；
 * 事务回滚时序号一并回滚，不会出现跳号或重复。
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PartSeqRepositoryImpl implements PartSeqRepository {

    private static final String SEQ_NAME = "PART_GLOBAL";

    private final PartSeqMapper partSeqMapper;

    @Override
    public long allocateNextSeq() {
        // 1. 尝试原子自增
        int affected = partSeqMapper.incrementNextSeq(SEQ_NAME);
        if (affected == 0) {
            // 2. 行不存在，先初始化
            try {
                PartSeqPo po = new PartSeqPo();
                po.setSeqName(SEQ_NAME);
                po.setNextSeq(0L);
                po.setCreateBy("system");
                po.setCreateTime(new Date());
                po.setModifyBy("system");
                po.setModifyTime(new Date());
                po.setRowVersion(0);
                po.setRowValid(true);
                partSeqMapper.insert(po);
            } catch (DuplicateKeyException ignore) {
                // 并发场景下其他事务已 INSERT，忽略并继续走自增
                log.debug("mdm_material_part_seq 已被并发事务初始化");
            }
            // 重试自增
            affected = partSeqMapper.incrementNextSeq(SEQ_NAME);
            if (affected == 0) {
                throw new IllegalStateException("mdm_material_part_seq 自增失败（初始化后仍未命中）");
            }
        }
        // 3. 读取当前值并返回
        Long current = partSeqMapper.selectNextSeq(SEQ_NAME);
        if (current == null) {
            throw new IllegalStateException("mdm_material_part_seq 读取失败");
        }
        return current;
    }
}
