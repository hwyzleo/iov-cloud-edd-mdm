package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

/**
 * 零件生命周期阶段枚举（Material 子域）
 * <p>
 * 状态机：PROTOTYPE → PRE_PRODUCTION → MASS_PRODUCTION → PHASE_OUT → OBSOLETE
 * OBSOLETE 为终态，禁止逆向跳转。
 *
 * @author hwyz_leo
 */
public enum LifecycleStage {
    PROTOTYPE,
    PRE_PRODUCTION,
    MASS_PRODUCTION,
    PHASE_OUT,
    OBSOLETE
}
