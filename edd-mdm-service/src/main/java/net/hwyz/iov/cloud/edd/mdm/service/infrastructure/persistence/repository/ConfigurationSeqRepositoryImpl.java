package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ConfigurationSeqRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.ConfigurationSeqMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.ConfigurationSeqPo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Configuration code 自增序列仓储实现（CR-005）
 * <p>
 * 序号分配流程：
 * <ol>
 *   <li>尝试原子自增（UPDATE next_seq = next_seq + 1）</li>
 *   <li>若行不存在（受影响行数=0），先 INSERT(next_seq=0)，再重试自增</li>
 *   <li>SELECT 查询自增后的值并返回</li>
 * </ol>
 * 与业务事务在同一本地事务内执行，依赖 InnoDB 行锁保证并发安全；
 * 事务回滚时序号一并回滚，不会出现跳号或重复。
 *
 * @author hwyz_leo
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ConfigurationSeqRepositoryImpl implements ConfigurationSeqRepository {

    private final ConfigurationSeqMapper configurationSeqMapper;

    @Override
    public long allocateNextSeq(String variantCode) {
        // 1. 尝试原子自增
        int affected = configurationSeqMapper.incrementNextSeq(variantCode);
        if (affected == 0) {
            // 2. 行不存在，先初始化
            try {
                ConfigurationSeqPo po = ConfigurationSeqPo.builder()
                        .variantCode(variantCode)
                        .nextSeq(0L)
                        .createBy("system")
                        .createTime(new Date())
                        .modifyBy("system")
                        .modifyTime(new Date())
                        .rowVersion(0)
                        .rowValid(true)
                        .build();
                configurationSeqMapper.insert(po);
            } catch (DuplicateKeyException ignore) {
                // 并发场景下其他事务已 INSERT，忽略并继续走自增
                log.debug("mdm_configuration_seq 已被并发事务初始化: variantCode={}", variantCode);
            }
            // 重试自增
            affected = configurationSeqMapper.incrementNextSeq(variantCode);
            if (affected == 0) {
                throw new IllegalStateException(
                        "mdm_configuration_seq 自增失败（初始化后仍未命中）: variantCode=" + variantCode);
            }
        }
        // 3. 读取当前值并返回
        Long current = configurationSeqMapper.selectNextSeq(variantCode);
        if (current == null) {
            throw new IllegalStateException("mdm_configuration_seq 读取失败: variantCode=" + variantCode);
        }
        return current;
    }
}
