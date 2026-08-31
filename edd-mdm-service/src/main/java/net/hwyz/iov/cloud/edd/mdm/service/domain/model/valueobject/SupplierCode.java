package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

import java.util.Collection;

/**
 * 供应商 code 值对象
 * CR-036 新增：SUP + 8 位零填充全局流水
 * <p>
 * 格式：^SUP[0-9]{8}$，流水有效范围 1 ~ 99,999,999；
 * 国家 / 供应商类型 / 品类及来源系统不参与 code 生成。
 */
public record SupplierCode(String code) {

    /** 固定前缀 */
    private static final String PREFIX = "SUP";

    /** 流水长度（8 位零填充） */
    private static final int SEQ_LENGTH = 8;

    /** 流水上限 */
    private static final long SEQ_MAX = 99_999_999L;

    /** 对外校验正则 */
    public static final String CODE_PATTERN = "^SUP[0-9]{8}$";

    /**
     * 按流水号生成系统发号 code
     *
     * @param seq 流水号（1 ~ 99,999,999）
     * @return 供应商 code 值对象
     */
    public static SupplierCode generate(long seq) {
        if (seq < 1 || seq > SEQ_MAX) {
            throw new IllegalArgumentException("供应商流水序号超出有效范围: " + seq);
        }
        String code = PREFIX + String.format("%0" + SEQ_LENGTH + "d", seq);
        return new SupplierCode(code);
    }

    /**
     * 判断 code 是否符合系统发号格式
     *
     * @param code 供应商 code
     * @return 是否匹配 ^SUP[0-9]{8}$
     */
    public static boolean matches(String code) {
        return code != null && code.matches(CODE_PATTERN);
    }

    /**
     * 从存量 code 集合中计算最大流水号（供序列初始化使用）
     *
     * @param codes 存量供应商 code 集合
     * @return 最大流水号；无符合格式记录返回 0
     */
    public static long maxSeq(Collection<String> codes) {
        if (codes == null) {
            return 0L;
        }
        long max = 0L;
        for (String code : codes) {
            if (matches(code)) {
                long seq = Long.parseLong(code.substring(PREFIX.length()));
                if (seq > max) {
                    max = seq;
                }
            }
        }
        return max;
    }

    public static long getSeqMax() {
        return SEQ_MAX;
    }
}
