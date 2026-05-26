-- V2: 上游数据摄取支持 - 为实体表添加数据来源字段，新增来源配置表和摄取日志表

-- ============================================================
-- 1. 主表添加数据来源字段
-- ============================================================

-- 品牌表添加来源字段
ALTER TABLE `mdm_brand`
    ADD COLUMN `source_system`        VARCHAR(32)  NOT NULL DEFAULT 'LOCAL' COMMENT '来源系统：LOCAL/UPSTREAM_xxx' AFTER `status`,
    ADD COLUMN `source_id`            VARCHAR(128)          DEFAULT NULL COMMENT '来源系统中的原始ID' AFTER `source_system`,
    ADD COLUMN `source_version`       VARCHAR(64)           DEFAULT NULL COMMENT '来源数据版本号' AFTER `source_id`,
    ADD COLUMN `ingestion_channel`    VARCHAR(16)  NOT NULL DEFAULT 'LOCAL' COMMENT '摄取通道：LOCAL/API/KAFKA/MQ' AFTER `source_version`,
    ADD COLUMN `ingestion_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '摄取时间' AFTER `ingestion_channel`,
    ADD COLUMN `source_payload_hash`  VARCHAR(64)           DEFAULT NULL COMMENT '来源数据体哈希，用于变更检测' AFTER `ingestion_time`,
    ADD KEY `idx_source` (`source_system`, `source_id`);

-- 车系表添加来源字段
ALTER TABLE `mdm_series`
    ADD COLUMN `source_system`        VARCHAR(32)  NOT NULL DEFAULT 'LOCAL' COMMENT '来源系统：LOCAL/UPSTREAM_xxx' AFTER `status`,
    ADD COLUMN `source_id`            VARCHAR(128)          DEFAULT NULL COMMENT '来源系统中的原始ID' AFTER `source_system`,
    ADD COLUMN `source_version`       VARCHAR(64)           DEFAULT NULL COMMENT '来源数据版本号' AFTER `source_id`,
    ADD COLUMN `ingestion_channel`    VARCHAR(16)  NOT NULL DEFAULT 'LOCAL' COMMENT '摄取通道：LOCAL/API/KAFKA/MQ' AFTER `source_version`,
    ADD COLUMN `ingestion_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '摄取时间' AFTER `ingestion_channel`,
    ADD COLUMN `source_payload_hash`  VARCHAR(64)           DEFAULT NULL COMMENT '来源数据体哈希，用于变更检测' AFTER `ingestion_time`,
    ADD KEY `idx_source` (`source_system`, `source_id`);

-- 平台表添加来源字段
ALTER TABLE `mdm_platform`
    ADD COLUMN `source_system`        VARCHAR(32)  NOT NULL DEFAULT 'LOCAL' COMMENT '来源系统：LOCAL/UPSTREAM_xxx' AFTER `status`,
    ADD COLUMN `source_id`            VARCHAR(128)          DEFAULT NULL COMMENT '来源系统中的原始ID' AFTER `source_system`,
    ADD COLUMN `source_version`       VARCHAR(64)           DEFAULT NULL COMMENT '来源数据版本号' AFTER `source_id`,
    ADD COLUMN `ingestion_channel`    VARCHAR(16)  NOT NULL DEFAULT 'LOCAL' COMMENT '摄取通道：LOCAL/API/KAFKA/MQ' AFTER `source_version`,
    ADD COLUMN `ingestion_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '摄取时间' AFTER `ingestion_channel`,
    ADD COLUMN `source_payload_hash`  VARCHAR(64)           DEFAULT NULL COMMENT '来源数据体哈希，用于变更检测' AFTER `ingestion_time`,
    ADD KEY `idx_source` (`source_system`, `source_id`);

-- ============================================================
-- 2. 历史快照表添加数据来源字段
-- ============================================================

-- 品牌历史快照表添加来源字段
ALTER TABLE `mdm_brand_history`
    ADD COLUMN `source_system`        VARCHAR(32)  NOT NULL DEFAULT 'LOCAL' COMMENT '来源系统' AFTER `operator`,
    ADD COLUMN `source_id`            VARCHAR(128)          DEFAULT NULL COMMENT '来源系统中的原始ID' AFTER `source_system`,
    ADD COLUMN `source_version`       VARCHAR(64)           DEFAULT NULL COMMENT '来源数据版本号' AFTER `source_id`,
    ADD COLUMN `ingestion_channel`    VARCHAR(16)  NOT NULL DEFAULT 'LOCAL' COMMENT '摄取通道' AFTER `source_version`,
    ADD COLUMN `ingestion_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '摄取时间' AFTER `ingestion_channel`,
    ADD COLUMN `source_payload_hash`  VARCHAR(64)           DEFAULT NULL COMMENT '来源数据体哈希' AFTER `ingestion_time`;

-- 车系历史快照表添加来源字段
ALTER TABLE `mdm_series_history`
    ADD COLUMN `source_system`        VARCHAR(32)  NOT NULL DEFAULT 'LOCAL' COMMENT '来源系统' AFTER `operator`,
    ADD COLUMN `source_id`            VARCHAR(128)          DEFAULT NULL COMMENT '来源系统中的原始ID' AFTER `source_system`,
    ADD COLUMN `source_version`       VARCHAR(64)           DEFAULT NULL COMMENT '来源数据版本号' AFTER `source_id`,
    ADD COLUMN `ingestion_channel`    VARCHAR(16)  NOT NULL DEFAULT 'LOCAL' COMMENT '摄取通道' AFTER `source_version`,
    ADD COLUMN `ingestion_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '摄取时间' AFTER `ingestion_channel`,
    ADD COLUMN `source_payload_hash`  VARCHAR(64)           DEFAULT NULL COMMENT '来源数据体哈希' AFTER `ingestion_time`;

-- 平台历史快照表添加来源字段
ALTER TABLE `mdm_platform_history`
    ADD COLUMN `source_system`        VARCHAR(32)  NOT NULL DEFAULT 'LOCAL' COMMENT '来源系统' AFTER `operator`,
    ADD COLUMN `source_id`            VARCHAR(128)          DEFAULT NULL COMMENT '来源系统中的原始ID' AFTER `source_system`,
    ADD COLUMN `source_version`       VARCHAR(64)           DEFAULT NULL COMMENT '来源数据版本号' AFTER `source_id`,
    ADD COLUMN `ingestion_channel`    VARCHAR(16)  NOT NULL DEFAULT 'LOCAL' COMMENT '摄取通道' AFTER `source_version`,
    ADD COLUMN `ingestion_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '摄取时间' AFTER `ingestion_channel`,
    ADD COLUMN `source_payload_hash`  VARCHAR(64)           DEFAULT NULL COMMENT '来源数据体哈希' AFTER `ingestion_time`;

-- ============================================================
-- 3. 权威来源配置表
-- ============================================================

CREATE TABLE IF NOT EXISTS `mdm_authoritative_source_config` (
    `id`                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `entity_type`          VARCHAR(32)  NOT NULL COMMENT '实体类型：BRAND/SERIES/PLATFORM',
    `code_pattern`         VARCHAR(128) NOT NULL COMMENT '编码匹配模式，支持通配符*',
    `authoritative_source` VARCHAR(32)  NOT NULL COMMENT '权威来源系统标识',
    `conflict_policy`      VARCHAR(16)  NOT NULL DEFAULT 'REJECT' COMMENT '冲突策略：REJECT/MERGE/OVERWRITE',
    `priority`             INT          NOT NULL DEFAULT 999 COMMENT '优先级，数值越小优先级越高',
    `enabled`              TINYINT      NOT NULL DEFAULT 1 COMMENT '是否启用：1-启用，0-禁用',
    `create_by`            VARCHAR(64)  NOT NULL COMMENT '创建人',
    `create_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`            VARCHAR(64)  NOT NULL COMMENT '修改人',
    `modify_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`          INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`            TINYINT      NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_entity_code_priority` (`entity_type`, `code_pattern`, `priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权威来源配置表';

-- 默认配置：BRAND/SERIES/PLATFORM 均以 LOCAL 为权威来源
INSERT INTO `mdm_authoritative_source_config` (`entity_type`, `code_pattern`, `authoritative_source`, `conflict_policy`, `priority`, `enabled`, `create_by`, `modify_by`)
VALUES
    ('BRAND',    '*', 'LOCAL', 'REJECT', 999, 1, 'SYSTEM', 'SYSTEM'),
    ('SERIES',   '*', 'LOCAL', 'REJECT', 999, 1, 'SYSTEM', 'SYSTEM'),
    ('PLATFORM', '*', 'LOCAL', 'REJECT', 999, 1, 'SYSTEM', 'SYSTEM');

-- ============================================================
-- 4. 摄取日志表
-- ============================================================

CREATE TABLE IF NOT EXISTS `mdm_ingestion_log` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `message_id`      VARCHAR(128)  NOT NULL COMMENT '消息唯一标识，用于幂等去重',
    `source_system`   VARCHAR(32)   NOT NULL COMMENT '来源系统标识',
    `source_id`       VARCHAR(128)           DEFAULT NULL COMMENT '来源系统中的原始ID',
    `source_version`  VARCHAR(64)            DEFAULT NULL COMMENT '来源数据版本号',
    `entity_type`     VARCHAR(32)   NOT NULL COMMENT '实体类型：BRAND/SERIES/PLATFORM',
    `entity_code`     VARCHAR(64)            DEFAULT NULL COMMENT '实体业务编码',
    `ingestion_channel` VARCHAR(16)  NOT NULL COMMENT '摄取通道：API/KAFKA/MQ',
    `received_at`     DATETIME      NOT NULL COMMENT '消息接收时间',
    `processed_at`    DATETIME               DEFAULT NULL COMMENT '处理完成时间',
    `status`          VARCHAR(16)   NOT NULL DEFAULT 'RECEIVED' COMMENT '状态：RECEIVED/PROCESSING/SUCCESS/FAILED/SKIPPED',
    `error_code`      VARCHAR(32)            DEFAULT NULL COMMENT '错误码',
    `error_message`   VARCHAR(512)           DEFAULT NULL COMMENT '错误信息',
    `payload_hash`    VARCHAR(64)            DEFAULT NULL COMMENT '数据体哈希',
    `create_by`       VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`       VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`     INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`       TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_message_id` (`message_id`),
    KEY `idx_source` (`source_system`, `source_id`),
    KEY `idx_status_received` (`status`, `received_at`),
    KEY `idx_entity` (`entity_type`, `entity_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='摄取日志表';
