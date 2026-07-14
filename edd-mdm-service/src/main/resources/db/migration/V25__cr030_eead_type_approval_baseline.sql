-- V25: CR-030 EEAD 子域新增型式批准基线（Type-Approval Baseline）
-- 参考 MDM-DSN-CR-030 变更内容
-- 关联 requirements US-121 ~ US-128

-- ============================================================
-- 1. 型式批准基线主表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_eead_type_approval_baseline` (
    `id`                     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '技术主键，自增',
    `ta_baseline_code`       VARCHAR(64)  NOT NULL COMMENT '业务键；全局唯一、不可变、不复用；系统发号',
    `swin_code`              VARCHAR(64)  NOT NULL COMMENT '-> mdm_eead_swin_definition.swin_code，须 ACTIVE，应用层松引用',
    `anchor_type`            VARCHAR(16)  NOT NULL COMMENT 'VARIANT / MODEL；须与 SWIN 型式引用层级一致',
    `anchor_code`            VARCHAR(64)  NOT NULL COMMENT 'Variant / Model code，与 SWIN 型式引用一致',
    `status`                 VARCHAR(16)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT / RELEASED / FROZEN',
    `projection_digest`      VARCHAR(128) NOT NULL COMMENT '型批相关版本组合摘要，sha256: 前缀；同锚点幂等键',
    `source_baseline_scope`  JSON                  DEFAULT NULL COMMENT '参与卷积的 SoftwareBaseline code 列表（溯源）',
    `effective_from`         DATETIME              DEFAULT NULL COMMENT '生效时间（发布 / 型批通过）',
    `remark`                 VARCHAR(512)          DEFAULT NULL COMMENT '备注',
    `version`                INT          NOT NULL DEFAULT 1 COMMENT '业务版本号，每次变更+1',
    `create_by`              VARCHAR(64)  NOT NULL COMMENT '创建人',
    `create_time`            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`              VARCHAR(64)  NOT NULL COMMENT '修改人',
    `modify_time`            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`            INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`              TINYINT      NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_TA_BASELINE_CODE` (`ta_baseline_code`),
    UNIQUE KEY `UK_TA_BASELINE_ANCHOR_DIGEST` (`swin_code`, `anchor_type`, `anchor_code`, `projection_digest`),
    KEY `IDX_TA_BASELINE_SWIN` (`swin_code`),
    KEY `IDX_TA_BASELINE_STATUS` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='型式批准基线表（EEAD 子域）';

-- ============================================================
-- 2. 型式批准基线项子表（聚合内子实体）
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_eead_type_approval_baseline_item` (
    `id`                     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '技术主键',
    `ta_baseline_id`         BIGINT       NOT NULL COMMENT '-> mdm_eead_type_approval_baseline.id',
    `vehicle_node_code`      VARCHAR(64)  NOT NULL COMMENT '型批相关受管节点，-> SwinManagedSystem（is_type_approval_relevant=1）',
    `part_code`              VARCHAR(64)  NOT NULL COMMENT '软件零件号，is_software=1',
    `approved_version`       VARCHAR(64)  NOT NULL COMMENT '型批基准版本',
    `source_baseline_code`   VARCHAR(64)  NOT NULL COMMENT '该项来源 SoftwareBaseline code（溯源）',
    `create_by`              VARCHAR(64)  NOT NULL COMMENT '创建人',
    `create_time`            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `row_version`            INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`              TINYINT      NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_TA_ITEM` (`ta_baseline_id`, `vehicle_node_code`, `part_code`),
    KEY `IDX_TA_ITEM_BASELINE` (`ta_baseline_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='型式批准基线项表（EEAD 子域）';

-- ============================================================
-- 3. 型式批准基线历史快照表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_eead_type_approval_baseline_history` (
    `snapshot_id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '快照主键',
    `entity_id`              BIGINT                DEFAULT NULL COMMENT '关联主表 id',
    `operation_type`         VARCHAR(16)  NOT NULL COMMENT '操作类型：CREATE/RELEASE/FREEZE/DELETE',
    `snapshot_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '快照时间',
    `operator`               VARCHAR(64)  NOT NULL COMMENT '操作人',
    `ta_baseline_code`       VARCHAR(64)  NOT NULL COMMENT '业务键',
    `swin_code`              VARCHAR(64)  NOT NULL COMMENT 'SWIN 定义代码',
    `anchor_type`            VARCHAR(16)  NOT NULL COMMENT '锚定层级枚举',
    `anchor_code`            VARCHAR(64)  NOT NULL COMMENT '锚点编码',
    `status`                 VARCHAR(16)  NOT NULL COMMENT 'DRAFT / RELEASED / FROZEN',
    `projection_digest`      VARCHAR(128) NOT NULL COMMENT '投影摘要',
    `source_baseline_scope`  JSON                  DEFAULT NULL COMMENT '参与卷积的 SoftwareBaseline code 列表',
    `effective_from`         DATETIME              DEFAULT NULL COMMENT '生效时间',
    `remark`                 VARCHAR(512)          DEFAULT NULL COMMENT '备注',
    `version`                INT          NOT NULL COMMENT '业务版本号',
    `items_snapshot`         JSON                  DEFAULT NULL COMMENT '基线项清单快照（全量基线项 JSON）',
    `create_by`              VARCHAR(64)  NOT NULL COMMENT '创建人',
    `create_time`            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`              VARCHAR(64)  NOT NULL COMMENT '修改人',
    `modify_time`            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`            INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`              TINYINT      NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`snapshot_id`),
    KEY `IDX_TABH_ENTITY_ID` (`entity_id`),
    KEY `IDX_TABH_CODE_VERSION` (`ta_baseline_code`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='型式批准基线历史快照表（EEAD 子域）';

-- ============================================================
-- 4. 型式批准基线序列表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_eead_ta_baseline_seq` (
    `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `seq_name`   VARCHAR(64) NOT NULL COMMENT '序列名称，固定值 TA_BASELINE',
    `next_val`   BIGINT      NOT NULL DEFAULT 1 COMMENT '下一个可用值',
    `create_time` DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_time` DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_TA_SEQ_NAME` (`seq_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='型式批准基线序列表（EEAD 子域）';

-- 初始化序列
INSERT INTO `mdm_eead_ta_baseline_seq` (`seq_name`, `next_val`) VALUES ('TA_BASELINE', 1);
