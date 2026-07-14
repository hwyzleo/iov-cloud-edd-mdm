package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * TA基线node↔part映射异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class TaBaselineNodePartMappingException extends MdmBaseException {

    public TaBaselineNodePartMappingException(String vehicleNodeCode, String partCode, String reason) {
        super(MdmErrorCode.TA_BASELINE_NODE_PART_MAPPING,
                String.format("node↔part映射异常: node=%s, part=%s, reason=%s", vehicleNodeCode, partCode, reason));
        log.warn("TA基线node↔part映射异常: node={}, part={}, reason={}", vehicleNodeCode, partCode, reason);
    }
}
