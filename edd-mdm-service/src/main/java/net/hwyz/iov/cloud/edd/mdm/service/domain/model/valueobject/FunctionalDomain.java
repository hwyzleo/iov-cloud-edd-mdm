package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

/**
 * 车载节点功能域枚举（EEAD 子域）
 * <p>
 * 行业经典 7 域 + CROSS_DOMAIN（容纳 CGW / HPC 等中央计算节点）+ OTHER 兜底。
 *
 * @author hwyz_leo
 */
public enum FunctionalDomain {

    /**
     * 动力域（发动机、变速箱、电池、电驱）
     */
    POWERTRAIN,

    /**
     * 底盘域（制动、转向、悬挂、ESP 等）
     */
    CHASSIS,

    /**
     * 车身域（车窗、门锁、座椅、灯光、空调控制基础）
     */
    BODY,

    /**
     * 智能驾驶域（雷达、激光雷达、摄像头、ADAS 主控等）
     */
    ADAS,

    /**
     * 智能座舱域（IVI、仪表、HUD、HMI、空调）
     */
    COCKPIT,

    /**
     * 车联域（TBOX、4G/5G 模组、V2X）
     */
    CONNECTIVITY,

    /**
     * 能源域（高压电池管理、充电控制 OBC、PDU）
     */
    ENERGY,

    /**
     * 跨域（CGW、HPC、中央计算节点等）
     */
    CROSS_DOMAIN,

    /**
     * 其他（兜底）
     */
    OTHER
}
