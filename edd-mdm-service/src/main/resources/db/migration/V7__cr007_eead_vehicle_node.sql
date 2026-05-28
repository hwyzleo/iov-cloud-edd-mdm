-- V7: CR-007 引入 EEAD 子域，新增 VehicleNode（车载节点）字典表
-- 参考 specs/mdm/design.md §3.1 mdm_eead_vehicle_node / §3.2 mdm_eead_vehicle_node_history
-- 关联 requirements US-039 ~ US-046

-- ============================================================
-- 1. 车载节点主表（EEAD 子域第一张表，强制带 eead 中缀前缀）
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_eead_vehicle_node` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `node_code`           VARCHAR(64)  NOT NULL COMMENT '业务主键，全局唯一（如 TBOX/CCP/IDCM/LIDAR_F）',
    `node_name`           VARCHAR(128) NOT NULL COMMENT '节点中文名称',
    `node_name_en`        VARCHAR(128)          DEFAULT NULL COMMENT '节点英文名称',
    `description`         VARCHAR(512)          DEFAULT NULL COMMENT '描述/备注',
    `node_type`           VARCHAR(32)  NOT NULL COMMENT '节点类型：DCU/ECU/MCU/SENSOR/ACTUATOR/GATEWAY/TELEMATICS/HMI/CHARGER/SWITCH/OTHER',
    `functional_domain`   VARCHAR(32)  NOT NULL COMMENT '功能域：POWERTRAIN/CHASSIS/BODY/ADAS/COCKPIT/CONNECTIVITY/ENERGY/CROSS_DOMAIN/OTHER',
    `device_category`     VARCHAR(64)           DEFAULT NULL COMMENT '设备分类（比 node_type 更细的子分类）',
    `is_core_node`        TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否核心节点：1-是，0-否',
    `ota_support_type`    VARCHAR(32)  NOT NULL COMMENT 'OTA 支持类型：FOTA/SOTA/BOTH/NOT_SUPPORTED',
    `hsm_capability`      VARCHAR(16)           DEFAULT NULL COMMENT 'HSM 能力：NONE/SHE/HSM_LIGHT/HSM_FULL',
    `security_level`      VARCHAR(8)            DEFAULT NULL COMMENT '信息安全等级（ISO/SAE 21434）：QM/CAL1/CAL2/CAL3/CAL4',
    `source`              VARCHAR(16)  NOT NULL DEFAULT 'MANUAL' COMMENT '数据来源：MDM/MANUAL，默认 MANUAL',
    `external_ref_id`     VARCHAR(64)           DEFAULT NULL COMMENT '上游系统主键（source=MANUAL 时为 NULL）',
    `external_version`    BIGINT                DEFAULT NULL COMMENT '上游系统版本号',
    `last_sync_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后同步时间',
    `version`             INT          NOT NULL DEFAULT 1 COMMENT '业务版本号，每次变更+1',
    `effective_from`      DATETIME              DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`        DATETIME              DEFAULT NULL COMMENT '生效结束时间',
    `status`              VARCHAR(16)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT/ACTIVE/INACTIVE',
    `create_by`           VARCHAR(64)  NOT NULL COMMENT '创建人',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)  NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT      NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_vn_node_code` (`node_code`),
    KEY `idx_vn_ota_status` (`ota_support_type`, `status`),
    KEY `idx_vn_type_status` (`node_type`, `status`),
    KEY `idx_vn_domain_status` (`functional_domain`, `status`),
    KEY `idx_vn_core_status` (`is_core_node`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车载节点主表（EEAD 子域）';

-- ============================================================
-- 2. 车载节点历史快照表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_eead_vehicle_node_history` (
    `snapshot_id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '快照主键',
    `entity_id`           BIGINT                DEFAULT NULL COMMENT '关联主表 id（DRAFT 物理删除时主表 id 已消失，允许 NULL）',
    `node_code`           VARCHAR(64)  NOT NULL COMMENT '业务主键',
    `node_name`           VARCHAR(128) NOT NULL COMMENT '节点中文名称',
    `node_name_en`        VARCHAR(128)          DEFAULT NULL COMMENT '节点英文名称',
    `description`         VARCHAR(512)          DEFAULT NULL COMMENT '描述/备注',
    `node_type`           VARCHAR(32)  NOT NULL COMMENT '节点类型',
    `functional_domain`   VARCHAR(32)  NOT NULL COMMENT '功能域',
    `device_category`     VARCHAR(64)           DEFAULT NULL COMMENT '设备分类',
    `is_core_node`        TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否核心节点',
    `ota_support_type`    VARCHAR(32)  NOT NULL COMMENT 'OTA 支持类型',
    `hsm_capability`      VARCHAR(16)           DEFAULT NULL COMMENT 'HSM 能力',
    `security_level`      VARCHAR(8)            DEFAULT NULL COMMENT '信息安全等级',
    `source`              VARCHAR(16)  NOT NULL DEFAULT 'MANUAL' COMMENT '数据来源',
    `external_ref_id`     VARCHAR(64)           DEFAULT NULL COMMENT '上游系统主键',
    `external_version`    BIGINT                DEFAULT NULL COMMENT '上游系统版本号',
    `last_sync_time`      DATETIME     NOT NULL COMMENT '最后同步时间',
    `version`             INT          NOT NULL COMMENT '业务版本号',
    `effective_from`      DATETIME              DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`        DATETIME              DEFAULT NULL COMMENT '生效结束时间',
    `status`              VARCHAR(16)  NOT NULL COMMENT '状态',
    `operation_type`      VARCHAR(16)  NOT NULL COMMENT '操作类型：CREATE/UPDATE/DEACTIVATE/DELETE',
    `snapshot_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '快照时间',
    `operator`            VARCHAR(64)  NOT NULL COMMENT '操作人',
    `force_delete`        TINYINT(1)   NOT NULL DEFAULT 0 COMMENT 'force 旁路删除标识：1-是，0-否（仅 DELETE 操作有意义，US-045）',
    `create_by`           VARCHAR(64)  NOT NULL COMMENT '创建人',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)  NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT      NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`snapshot_id`),
    KEY `idx_vnh_entity_id` (`entity_id`),
    KEY `idx_vnh_code_version` (`node_code`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车载节点历史快照表（EEAD 子域）';
