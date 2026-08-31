package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 设备类别 code 格式校验异常（CR-037 §3.1）
 * <p>
 * 新建 code 不符合设备族统一格式（^[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)*$、长度≤16）或
 * 携带节点级规格语义（通信制式/安装方位/功率/分辨率/线数/硬件代次）时抛出。
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class DeviceCategoryCodeFormatInvalidException extends MdmBaseException {

    private final String code;

    public DeviceCategoryCodeFormatInvalidException(String code) {
        super(MdmErrorCode.DEVICE_CATEGORY_CODE_FORMAT_INVALID,
                String.format("设备类别 code 不符合设备族统一格式或包含节点规格语义: %s", code));
        this.code = code;
        log.warn("设备类别 code 格式校验失败: code={}", code);
    }
}
