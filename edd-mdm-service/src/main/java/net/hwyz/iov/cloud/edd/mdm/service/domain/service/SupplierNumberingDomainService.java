package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SupplierCodeExhaustedException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SupplierCodeGenerationFailedException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SupplierCode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SupplierRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SupplierSeqRepository;
import org.springframework.stereotype.Service;

/**
 * Supplier code 发号领域服务
 * CR-036 新增
 * <p>
 * 职责：
 * 1. 全局单一序列行锁取号（mdm_supplier_seq 固定行 SUPPLIER_GLOBAL）
 * 2. 拼接 SUP + 8 位零填充流水
 * 3. 溢出（&gt; 99,999,999）返回 812703
 * 4. UK 兜底重试一次，仍冲突返回 812702
 * 5. 本地 MPT 创建与上游首次创建共用同一发号器
 * <p>
 * 发号与 Supplier 创建在同一本地事务内执行；已提交 code 不回收，DRAFT 物理删除不回退 next_seq。
 * 不使用 Redis、记录总数或客户端传入值作为发号依据。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierNumberingDomainService {

    private final SupplierSeqRepository supplierSeqRepository;
    private final SupplierRepository supplierRepository;

    /**
     * 系统发号生成供应商 code
     *
     * @return 供应商 code 值对象
     */
    public SupplierCode allocate() {
        // 1. 行锁取号
        long seq = supplierSeqRepository.allocateNextSeq();
        log.debug("分配供应商 code 流水序号: {}", seq);

        // 2. 溢出检查（超过 99,999,999 返回 812703）
        if (seq > SupplierCode.getSeqMax()) {
            throw new SupplierCodeExhaustedException(seq);
        }

        // 3. 拼接 code
        SupplierCode supplierCode = SupplierCode.generate(seq);
        log.debug("生成供应商 code: {}", supplierCode.code());

        // 4. UK 兜底重试一次（与存量数据碰撞场景）
        if (supplierRepository.existsByCode(supplierCode.code())) {
            log.warn("供应商 code UK 冲突，重试一次: {}", supplierCode.code());
            long retrySeq = supplierSeqRepository.allocateNextSeq();
            if (retrySeq > SupplierCode.getSeqMax()) {
                throw new SupplierCodeExhaustedException(retrySeq);
            }
            supplierCode = SupplierCode.generate(retrySeq);
            if (supplierRepository.existsByCode(supplierCode.code())) {
                throw new SupplierCodeGenerationFailedException(supplierCode.code());
            }
        }

        return supplierCode;
    }
}
