package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

@Getter
public class PartSubstituteInvalidException extends RuntimeException {
    public static final int ERROR_CODE = 814014;
    private final String substitutePartCode;

    public PartSubstituteInvalidException(String substitutePartCode) {
        super(String.format("替代零件无效: %s", substitutePartCode));
        this.substitutePartCode = substitutePartCode;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
