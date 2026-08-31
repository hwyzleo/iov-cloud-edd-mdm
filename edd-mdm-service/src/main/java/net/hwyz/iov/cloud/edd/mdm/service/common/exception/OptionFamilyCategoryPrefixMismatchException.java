package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionFamilyCategory;

/**
 * 选项族 code 分类前缀与 category 不一致异常
 * <p>
 * 新建或更新标准格式选项族时，code 前缀（EXT/INT/PWR/SMART/COMF/SAFE/ACC/OTH）
 * 与 category 固定映射不一致时抛出。CR-035 新增。
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class OptionFamilyCategoryPrefixMismatchException extends MdmBaseException {

    private final String code;
    private final OptionFamilyCategory category;

    public OptionFamilyCategoryPrefixMismatchException(String code, OptionFamilyCategory category) {
        super(MdmErrorCode.OPTION_FAMILY_CATEGORY_PREFIX_MISMATCH,
                String.format("选项族 code 分类前缀与 category 不一致: code=%s, category=%s", code, category));
        this.code = code;
        this.category = category;
        log.warn("选项族 code 前缀与 category 不一致: code={}, category={}", code, category);
    }
}
