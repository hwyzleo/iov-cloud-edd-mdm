package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * SWIN路由类型无效异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class SwinSchemeRouteInvalidException extends MdmBaseException {

    private final String routeType;

    public SwinSchemeRouteInvalidException(String routeType) {
        super(MdmErrorCode.SWIN_SCHEME_ROUTE_INVALID,
                String.format("SWIN路由类型无效: %s", routeType));
        this.routeType = routeType;
        log.warn("SWIN路由类型无效: {}", routeType);
    }
}
