package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 物料品类受控缩写注册表（CR-039 §5.1）
 * <p>
 * 统一维护层级语义 code 的受控缩写字典，供 CatalogLoader 静态校验与 MPT 创建/更新共用：
 * <ul>
 *   <li>L1 Scope 受控缩写：CMP / RAW / SW / IND</li>
 *   <li>L2 Domain 受控缩写：Component=BODY/EXT/INT/CHS/PWR/EGY/THM/ELEC/ADAS/GEN；
 *       Raw=MET/POLY/CHEM；Software=EMB/DATA/MODEL；Indirect=TOOL/PKG/CONS</li>
 *   <li>L3 Family 简短稳定词：优先采用行业公认缩写（HV_BATTERY、LV_BATTERY、HV_DIST、HVAC、LIDAR、ECU、HARNESS），
 *       不存在通行缩写时保留简短英文名（2~16 字符、大写 token），完整英文名称不得机械拼入 code</li>
 * </ul>
 * 输入不自动改写；未知缩写（不在受控字典或格式非法）由 MaterialCategoryCodePolicy 判定并返回 812924。
 *
 * @author hwyz_leo
 */
@Component
public class MaterialCategoryAbbreviationRegistry {

    /**
     * L1 Scope 受控缩写
     */
    public static final Set<String> SCOPE_ABBREVIATIONS = Set.of("CMP", "RAW", "SW", "IND");

    /**
     * L2 Domain 受控缩写（按 Scope 分组）
     */
    public static final Map<String, Set<String>> DOMAIN_ABBREVIATIONS = Map.of(
            "CMP", Set.of("BODY", "EXT", "INT", "CHS", "PWR", "EGY", "THM", "ELEC", "ADAS", "GEN"),
            "RAW", Set.of("MET", "POLY", "CHEM"),
            "SW", Set.of("EMB", "DATA", "MODEL"),
            "IND", Set.of("TOOL", "PKG", "CONS")
    );

    /**
     * 受控 L3 Family 简短词（行业公认缩写 + 标准目录 L3 token）
     */
    public static final Set<String> APPROVED_FAMILY_SHORT_NAMES = Set.of(
            // 车身结构与闭合
            "BIW", "CLOSURE", "GLASS", "HARDWARE",
            // 外饰
            "BUMPER", "LIGHTING", "MIRROR", "WIPER", "TRIM",
            // 内饰
            "SEAT", "IP", "CONSOLE", "RESTRAINT",
            // 底盘
            "SUSPENSION", "STEERING", "BRAKE", "WHEEL_TIRE", "AXLE_HUB", "FRAME",
            // 动力
            "EDRIVE", "ENGINE", "TRANSMISSION", "DRIVELINE", "FUEL", "EXHAUST",
            // 能源
            "TRACTION_BATTERY", "LV_BATTERY", "HV_DISTRIBUTION", "CHARGING", "POWER_CONVERSION",
            "CHARGE_PORT", "BATTERY_SWAP", "HV_BATTERY", "HV_DIST",
            // 热管理
            "HVAC", "BATTERY", "PROPULSION", "COOLING", "REFRIGERANT",
            // 电气电子
            "CONTROLLER", "ECU", "HARNESS", "CONNECTOR", "SENSOR", "ACTUATOR", "POWER_DIST", "NETWORK",
            // 智能驾驶
            "CAMERA", "RADAR", "LIDAR", "ULTRASONIC", "COMPUTE",
            // 通用件
            "FASTENER", "BEARING", "SEAL",
            // 金属
            "STEEL", "ALUMINUM", "COPPER", "OTHER",
            // 高分子
            "PLASTIC", "RUBBER", "COMPOSITE",
            // 化学
            "ADHESIVE", "COATING", "FLUID",
            // 嵌入式软件
            "BASIC", "APPLICATION", "DIAGNOSTIC",
            // 软件数据
            "CALIBRATION", "MAP", "CONFIGURATION",
            // 数字模型
            "AI", "CONTROL",
            // 工装
            "PRODUCTION", "SERVICE",
            // 包装
            "PRIMARY", "TRANSPORT", "LABEL",
            // 制造耗材
            "PROCESS", "MRO"
    );

    /**
     * 扩展叶子标记段（企业扩展 code 固定使用 _X_）
     */
    public static final String EXTENSION_MARKER = "X";

    /**
     * L3 Family token 格式：大写字母/数字/下划线分段，长度 2~16
     */
    private static final Pattern FAMILY_TOKEN_PATTERN = Pattern.compile("^[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)*$");
    private static final int FAMILY_TOKEN_MAX_LENGTH = 16;

    /**
     * 是否为受控 Scope 缩写
     */
    public boolean isScopeAbbreviation(String token) {
        return token != null && SCOPE_ABBREVIATIONS.contains(token);
    }

    /**
     * 指定 Scope 下是否为受控 Domain 缩写
     */
    public boolean isDomainAbbreviation(String scopeAbbreviation, String domainToken) {
        Set<String> domains = scopeAbbreviation == null ? null : DOMAIN_ABBREVIATIONS.get(scopeAbbreviation);
        return domains != null && domainToken != null && domains.contains(domainToken);
    }

    /**
     * 是否为受控（已批准）L3 Family 简短词
     */
    public boolean isApprovedFamilyShortName(String shortName) {
        return shortName != null && APPROVED_FAMILY_SHORT_NAMES.contains(shortName);
    }

    /**
     * 是否为格式合法的 L3 Family 简短词（大写 token、长度 2~16，允许合理新词但拒绝机械拼入完整英文名）
     */
    public boolean isValidFamilyShortName(String shortName) {
        return shortName != null
                && shortName.length() >= 2
                && shortName.length() <= FAMILY_TOKEN_MAX_LENGTH
                && FAMILY_TOKEN_PATTERN.matcher(shortName).matches();
    }
}
