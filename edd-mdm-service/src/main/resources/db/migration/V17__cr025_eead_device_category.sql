-- V17: CR-025 EEAD 子域新增 DeviceCategory（设备类别）字典表
-- 参考 MDM-DSN-CR-025 变更内容
-- 关联 requirements US-083 ~ US-088

-- ============================================================
-- 1. 设备类别主表（扁平字典，无 parent_code）
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_eead_device_category` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`                VARCHAR(64)  NOT NULL COMMENT '业务主键，全局唯一，创建后不可变',
    `name`                VARCHAR(128) NOT NULL COMMENT '设备类别名称',
    `name_local`          VARCHAR(128)          DEFAULT NULL COMMENT '本地化名称',
    `description`         VARCHAR(512)          DEFAULT NULL COMMENT '描述',
    `sort_order`          INT          NOT NULL DEFAULT 0 COMMENT '排序序号（升序）',
    `source`              VARCHAR(16)  NOT NULL DEFAULT 'MANUAL' COMMENT '数据来源：MDM/MANUAL，默认 MANUAL',
    `external_ref_id`     VARCHAR(64)           DEFAULT NULL COMMENT '上游系统主键（source=MANUAL 时为 NULL）',
    `external_version`    BIGINT                DEFAULT NULL COMMENT '上游系统版本号',
    `last_sync_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后同步时间',
    `version`             INT          NOT NULL DEFAULT 1 COMMENT '业务版本号，每次变更+1',
    `effective_from`      DATETIME              DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`        DATETIME              DEFAULT NULL COMMENT '生效结束时间',
    `status`              VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：DRAFT/ACTIVE/INACTIVE',
    `create_by`           VARCHAR(64)  NOT NULL COMMENT '创建人',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)  NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT      NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_DC_CODE` (`code`),
    KEY `IDX_DC_STATUS` (`status`),
    KEY `IDX_DC_SORT` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备类别字典表（EEAD 子域）';

-- ============================================================
-- 2. 设备类别历史快照表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_eead_device_category_history` (
    `snapshot_id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '快照主键',
    `entity_id`           BIGINT                DEFAULT NULL COMMENT '关联主表 id（DRAFT 物理删除时主表 id 已消失，允许 NULL）',
    `operation_type`      VARCHAR(16)  NOT NULL COMMENT '操作类型：CREATE/UPDATE/DEACTIVATE/DELETE',
    `snapshot_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '快照时间',
    `operator`            VARCHAR(64)  NOT NULL COMMENT '操作人',
    `code`                VARCHAR(64)  NOT NULL COMMENT '业务主键',
    `name`                VARCHAR(128) NOT NULL COMMENT '设备类别名称',
    `name_local`          VARCHAR(128)          DEFAULT NULL COMMENT '本地化名称',
    `description`         VARCHAR(512)          DEFAULT NULL COMMENT '描述',
    `sort_order`          INT          NOT NULL DEFAULT 0 COMMENT '排序序号',
    `source`              VARCHAR(16)  NOT NULL DEFAULT 'MANUAL' COMMENT '数据来源',
    `external_ref_id`     VARCHAR(64)           DEFAULT NULL COMMENT '上游系统主键',
    `external_version`    BIGINT                DEFAULT NULL COMMENT '上游系统版本号',
    `last_sync_time`      DATETIME     NOT NULL COMMENT '最后同步时间',
    `version`             INT          NOT NULL COMMENT '业务版本号',
    `effective_from`      DATETIME              DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`        DATETIME              DEFAULT NULL COMMENT '生效结束时间',
    `status`              VARCHAR(16)  NOT NULL COMMENT '状态',
    `create_by`           VARCHAR(64)  NOT NULL COMMENT '创建人',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)  NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT      NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`snapshot_id`),
    KEY `IDX_DCH_ENTITY_ID` (`entity_id`),
    KEY `IDX_DCH_CODE_VERSION` (`code`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备类别历史快照表（EEAD 子域）';

-- ============================================================
-- 3. VehicleNode 表新增索引（支撑删除前置反查）
-- ============================================================
ALTER TABLE `mdm_eead_vehicle_node` ADD INDEX `IDX_VN_DEVICE_CATEGORY` (`device_category`);
