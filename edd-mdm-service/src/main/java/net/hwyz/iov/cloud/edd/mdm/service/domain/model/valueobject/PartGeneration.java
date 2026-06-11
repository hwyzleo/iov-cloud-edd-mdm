package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

/**
 * 零件代次值对象
 * 2位字母，从AA起步按AA→AB→…→AZ→BA→…→ZZ递增
 * CR-023 新增
 */
public record PartGeneration(String value) {
    public static final PartGeneration AA = new PartGeneration("AA");
    public static final PartGeneration ZZ = new PartGeneration("ZZ");

    public PartGeneration {
        if (value == null || value.length() != 2 || !value.matches("[A-Z]{2}")) {
            throw new IllegalArgumentException("代次必须为2位大写字母: " + value);
        }
    }

    /**
     * 计算下一代次
     */
    public PartGeneration next() {
        if (equals(ZZ)) {
            return null; // 溢出
        }
        char first = value.charAt(0);
        char second = value.charAt(1);
        if (second == 'Z') {
            return new PartGeneration("" + (char)(first + 1) + 'A');
        } else {
            return new PartGeneration("" + first + (char)(second + 1));
        }
    }

    /**
     * 判断是否溢出（已经是ZZ）
     */
    public boolean isOverflow() {
        return equals(ZZ);
    }

    /**
     * 从字符串解析代次
     */
    public static PartGeneration parse(String code) {
        if (code == null || code.length() < 2) {
            return null;
        }
        String gen = code.substring(code.length() - 2);
        if (gen.matches("[A-Z]{2}")) {
            return new PartGeneration(gen);
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
