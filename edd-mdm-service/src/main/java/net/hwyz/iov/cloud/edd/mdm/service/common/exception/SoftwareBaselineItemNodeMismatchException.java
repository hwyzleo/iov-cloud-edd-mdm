package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 软件基线项冗余节点与零件承载节点不一致异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class SoftwareBaselineItemNodeMismatchException extends MdmBaseException {

    public SoftwareBaselineItemNodeMismatchException(String partCode, String itemNodeCode, String partNodeCode) {
        super(MdmErrorCode.SW_BASELINE_ITEM_NODE_MISMATCH,
                String.format("基线项零件 %s 冗余节点 %s 与零件承载节点 %s 不一致", partCode, itemNodeCode, partNodeCode));
        log.warn("软件基线项节点不一致: partCode={}, itemNodeCode={}, partNodeCode={}", partCode, itemNodeCode, partNodeCode);
    }
}
