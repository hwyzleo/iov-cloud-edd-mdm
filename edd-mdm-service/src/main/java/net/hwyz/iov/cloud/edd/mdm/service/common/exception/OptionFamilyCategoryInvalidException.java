package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 选项族商品分类（category）校验异常
 * <p>
 * 当 category 为空或取值不在枚举范围时抛出。
 * CR-010 新增。
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class OptionFamilyCategoryInvalidException extends MdmBaseException {

    private final String category;

    public OptionFamilyCategoryInvalidException(String category) {
        super(MdmErrorCode.OPTION_FAMILY_CATEGORY_INVALID,
                String.format("选项族商品分类（category）无效: %s，取值范围: EXTERIOR/INTERIOR/POWERTRAIN/INTELLIGENT/COMFORT/SAFETY/ACCESSORY/OTHER", category));
        this.category = category;
        log.warn("选项族商品分类校验失败: category={}", category);
    }
}
