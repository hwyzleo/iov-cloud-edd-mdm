package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.RxswinSeqRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.RxswinSeqMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.RxswinSeqPo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * RXSWIN序列仓储实现
 * CR-028 新增
 *
 * 序号分配流程（与PartSeqRepositoryImpl一致）：
 * 1. 尝试原子自增（UPDATE next_seq = next_seq + 1）
 * 2. 若行不存在（受影响行数=0），先 INSERT(next_seq=0)，再重试自增
 * 3. SELECT 查询自增后的值并返回
 *
 * @author hwyz_leo
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class RxswinSeqRepositoryImpl implements RxswinSeqRepository {

    private static final String SEQ_NAME = "RXSWIN";

    private final RxswinSeqMapper rxswinSeqMapper;

    @Override
    public long allocateNextSeq() {
        int affected = rxswinSeqMapper.incrementNextSeq(SEQ_NAME);
        if (affected == 0) {
            try {
                RxswinSeqPo po = RxswinSeqPo.builder()
                        .seqName(SEQ_NAME)
                        .nextSeq(0L)
                        .createBy("system")
                        .createTime(new Date())
                        .modifyBy("system")
                        .modifyTime(new Date())
                        .rowVersion(0)
                        .rowValid(true)
                        .build();
                rxswinSeqMapper.insert(po);
            } catch (DuplicateKeyException ignore) {
                log.debug("mdm_eead_rxswin_seq 已被并发事务初始化");
            }
            affected = rxswinSeqMapper.incrementNextSeq(SEQ_NAME);
            if (affected == 0) {
                throw new IllegalStateException("mdm_eead_rxswin_seq 自增失败（初始化后仍未命中）");
            }
        }
        Long current = rxswinSeqMapper.selectNextSeq(SEQ_NAME);
        if (current == null) {
            throw new IllegalStateException("mdm_eead_rxswin_seq 读取失败");
        }
        return current;
    }
}
