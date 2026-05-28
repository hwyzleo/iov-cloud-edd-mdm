package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

/**
 * 车载节点类型枚举（EEAD 子域）
 * <p>
 * 行业标准 11 类，覆盖现代 E/E 架构主流节点类型。
 *
 * @author hwyz_leo
 */
public enum NodeType {

    /**
     * 域控制器（Domain Controller Unit）
     */
    DCU,

    /**
     * 电控单元（Electronic Control Unit）
     */
    ECU,

    /**
     * 微控制器单元（Microcontroller Unit）
     */
    MCU,

    /**
     * 传感器（雷达、摄像头、超声波、激光雷达等）
     */
    SENSOR,

    /**
     * 执行器（电机、阀、泵等）
     */
    ACTUATOR,

    /**
     * 网关（车内网络互联节点，如 CGW）
     */
    GATEWAY,

    /**
     * 通讯终端（TBOX、5G 模组等）
     */
    TELEMATICS,

    /**
     * 人机交互节点（仪表、HUD、IVI 主机等）
     */
    HMI,

    /**
     * 充电节点（OBC、充电控制器等）
     */
    CHARGER,

    /**
     * 车载交换机（车载以太网架构下的 L2 交换节点）
     */
    SWITCH,

    /**
     * 其他（兜底）
     */
    OTHER
}
