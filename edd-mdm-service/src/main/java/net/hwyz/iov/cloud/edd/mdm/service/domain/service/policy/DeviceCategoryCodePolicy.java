package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import net.hwyz.iov.cloud.edd.mdm.service.common.exception.DeviceCategoryCodeFormatInvalidException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * 设备类别编码策略（CR-037 §3.1）
 * <p>
 * 统一设备族 code 规则：
 * <ul>
 *   <li>格式：^[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)*$，仅允许大写英文字母、数字和下划线</li>
 *   <li>受控精简标识，优先采用行业常用缩写（TBOX、VCU、BMS、CGW、CAM）；长度不超过 16 字符</li>
 *   <li>行业缩写存在歧义时使用短组合消歧（如 BRAKE_ECU、AIRBAG_ECU），不使用含义不唯一的 BCU/ACU</li>
 *   <li>code 不得写入节点级规格语义：通信制式（4G/5G）、安装方位（FRONT/REAR）、
 *       功率（11KW）、分辨率（8M）、线数（128）、硬件代次（GEN1）等仅作为 VehicleNode 限定词</li>
 *   <li>code 输入不自动改写；小写、连续下划线、首尾下划线和非 ASCII 字符直接拒绝；更新接口忽略 code 修改</li>
 * </ul>
 *
 * @author hwyz_leo
 */
@Component
public class DeviceCategoryCodePolicy {

    /**
     * code 长度上限（CR-037 建议不超过 16 字符）
     */
    public static final int CODE_MAX_LENGTH = 16;

    /**
     * 设备族统一格式：大写字母开头，仅大写字母/数字/下划线，禁止连续下划线、首尾下划线
     */
    private static final Pattern STANDARD_PATTERN = Pattern.compile(
            "^[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)*$");

    /**
     * 节点级规格段（通信制式/安装方位/硬件代次等），出现即判定为非法设备族 code
     */
    private static final Set<String> SPEC_SEGMENTS = Set.of(
            // 通信制式（4G/5G 等蜂窝代次；V2X 作为设备族命名技术本身不在此列，如 V2X_OBU）
            "4G", "5G", "3G", "LTE", "NR",
            // 安装方位/位置
            "FRONT", "REAR", "LEFT", "RIGHT", "FL", "FR", "RL", "RR",
            "CORNER", "SIDE", "TOP", "BOTTOM", "DOOR", "ROOF", "PILLAR",
            // 硬件代次
            "GEN1", "GEN2", "GEN3", "GEN4", "GEN5", "GEN6",
            "G1", "G2", "G3", "G4", "G5", "G6");

    /**
     * 数字规格段（功率/分辨率/线数/电压等）：11KW、800V、8M、128、12IN、8PORT 等
     */
    private static final Pattern SPEC_NUMERIC_PATTERN = Pattern.compile(
            "^\\d+(M|K|MP|KW|W|V|PORT|IN|CH|P|S)?$");

    /**
     * 是否为标准设备族格式（含格式与长度校验，不含规格语义校验）
     */
    public boolean isStandardFormat(String code) {
        return code != null && code.length() <= CODE_MAX_LENGTH && STANDARD_PATTERN.matcher(code).matches();
    }

    /**
     * code 是否携带节点级规格语义（通信制式/安装方位/功率/分辨率/线数/硬件代次等）
     */
    public boolean containsNodeSpec(String code) {
        if (code == null) {
            return false;
        }
        String[] segments = code.split("_");
        for (String segment : segments) {
            if (SPEC_SEGMENTS.contains(segment) || SPEC_NUMERIC_PATTERN.matcher(segment).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否为合法的设备族 code（格式 + 长度 + 无节点级规格语义）
     */
    public boolean isValidDeviceFamilyCode(String code) {
        return isStandardFormat(code) && !containsNodeSpec(code);
    }

    /**
     * 校验设备族 code，非法时抛出 DeviceCategoryCodeFormatInvalidException（812344）
     */
    public void validateCodeFormat(String code) {
        if (!isValidDeviceFamilyCode(code)) {
            throw new DeviceCategoryCodeFormatInvalidException(code);
        }
    }
}
