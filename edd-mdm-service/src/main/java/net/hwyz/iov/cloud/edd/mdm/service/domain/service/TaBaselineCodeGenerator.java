package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.TaBaselineSeqOverflowException;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.TaBaselineSeqMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.TaBaselineSeqPo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * TA基线编码生成器
 * <p>
 * 复用 PartSeqRepository 的 InnoDB 行锁原子取号模式，同事务 UPDATE next_seq -> SELECT。
 * 首版编码 TAB + 16 位零填充序号。
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaBaselineCodeGenerator {

    private static final String SEQ_NAME = "TA_BASELINE";
    private static final String CODE_PREFIX = "TAB";
    private static final int CODE_PADDING = 16;

    private final TaBaselineSeqMapper taBaselineSeqMapper;

    /**
     * 生成下一个TA基线编码
     *
     * @return 新的TA基线编码
     */
    @Transactional(rollbackFor = Exception.class)
    public String nextValue() {
        // 使用 InnoDB 行锁原子取号
        int rows = taBaselineSeqMapper.incrementNextSeq(SEQ_NAME);
        if (rows == 0) {
            throw new TaBaselineSeqOverflowException(SEQ_NAME);
        }

        TaBaselineSeqPo seqPo = taBaselineSeqMapper.selectBySeqName(SEQ_NAME);
        if (seqPo == null) {
            throw new TaBaselineSeqOverflowException(SEQ_NAME);
        }

        long nextVal = seqPo.getNextVal();
        String seqStr = String.format("%0" + CODE_PADDING + "d", nextVal);

        if (seqStr.length() > CODE_PADDING) {
            throw new TaBaselineSeqOverflowException(SEQ_NAME);
        }

        String code = CODE_PREFIX + seqStr;
        log.debug("生成TA基线编码: {}", code);
        return code;
    }
}
