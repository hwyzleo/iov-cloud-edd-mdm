package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import net.hwyz.iov.cloud.framework.common.exception.BusinessException;
import net.hwyz.iov.cloud.framework.common.exception.ErrorCode;

/**
 * 主数据管理服务基础异常
 *
 * @author hwyz_leo
 */
public class MdmBaseException extends BusinessException {

    public MdmBaseException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MdmBaseException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

}
