package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 供应商 code 全局流水序号耗尽异常
 * CR-036 新增：next_seq 超过 99,999,999 时抛出
 */
@Slf4j
@Getter
public class SupplierCodeExhaustedException extends MdmBaseException {

    private final long currentSeq;

    public SupplierCodeExhaustedException(long currentSeq) {
        super(MdmErrorCode.SUPPLIER_CODE_EXHAUSTED, String.format("供应商 code 全局流水序号耗尽: %d", currentSeq));
        this.currentSeq = currentSeq;
        log.warn("供应商 code 全局流水序号耗尽: {}", currentSeq);
    }

    public SupplierCodeExhaustedException(long currentSeq, Throwable cause) {
        super(MdmErrorCode.SUPPLIER_CODE_EXHAUSTED, String.format("供应商 code 全局流水序号耗尽: %d", currentSeq));
        this.currentSeq = currentSeq;
        log.warn("供应商 code 全局流水序号耗尽: {}", currentSeq, cause);
    }
}
