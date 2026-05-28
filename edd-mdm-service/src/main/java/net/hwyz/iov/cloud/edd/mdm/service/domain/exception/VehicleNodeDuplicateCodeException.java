package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

/**
 * 车载节点 nodeCode 重复异常（EEAD 子域）
 * <p>
 * 错误码：812002 VEHICLE_NODE_CODE_EXIST
 * 错误响应中 SHALL 明确指出冲突的 nodeCode 与已占用记录的当前 status（requirements US-040）。
 *
 * @author hwyz_leo
 */
@Getter
public class VehicleNodeDuplicateCodeException extends RuntimeException {

    /**
     * 错误码常量
     */
    public static final int ERROR_CODE = 812002;

    /**
     * 冲突的 nodeCode
     */
    private final String conflictNodeCode;

    /**
     * 已占用记录的当前状态
     */
    private final String existingStatus;

    public VehicleNodeDuplicateCodeException(String conflictNodeCode, String existingStatus) {
        super(String.format("车载节点 nodeCode 已存在: %s（已占用记录状态: %s）", conflictNodeCode, existingStatus));
        this.conflictNodeCode = conflictNodeCode;
        this.existingStatus = existingStatus;
    }

    public VehicleNodeDuplicateCodeException(String conflictNodeCode, String existingStatus, Throwable cause) {
        super(String.format("车载节点 nodeCode 已存在: %s（已占用记录状态: %s）", conflictNodeCode, existingStatus), cause);
        this.conflictNodeCode = conflictNodeCode;
        this.existingStatus = existingStatus;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
