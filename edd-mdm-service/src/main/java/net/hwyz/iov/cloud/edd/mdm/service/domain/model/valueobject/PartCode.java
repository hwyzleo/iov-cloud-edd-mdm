package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

/**
 * 零件号值对象
 * 封装 8位零填充流水 + 2位字母代次 的拼装/解析/反解
 * CR-023 新增，CR-032 移除软件件S前缀，软硬件统一无前缀骨架
 */
public record PartCode(String code, String baseNo, PartGeneration generation) {
    private static final int SEQ_LENGTH = 8;
    private static final long SEQ_MAX = 99_999_999L;

    /**
     * 系统发号构造
     * 软硬件统一无前缀骨架：8位零填充流水 + 2位字母代次
     * 软硬件区分仅由 is_software 承载，不编入 code/base_no
     */
    public static PartCode generate(boolean isSoftware, boolean isAssembly, long seq) {
        if (seq > SEQ_MAX) {
            throw new IllegalArgumentException("序号溢出: " + seq);
        }
        String seqStr = String.format("%0" + SEQ_LENGTH + "d", seq);
        String baseNo = seqStr;
        String code = baseNo + PartGeneration.AA.value();
        return new PartCode(code, baseNo, PartGeneration.AA);
    }

    /**
     * 从既有code反解（导入/上游直采场景）
     */
    public static PartCode parse(String code) {
        if (code == null || code.length() < SEQ_LENGTH + 2) {
            throw new IllegalArgumentException("零件号格式非法: " + code);
        }
        PartGeneration gen = PartGeneration.parse(code);
        if (gen == null) {
            throw new IllegalArgumentException("零件号代次解析失败: " + code);
        }
        String baseNo = code.substring(0, code.length() - 2);
        return new PartCode(code, baseNo, gen);
    }

    /**
     * 基于base_no生成下一代次的code
     */
    public PartCode nextGeneration() {
        PartGeneration nextGen = generation.next();
        if (nextGen == null) {
            return null; // 溢出ZZ
        }
        String newCode = baseNo + nextGen.value();
        return new PartCode(newCode, baseNo, nextGen);
    }

    public static long getSeqMax() {
        return SEQ_MAX;
    }
}
