-- V1: 初始化Product MDM数据库表结构
-- 品牌表
CREATE TABLE IF NOT EXISTS `mdm_brand` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`            VARCHAR(64)   NOT NULL COMMENT '业务主键',
    `name`            VARCHAR(128)  NOT NULL COMMENT '官方名称',
    `name_local`      VARCHAR(128)           DEFAULT NULL COMMENT '本地化名称',
    `description`     VARCHAR(512)           DEFAULT NULL COMMENT '品牌描述',
    `logo`            VARCHAR(256)           DEFAULT NULL COMMENT 'Logo URL',
    `country`         VARCHAR(64)            DEFAULT NULL COMMENT '国家',
    `founded_year`    INT                    DEFAULT NULL COMMENT '创立年份',
    `version`         INT           NOT NULL DEFAULT 1 COMMENT '业务版本号',
    `effective_from`  DATETIME               DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`    DATETIME               DEFAULT NULL COMMENT '生效结束时间',
    `status`          VARCHAR(16)   NOT NULL DEFAULT 'DRAFT' COMMENT '状态：ACTIVE/INACTIVE/DEPRECATED/DRAFT',
    `create_by`       VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`       VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`     INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`       TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='品牌表';

-- 车系表
CREATE TABLE IF NOT EXISTS `mdm_series` (
    `id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`             VARCHAR(64)   NOT NULL COMMENT '业务主键',
    `name`             VARCHAR(128)  NOT NULL COMMENT '官方名称',
    `name_local`       VARCHAR(128)           DEFAULT NULL COMMENT '本地化名称',
    `brand_code`       VARCHAR(64)   NOT NULL COMMENT '品牌code',
    `series_type`      VARCHAR(16)            DEFAULT NULL COMMENT '车系类型：SEDAN/SUV/MPV/PICKUP/COMMERCIAL',
    `lifecycle_status` VARCHAR(16)            DEFAULT NULL COMMENT '生命周期状态：IN_DEVELOPMENT/ON_SALE/DISCONTINUED',
    `target_market`    VARCHAR(16)            DEFAULT NULL COMMENT '目标市场：DOMESTIC/OVERSEAS/GLOBAL',
    `version`          INT           NOT NULL DEFAULT 1 COMMENT '业务版本号',
    `effective_from`   DATETIME               DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`     DATETIME               DEFAULT NULL COMMENT '生效结束时间',
    `status`           VARCHAR(16)   NOT NULL DEFAULT 'DRAFT' COMMENT '状态：ACTIVE/INACTIVE/DEPRECATED/DRAFT',
    `create_by`        VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`        VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`      INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`        TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_brand_code` (`brand_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车系表';

-- 平台表
CREATE TABLE IF NOT EXISTS `mdm_platform` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`            VARCHAR(64)   NOT NULL COMMENT '业务主键',
    `name`            VARCHAR(128)  NOT NULL COMMENT '官方名称',
    `name_local`      VARCHAR(128)           DEFAULT NULL COMMENT '本地化名称',
    `platform_type`   VARCHAR(16)            DEFAULT NULL COMMENT '平台类型：FUEL/BEV/PHEV/EREV',
    `architecture`    VARCHAR(64)            DEFAULT NULL COMMENT 'EE架构代号',
    `version`         INT           NOT NULL DEFAULT 1 COMMENT '业务版本号',
    `effective_from`  DATETIME               DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`    DATETIME               DEFAULT NULL COMMENT '生效结束时间',
    `status`          VARCHAR(16)   NOT NULL DEFAULT 'DRAFT' COMMENT '状态：ACTIVE/INACTIVE/DEPRECATED/DRAFT',
    `create_by`       VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`       VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`     INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`       TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台表';

-- 品牌历史快照表
CREATE TABLE IF NOT EXISTS `mdm_brand_history` (
    `snapshot_id`     BIGINT        NOT NULL AUTO_INCREMENT COMMENT '快照主键',
    `entity_id`       BIGINT        NOT NULL COMMENT '关联主表id',
    `code`            VARCHAR(64)   NOT NULL COMMENT '业务主键',
    `name`            VARCHAR(128)  NOT NULL COMMENT '官方名称',
    `name_local`      VARCHAR(128)           DEFAULT NULL COMMENT '本地化名称',
    `description`     VARCHAR(512)           DEFAULT NULL COMMENT '品牌描述',
    `logo`            VARCHAR(256)           DEFAULT NULL COMMENT 'Logo URL',
    `country`         VARCHAR(64)            DEFAULT NULL COMMENT '国家',
    `founded_year`    INT                    DEFAULT NULL COMMENT '创立年份',
    `version`         INT           NOT NULL COMMENT '业务版本号',
    `effective_from`  DATETIME               DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`    DATETIME               DEFAULT NULL COMMENT '生效结束时间',
    `status`          VARCHAR(16)   NOT NULL COMMENT '状态',
    `operation_type`  VARCHAR(16)   NOT NULL COMMENT '操作类型：CREATE/UPDATE/DEACTIVATE/DELETE',
    `snapshot_time`   DATETIME      NOT NULL COMMENT '快照时间',
    `operator`        VARCHAR(64)   NOT NULL COMMENT '操作人',
    `create_by`       VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`       VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`     INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`       TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`snapshot_id`),
    KEY `idx_entity_id` (`entity_id`),
    KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='品牌历史快照表';

-- 车系历史快照表
CREATE TABLE IF NOT EXISTS `mdm_series_history` (
    `snapshot_id`      BIGINT        NOT NULL AUTO_INCREMENT COMMENT '快照主键',
    `entity_id`        BIGINT        NOT NULL COMMENT '关联主表id',
    `code`             VARCHAR(64)   NOT NULL COMMENT '业务主键',
    `name`             VARCHAR(128)  NOT NULL COMMENT '官方名称',
    `name_local`       VARCHAR(128)           DEFAULT NULL COMMENT '本地化名称',
    `brand_code`       VARCHAR(64)   NOT NULL COMMENT '品牌code',
    `series_type`      VARCHAR(16)            DEFAULT NULL COMMENT '车系类型',
    `lifecycle_status` VARCHAR(16)            DEFAULT NULL COMMENT '生命周期状态',
    `target_market`    VARCHAR(16)            DEFAULT NULL COMMENT '目标市场',
    `version`          INT           NOT NULL COMMENT '业务版本号',
    `effective_from`   DATETIME               DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`     DATETIME               DEFAULT NULL COMMENT '生效结束时间',
    `status`           VARCHAR(16)   NOT NULL COMMENT '状态',
    `operation_type`   VARCHAR(16)   NOT NULL COMMENT '操作类型：CREATE/UPDATE/DEACTIVATE/DELETE',
    `snapshot_time`    DATETIME      NOT NULL COMMENT '快照时间',
    `operator`         VARCHAR(64)   NOT NULL COMMENT '操作人',
    `create_by`        VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`        VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`      INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`        TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`snapshot_id`),
    KEY `idx_entity_id` (`entity_id`),
    KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车系历史快照表';

-- 平台历史快照表
CREATE TABLE IF NOT EXISTS `mdm_platform_history` (
    `snapshot_id`    BIGINT        NOT NULL AUTO_INCREMENT COMMENT '快照主键',
    `entity_id`      BIGINT        NOT NULL COMMENT '关联主表id',
    `code`           VARCHAR(64)   NOT NULL COMMENT '业务主键',
    `name`           VARCHAR(128)  NOT NULL COMMENT '官方名称',
    `name_local`     VARCHAR(128)           DEFAULT NULL COMMENT '本地化名称',
    `platform_type`  VARCHAR(16)            DEFAULT NULL COMMENT '平台类型',
    `architecture`   VARCHAR(64)            DEFAULT NULL COMMENT 'EE架构代号',
    `version`        INT           NOT NULL COMMENT '业务版本号',
    `effective_from` DATETIME               DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`   DATETIME               DEFAULT NULL COMMENT '生效结束时间',
    `status`         VARCHAR(16)   NOT NULL COMMENT '状态',
    `operation_type` VARCHAR(16)   NOT NULL COMMENT '操作类型：CREATE/UPDATE/DEACTIVATE/DELETE',
    `snapshot_time`  DATETIME      NOT NULL COMMENT '快照时间',
    `operator`       VARCHAR(64)   NOT NULL COMMENT '操作人',
    `create_by`      VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`      VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`    INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`      TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`snapshot_id`),
    KEY `idx_entity_id` (`entity_id`),
    KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台历史快照表';

-- 事件发件箱表
CREATE TABLE IF NOT EXISTS `mdm_outbox` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `aggregate_type` VARCHAR(32)   NOT NULL COMMENT '聚合类型：BRAND/SERIES/PLATFORM',
    `aggregate_id`   VARCHAR(64)   NOT NULL COMMENT '聚合根ID（code）',
    `event_type`     VARCHAR(64)   NOT NULL COMMENT '事件类型',
    `payload`        TEXT          NOT NULL COMMENT 'JSON格式事件体',
    `occurred_at`    DATETIME      NOT NULL COMMENT '事件发生时间',
    `sent`           TINYINT       NOT NULL DEFAULT 0 COMMENT '是否已发送：0-未发送，1-已发送',
    `sent_at`        DATETIME               DEFAULT NULL COMMENT '发送时间',
    `retry_count`    INT           NOT NULL DEFAULT 0 COMMENT '重试次数',
    `create_by`      VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`      VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`    INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`      TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`id`),
    KEY `idx_outbox_sent_occurred` (`sent`, `occurred_at`),
    KEY `idx_outbox_aggregate` (`aggregate_type`, `aggregate_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件发件箱表';
