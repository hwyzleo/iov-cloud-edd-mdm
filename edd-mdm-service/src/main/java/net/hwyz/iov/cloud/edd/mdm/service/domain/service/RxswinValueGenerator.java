package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.RxswinSeqOverflowException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.RxswinSeqRepository;
import org.springframework.stereotype.Service;

/**
 * RXSWIN值生成器（领域端口）
 * <p>
 * 首版采用MDM中央发号，使用专用序列表 mdm_eead_rxswin_seq 的全局单一计数器。
 * 编号格式：RX + 16位十进制零填充序号，例如 RX0000000000000001。
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RxswinValueGenerator {

    private static final long SEQ_MAX = 9999999999999999L;
    private static final String PREFIX = "RX";

    private final RxswinSeqRepository rxswinSeqRepository;

    /**
     * 生成下一个RXSWIN值
     *
     * @return RXSWIN值
     * @throws RxswinSeqOverflowException 如果序号溢出
     */
    public String nextValue() {
        long seq = rxswinSeqRepository.allocateNextSeq();
        if (seq > SEQ_MAX) {
            throw new RxswinSeqOverflowException(seq);
        }
        String rxswinValue = PREFIX + String.format("%016d", seq);
        log.info("生成RXSWIN值: {}", rxswinValue);
        return rxswinValue;
    }
}
