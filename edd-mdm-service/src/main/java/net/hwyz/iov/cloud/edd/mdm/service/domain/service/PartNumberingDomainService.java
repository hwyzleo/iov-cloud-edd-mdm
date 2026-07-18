package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartCode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartGeneration;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.PartRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.PartSeqRepository;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.PartCodeGenConflictException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.PartSeqOverflowException;

/**
 * Part零件号发号领域服务
 * CR-023 新增，CR-032 移除软件件S前缀
 *
 * 职责：
 * 1. 全局单一序列取号
 * 2. 拼接code/base_no（软硬件统一无前缀骨架）
 * 3. UK兜底重试一次
 * 4. 代次升级next代次生成
 * 5. 上游code冲突两层决策兜底
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PartNumberingDomainService {

    private final PartSeqRepository partSeqRepository;
    private final PartRepository partRepository;

    /**
     * 系统发号生成零件号
     * @param isSoftware 是否软件件
     * @param isAssembly 是否总成件
     * @return 零件号值对象
     */
    public PartCode generatePartCode(boolean isSoftware, boolean isAssembly) {
        // 1. 行锁自增取号
        long seq = partSeqRepository.allocateNextSeq();
        log.debug("分配零件号序号: {}", seq);

        // 2. 检查溢出
        if (seq > PartCode.getSeqMax()) {
            throw new PartSeqOverflowException(seq);
        }

        // 3. 拼接code
        PartCode partCode = PartCode.generate(isSoftware, isAssembly, seq);
        log.debug("生成零件号: code={}, baseNo={}", partCode.code(), partCode.baseNo());

        // 4. UK兜底检查
        if (partRepository.existsByCode(partCode.code())) {
            log.warn("零件号UK冲突，重试一次: {}", partCode.code());
            seq = partSeqRepository.allocateNextSeq();
            if (seq > PartCode.getSeqMax()) {
                throw new PartSeqOverflowException(seq);
            }
            partCode = PartCode.generate(isSoftware, isAssembly, seq);
            if (partRepository.existsByCode(partCode.code())) {
                throw new PartCodeGenConflictException(partCode.code());
            }
        }

        return partCode;
    }

    /**
     * 基于既有code计算下一代次
     * @param currentCode 当前零件号
     * @return 新零件号值对象，如果代次溢出返回null
     */
    public PartCode nextGeneration(String currentCode) {
        PartCode current = PartCode.parse(currentCode);
        PartCode next = current.nextGeneration();
        if (next == null) {
            log.warn("代次溢出: {}", currentCode);
        }
        return next;
    }

    /**
     * 从既有code反解（导入场景）
     * @param code 零件号
     * @return 零件号值对象
     */
    public PartCode parseCode(String code) {
        return PartCode.parse(code);
    }
}
