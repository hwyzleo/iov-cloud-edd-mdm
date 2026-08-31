package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SupplierSeqRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.SupplierSeqMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SupplierSeqPo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Supplier 全局流水序列表仓储实现
 * CR-036 新增
 * <p>
 * 分配流程（与业务事务同一本地事务，依赖 InnoDB 行锁）：
 * 1. SELECT next_seq ... FOR UPDATE 锁定单行
 * 2. 返回锁定时的 next_seq 作为本次分配流水号
 * 3. UPDATE next_seq = next_seq + 1
 * 4. 事务提交后 code 对外可见；回滚时序号一并回滚
 * <p>
 * 全局仅一行 seq_name=SUPPLIER_GLOBAL（由 Flyway 初始化），不按国家 / 类型 / 来源分段。
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SupplierSeqRepositoryImpl implements SupplierSeqRepository {

    private static final String SEQ_NAME = "SUPPLIER_GLOBAL";

    private final SupplierSeqMapper supplierSeqMapper;

    @Override
    public long allocateNextSeq() {
        // 1. 行锁读取当前值
        Long current = supplierSeqMapper.selectNextSeqForUpdate(SEQ_NAME);
        if (current == null) {
            // 2. 异常兜底：Flyway 应已初始化；若行缺失则初始化 next_seq=1
            initSeqRow(1L);
            current = 1L;
        }
        // 3. 自增 +1
        int affected = supplierSeqMapper.incrementNextSeq(SEQ_NAME);
        if (affected == 0) {
            throw new IllegalStateException("mdm_supplier_seq 自增失败: seq_name=" + SEQ_NAME);
        }
        // 4. 返回本次分配值（自增前的 next_seq）
        return current;
    }

    private void initSeqRow(long nextSeq) {
        try {
            SupplierSeqPo po = new SupplierSeqPo();
            po.setSeqName(SEQ_NAME);
            po.setNextSeq(nextSeq);
            po.setCreateBy("system");
            po.setCreateTime(new Date());
            po.setModifyBy("system");
            po.setModifyTime(new Date());
            po.setRowVersion(0);
            po.setRowValid(true);
            supplierSeqMapper.insert(po);
        } catch (DuplicateKeyException ignore) {
            // 并发场景下其他事务已 INSERT，忽略并继续走行锁读取
            log.debug("mdm_supplier_seq 已被并发事务初始化");
        }
    }
}
