package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

/**
 * VMD 服务不可用异常（EEAD 子域）
 * <p>
 * 当 VehicleNode 删除前置依赖反查 VMD VehiclePart 失败时（超时 / 降级 / 不可用），
 * fail-safe 拒绝删除并抛出本异常。Controller 层 SHALL 映射为 503 Service Unavailable。
 * 引导用户："VMD 不可用，请稍后重试或联系 MDM-Admin 通过 force 旁路删除"（design §4 F11）。
 *
 * @author hwyz_leo
 */
public class VmdServiceUnavailableException extends RuntimeException {

    public VmdServiceUnavailableException(String message) {
        super(message);
    }

    public VmdServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
