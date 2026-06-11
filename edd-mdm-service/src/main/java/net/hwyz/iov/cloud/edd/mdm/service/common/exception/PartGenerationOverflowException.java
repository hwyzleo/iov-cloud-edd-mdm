package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 零件代次溢出异常
 * CR-023 新增
 */
@Slf4j
@Getter
public class PartGenerationOverflowException extends MdmBaseException {

    private final String partCode;

    public PartGenerationOverflowException(String partCode) {
        super(MdmErrorCode.PART_GENERATION_OVERFLOW, String.format("代次溢出（超过ZZ）: %s", partCode));
        this.partCode = partCode;
        log.warn("代次溢出（超过ZZ）: {}", partCode);
    }

    public PartGenerationOverflowException(String partCode, Throwable cause) {
        super(MdmErrorCode.PART_GENERATION_OVERFLOW, String.format("代次溢出（超过ZZ）: %s", partCode));
        this.partCode = partCode;
        log.warn("代次溢出（超过ZZ）: {}", partCode, cause);
    }
}
