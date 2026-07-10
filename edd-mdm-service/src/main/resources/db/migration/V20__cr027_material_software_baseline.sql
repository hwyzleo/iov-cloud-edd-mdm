-- V20: CR-027 Material 子域新增软件基线（SoftwareBaseline）
-- 参考 MDM-DSN-CR-027 变更内容
-- 关联 requirements US-100 ~ US-111

-- ============================================================
-- 1. 软件基线主表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_material_software_baseline` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键，自增（surrogate，无语义、不可变，承担内部 FK）',
    `code`                VARCHAR(64)  NOT NULL COMMENT '业务主键，全局唯一，创建后不可变；由系统按 {anchorCode} + 基线版本拼装发号',
    `name`                VARCHAR(128) NOT NULL COMMENT '基线名称',
    `anchor_type`         VARCHAR(16)  NOT NULL COMMENT '锚定层级枚举：CONFIGURATION / VARIANT',
    `anchor_code`         VARCHAR(64)  NOT NULL COMMENT '锚点编码（按 anchor_type -> mdm_configuration.code 或 mdm_variant.code，须 ACTIVE）',
    `baseline_version`    VARCHAR(64)  NOT NULL COMMENT '基线版本标识（同一锚点下唯一）',
    `baseline_status`     VARCHAR(16)  NOT NULL DEFAULT 'DRAFT' COMMENT '基线生命周期枚举：DRAFT / RELEASED / SUPERSEDED',
    `released_at`         DATETIME              DEFAULT NULL COMMENT '发布时间（RELEASED 时写入）',
    `released_by`         VARCHAR(64)           DEFAULT NULL COMMENT '发布人（RELEASED 时写入）',
    `superseded_by_code`  VARCHAR(64)           DEFAULT NULL COMMENT '取代本基线的新基线 code（SUPERSEDED 时写入）',
    `description`         VARCHAR(512)          DEFAULT NULL COMMENT '描述',
    `source_system`       VARCHAR(16)  NOT NULL DEFAULT 'LOCAL' COMMENT '数据来源系统，默认 LOCAL',
    `source_id`           VARCHAR(64)           DEFAULT NULL COMMENT '上游系统主键',
    `source_version`      VARCHAR(64)           DEFAULT NULL COMMENT '上游系统版本号',
    `ingestion_channel`   VARCHAR(16)           DEFAULT NULL COMMENT '接入渠道',
    `ingestion_time`      DATETIME              DEFAULT NULL COMMENT '接入时间',
    `source_payload_hash` VARCHAR(128)          DEFAULT NULL COMMENT '上游 payload 哈希',
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
    UNIQUE KEY `UK_SWB_CODE` (`code`),
    UNIQUE KEY `UK_SWB_ANCHOR_VERSION` (`anchor_type`, `anchor_code`, `baseline_version`),
    KEY `IDX_SWB_ANCHOR` (`anchor_type`, `anchor_code`),
    KEY `IDX_SWB_STATUS` (`baseline_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='软件基线表（Material 子域）';

-- ============================================================
-- 2. 软件基线历史快照表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_material_software_baseline_history` (
    `snapshot_id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '快照主键',
    `entity_id`           BIGINT                DEFAULT NULL COMMENT '关联主表 id（DRAFT 物理删除时主表 id 已消失，允许 NULL）',
    `operation_type`      VARCHAR(16)  NOT NULL COMMENT '操作类型：CREATE/UPDATE/RELEASE/SUPERSEDE/DELETE',
    `snapshot_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '快照时间',
    `operator`            VARCHAR(64)  NOT NULL COMMENT '操作人',
    `code`                VARCHAR(64)  NOT NULL COMMENT '业务主键',
    `name`                VARCHAR(128) NOT NULL COMMENT '基线名称',
    `anchor_type`         VARCHAR(16)  NOT NULL COMMENT '锚定层级枚举',
    `anchor_code`         VARCHAR(64)  NOT NULL COMMENT '锚点编码',
    `baseline_version`    VARCHAR(64)  NOT NULL COMMENT '基线版本标识',
    `baseline_status`     VARCHAR(16)  NOT NULL COMMENT '基线生命周期枚举',
    `released_at`         DATETIME              DEFAULT NULL COMMENT '发布时间',
    `released_by`         VARCHAR(64)           DEFAULT NULL COMMENT '发布人',
    `superseded_by_code`  VARCHAR(64)           DEFAULT NULL COMMENT '取代本基线的新基线 code',
    `description`         VARCHAR(512)          DEFAULT NULL COMMENT '描述',
    `source_system`       VARCHAR(16)  NOT NULL DEFAULT 'LOCAL' COMMENT '数据来源系统',
    `source_id`           VARCHAR(64)           DEFAULT NULL COMMENT '上游系统主键',
    `source_version`      VARCHAR(64)           DEFAULT NULL COMMENT '上游系统版本号',
    `ingestion_channel`   VARCHAR(16)           DEFAULT NULL COMMENT '接入渠道',
    `ingestion_time`      DATETIME              DEFAULT NULL COMMENT '接入时间',
    `source_payload_hash` VARCHAR(128)          DEFAULT NULL COMMENT '上游 payload 哈希',
    `version`             INT          NOT NULL COMMENT '业务版本号',
    `effective_from`      DATETIME              DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`        DATETIME              DEFAULT NULL COMMENT '生效结束时间',
    `status`              VARCHAR(16)  NOT NULL COMMENT '状态',
    `items_snapshot`      JSON                  DEFAULT NULL COMMENT '基线项清单快照（全量基线项 JSON）',
    `create_by`           VARCHAR(64)  NOT NULL COMMENT '创建人',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)  NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT      NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    `force_delete`        TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '物理删除标记：1-是，0-否',
    PRIMARY KEY (`snapshot_id`),
    KEY `idx_swbh_entity_id` (`entity_id`),
    KEY `idx_swbh_code_version` (`code`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='软件基线历史快照表（Material 子域）';

-- ============================================================
-- 3. 软件基线项子表（聚合内子实体）
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_material_software_baseline_item` (
    `id`                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键，自增（surrogate）',
    `baseline_code`         VARCHAR(64)  NOT NULL COMMENT '所属软件基线（-> mdm_material_software_baseline.code）',
    `part_code`             VARCHAR(64)  NOT NULL COMMENT '软件件零件（-> mdm_material_part.code，须存在且 is_software=true）',
    `part_software_version` VARCHAR(64)           DEFAULT NULL COMMENT '该软件件在本基线中的应装软件版本 / 代次约束',
    `vehicle_node_code`     VARCHAR(64)           DEFAULT NULL COMMENT '冗余承载节点（取自 Part.vehicle_node_code，便于 OTA 圈选）',
    `fota_upgradeable`      TINYINT(1)            DEFAULT NULL COMMENT '冗余自 Part，标识该软件件是否可 FOTA 升级',
    `quantity`              INT          NOT NULL DEFAULT 1 COMMENT '数量，默认 1',
    `position`              VARCHAR(64)           DEFAULT NULL COMMENT '装配位置',
    `remark`                VARCHAR(512)          DEFAULT NULL COMMENT '备注',
    `effective_from`        DATETIME              DEFAULT NULL COMMENT '条目生效开始时间',
    `effective_to`          DATETIME              DEFAULT NULL COMMENT '条目生效结束时间',
    `create_by`             VARCHAR(64)  NOT NULL COMMENT '创建人',
    `create_time`           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`             VARCHAR(64)  NOT NULL COMMENT '修改人',
    `modify_time`           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`           INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`             TINYINT      NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_SWBI_BASELINE_PART` (`baseline_code`, `part_code`),
    KEY `IDX_SWBI_PART` (`part_code`),
    KEY `IDX_SWBI_BASELINE` (`baseline_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='软件基线项表（Material 子域）';
