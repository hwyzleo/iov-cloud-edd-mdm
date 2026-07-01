-- V18: CR-026 EEAD 子域新增 SWIN 定义域
-- 参考 MDM-DSN-CR-026 变更内容
-- 关联 requirements US-089 ~ US-095

-- ============================================================
-- 1. SWIN编码方案主表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_eead_swin_scheme` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`                VARCHAR(64)  NOT NULL COMMENT '业务主键，全局唯一，创建后不可变',
    `name`                VARCHAR(128) NOT NULL COMMENT '编码方案名称',
    `name_local`          VARCHAR(128)          DEFAULT NULL COMMENT '本地化名称',
    `description`         VARCHAR(512)          DEFAULT NULL COMMENT '描述',
    `route`               VARCHAR(16)  NOT NULL COMMENT 'SWIN路由类型：SINGLE_SWIN/MULTI_SWIN',
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
    UNIQUE KEY `UK_SWIN_SCHEME_CODE` (`code`),
    KEY `idx_ss_status` (`status`),
    KEY `idx_ss_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SWIN编码方案字典表（EEAD 子域）';

-- ============================================================
-- 2. SWIN编码方案历史快照表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_eead_swin_scheme_history` (
    `snapshot_id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '快照主键',
    `entity_id`           BIGINT                DEFAULT NULL COMMENT '关联主表 id（DRAFT 物理删除时主表 id 已消失，允许 NULL）',
    `operation_type`      VARCHAR(16)  NOT NULL COMMENT '操作类型：CREATE/UPDATE/DEACTIVATE/DELETE',
    `snapshot_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '快照时间',
    `operator`            VARCHAR(64)  NOT NULL COMMENT '操作人',
    `code`                VARCHAR(64)  NOT NULL COMMENT '业务主键',
    `name`                VARCHAR(128) NOT NULL COMMENT '编码方案名称',
    `name_local`          VARCHAR(128)          DEFAULT NULL COMMENT '本地化名称',
    `description`         VARCHAR(512)          DEFAULT NULL COMMENT '描述',
    `route`               VARCHAR(16)  NOT NULL COMMENT 'SWIN路由类型',
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
    `force_delete`        TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '物理删除标记：1-是，0-否',
    PRIMARY KEY (`snapshot_id`),
    KEY `idx_ssh_entity_id` (`entity_id`),
    KEY `idx_ssh_code_version` (`code`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SWIN编码方案历史快照表（EEAD 子域）';

-- ============================================================
-- 3. SWIN定义主表（类型级SWIN定义）
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_eead_swin_definition` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `swin_code`           VARCHAR(64)  NOT NULL COMMENT 'SWIN业务主键，全局唯一，创建后不可变',
    `scheme_code`         VARCHAR(64)  NOT NULL COMMENT '关联的编码方案代码',
    `type_ref_type`       VARCHAR(16)  NOT NULL COMMENT '引用类型：VARIANT/MODEL',
    `type_ref_code`       VARCHAR(64)  NOT NULL COMMENT '引用的Variant或Model代码',
    `name`                VARCHAR(128) NOT NULL COMMENT 'SWIN定义名称',
    `name_local`          VARCHAR(128)          DEFAULT NULL COMMENT '本地化名称',
    `description`         VARCHAR(512)          DEFAULT NULL COMMENT '描述',
    `version`             INT          NOT NULL DEFAULT 1 COMMENT '业务版本号，每次变更+1',
    `status`              VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：DRAFT/ACTIVE/INACTIVE',
    `create_by`           VARCHAR(64)  NOT NULL COMMENT '创建人',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)  NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT      NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_SWIN_DEF_SWIN_CODE` (`swin_code`),
    KEY `idx_sd_scheme` (`scheme_code`),
    KEY `idx_sd_type_ref` (`type_ref_type`, `type_ref_code`),
    KEY `idx_sd_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SWIN定义表（EEAD 子域）';

-- ============================================================
-- 4. SWIN定义历史快照表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_eead_swin_definition_history` (
    `snapshot_id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '快照主键',
    `entity_id`           BIGINT                DEFAULT NULL COMMENT '关联主表 id',
    `operation_type`      VARCHAR(16)  NOT NULL COMMENT '操作类型：CREATE/UPDATE/DEACTIVATE/DELETE',
    `snapshot_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '快照时间',
    `operator`            VARCHAR(64)  NOT NULL COMMENT '操作人',
    `swin_code`           VARCHAR(64)  NOT NULL COMMENT 'SWIN业务主键',
    `scheme_code`         VARCHAR(64)  NOT NULL COMMENT '关联的编码方案代码',
    `type_ref_type`       VARCHAR(16)  NOT NULL COMMENT '引用类型',
    `type_ref_code`       VARCHAR(64)  NOT NULL COMMENT '引用的Variant或Model代码',
    `name`                VARCHAR(128) NOT NULL COMMENT 'SWIN定义名称',
    `name_local`          VARCHAR(128)          DEFAULT NULL COMMENT '本地化名称',
    `description`         VARCHAR(512)          DEFAULT NULL COMMENT '描述',
    `version`             INT          NOT NULL COMMENT '业务版本号',
    `status`              VARCHAR(16)  NOT NULL COMMENT '状态',
    `managed_systems`     JSON                  DEFAULT NULL COMMENT '管理的软件系统清单快照',
    `create_by`           VARCHAR(64)  NOT NULL COMMENT '创建人',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)  NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT      NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    `force_delete`        TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '物理删除标记：1-是，0-否',
    PRIMARY KEY (`snapshot_id`),
    KEY `idx_sdh_entity_id` (`entity_id`),
    KEY `idx_sdh_swin_code_version` (`swin_code`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SWIN定义历史快照表（EEAD 子域）';

-- ============================================================
-- 5. SWIN管理软件系统清单表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_eead_swin_managed_system` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `swin_code`           VARCHAR(64)  NOT NULL COMMENT '关联的SWIN定义代码',
    `vehicle_node_code`   VARCHAR(64)  NOT NULL COMMENT '关联的车载节点代码',
    `create_by`           VARCHAR(64)  NOT NULL COMMENT '创建人',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)  NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT      NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_SWIN_MS_SWIN_NODE` (`swin_code`, `vehicle_node_code`),
    KEY `idx_sms_node` (`vehicle_node_code`),
    KEY `idx_sms_swin` (`swin_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SWIN管理软件系统清单表（EEAD 子域）';

-- ============================================================
-- 6. VehicleNode 表新增索引（支撑SWIN删除前置反查）
-- ============================================================
-- 注意：V17已经添加了IDX_VN_DEVICE_CATEGORY索引
-- 这里不需要再添加
