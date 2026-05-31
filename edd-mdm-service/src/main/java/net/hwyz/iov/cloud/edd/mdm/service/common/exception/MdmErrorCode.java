package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hwyz.iov.cloud.framework.common.exception.ErrorCode;

/**
 * 主数据管理服务错误码
 * <p>
 * 段位规划：Product=8121XX, EEAD=8123XX, Org=8125XX, Party=8127XX, Material=8129XX
 *
 * @author hwyz_leo
 */
@Getter
@AllArgsConstructor
public enum MdmErrorCode implements ErrorCode {

    // ==================== Product 子域 (8121XX) ====================
    CODE_ALREADY_EXIST("812101", "业务主键（code）已存在"),
    RECORD_NOT_EXIST("812102", "记录不存在"),
    STATUS_NOT_ALLOW_DELETE("812103", "状态不允许删除（非 DRAFT）"),
    EFFECTIVE_PERIOD_INVALID("812104", "生效期无效（effectiveFrom > effectiveTo）"),
    BRAND_NOT_EXIST_OR_INVALID("812105", "引用的 Brand 不存在或状态无效"),
    STATUS_NOT_ALLOW_DEACTIVATE("812106", "状态不允许失效（非 ACTIVE）"),
    PARENT_ENTITY_NOT_EXIST_OR_INVALID("812107", "引用的上层实体不存在或状态无效"),
    HAS_CHILDREN_REFERENCE("812108", "存在下层实体引用，不允许删除"),
    OPTION_FAMILY_EXCLUSIVE_VIOLATION("812109", "同一选项族互斥约束违反"),
    UPSTREAM_SCHEMA_INVALID("812110", "上游消息 schema 非法或必填字段缺失"),
    UPSTREAM_AUTH_FAILED("812111", "上游来源鉴权失败"),
    UPSTREAM_VERSION_CONFLICT("812112", "同版本冲突"),
    UPSTREAM_NON_AUTHORITY("812113", "非权威源写入被拒绝"),
    CONFIGURATION_SEQ_OVERFLOW("812114", "Configuration 序号溢出"),
    VARIANT_CODE_TOO_LONG("812115", "Variant code 长度超限"),
    BRAND_HAS_ACTIVE_CHILDREN("812116", "品牌下存在活跃车系，失效被拒绝"),
    CARLINE_HAS_ACTIVE_CHILDREN("812117", "车系下存在活跃车型，失效被拒绝"),
    PLATFORM_HAS_ACTIVE_CHILDREN("812118", "平台下存在活跃车型，失效被拒绝"),
    MODEL_HAS_ACTIVE_CHILDREN("812119", "车型下存在活跃版本，失效被拒绝"),
    VARIANT_HAS_ACTIVE_CHILDREN("812121", "版本下存在活跃配置，失效被拒绝"),
    OPTION_FAMILY_HAS_ACTIVE_CHILDREN("812122", "选项族下存在活跃选项码，失效被拒绝"),
    OPTION_FAMILY_CATEGORY_INVALID("812123", "选项族商品分类（category）为空或取值不在枚举范围"),

    // ==================== EEAD 子域 (8123XX) ====================
    VEHICLE_NODE_NOT_EXIST("812301", "车载节点不存在"),
    VEHICLE_NODE_CODE_EXIST("812302", "车载节点 nodeCode 已存在"),
    VEHICLE_NODE_HAS_DOWNSTREAM_REF("812303", "车载节点存在下游引用，删除被拒绝"),

    // ==================== Org 子域 (8125XX) ====================
    PLANT_NOT_EXIST("812501", "工厂不存在"),
    PLANT_CODE_EXIST("812502", "工厂 code 已存在"),
    PLANT_HAS_DOWNSTREAM_REF("812503", "工厂存在下游引用，删除被拒绝"),
    PLANT_EFFECTIVE_PERIOD_INVALID("812504", "工厂生效期非法"),
    PLANT_UPSTREAM_SCHEMA_INVALID("812510", "上游消息 schema 非法"),
    PLANT_UPSTREAM_AUTH_FAILED("812511", "上游来源鉴权失败"),
    PLANT_UPSTREAM_VERSION_CONFLICT("812512", "同版本冲突"),
    PLANT_UPSTREAM_NON_AUTHORITY("812513", "非权威源写入被拒绝"),

    // ==================== Party 子域 (8127XX) ====================
    SUPPLIER_CODE_EXIST("812701", "供应商 code 已存在"),

    // ==================== Material 子域 (8129XX) ====================
    MATERIAL_CATEGORY_NOT_EXIST("812901", "物料品类不存在"),
    MATERIAL_CATEGORY_CODE_EXIST("812902", "物料品类 code 已存在"),
    MATERIAL_CATEGORY_HAS_CHILDREN("812903", "物料品类存在子项或被 Part 引用，删除被拒绝"),
    MATERIAL_CATEGORY_EFFECTIVE_PERIOD_INVALID("812904", "生效期非法"),
    MATERIAL_CATEGORY_PARENT_NOT_EXIST("812905", "父品类不存在"),
    MATERIAL_CATEGORY_LOOP_DETECTED("812906", "物料品类层级形成环路"),
    PART_CODE_EXIST("812910", "零件 code 已存在"),
    PART_CATEGORY_INVALID("812911", "零件 categoryCode 指向不存在或非 ACTIVE 的 MaterialCategory"),
    PART_VEHICLE_NODE_INVALID("812912", "零件 vehicleNodeCode 指向不存在的 VehicleNode"),
    PART_SUPPLIER_INVALID("812913", "零件 supplierCode 指向不存在的 Supplier"),
    PART_SUBSTITUTE_INVALID("812914", "零件 substitutePartCode 指向不存在或指向自身"),
    PART_LIFECYCLE_INVALID_TRANSITION("812915", "零件 lifecycleStage 状态机逆向跳转或 OBSOLETE 终态变更"),
    PART_HAS_DOWNSTREAM_REF("812916", "零件存在下游引用，删除被拒绝");

    private final String code;
    private final String message;

}
