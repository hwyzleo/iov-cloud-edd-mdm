package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 供应商 code 系统发号失败异常
 * CR-036 新增：发号器异常或生成 code 撞 UK 重试一次后仍冲突时抛出
 */
@Slf4j
@Getter
public class SupplierCodeGenerationFailedException extends MdmBaseException {

    private final String conflictCode;

    public SupplierCodeGenerationFailedException(String conflictCode) {
        super(MdmErrorCode.SUPPLIER_CODE_GENERATION_FAILED, String.format("供应商 code 系统发号碰撞重试后仍失败: %s", conflictCode));
        this.conflictCode = conflictCode;
        log.warn("供应商 code 系统发号碰撞重试后仍失败: {}", conflictCode);
    }

    public SupplierCodeGenerationFailedException(String conflictCode, Throwable cause) {
        super(MdmErrorCode.SUPPLIER_CODE_GENERATION_FAILED, String.format("供应商 code 系统发号碰撞重试后仍失败: %s", conflictCode));
        this.conflictCode = conflictCode;
        log.warn("供应商 code 系统发号碰撞重试后仍失败: {}", conflictCode, cause);
    }
}
