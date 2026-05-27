-- V4: CR-004 产品树底层主数据
-- 新增 5 类主数据：Model（车型）/ Variant（版本）/ Configuration（配置）/ OptionFamily（选项族）/ OptionCode（选项码）
-- 新增 2 类绑定关系：Variant-OptionCode / Configuration-OptionCode
-- 新增对应历史快照表

-- ============================================================
-- 1. 车型表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_model` (
    `id`                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`                VARCHAR(64)   NOT NULL COMMENT '业务主键',
    `name`                VARCHAR(128)  NOT NULL COMMENT '官方名称',
    `name_local`          VARCHAR(128)           DEFAULT NULL COMMENT '本地化名称',
    `car_line_code`       VARCHAR(64)   NOT NULL COMMENT '车系code（逻辑引用 mdm_car_line.code）',
    `platform_code`       VARCHAR(64)   NOT NULL COMMENT '平台code（逻辑引用 mdm_platform.code）',
    `model_year`          VARCHAR(8)             DEFAULT NULL COMMENT '年款（如 2024）',
    `description`         VARCHAR(512)           DEFAULT NULL COMMENT '车型描述',
    `source_system`       VARCHAR(32)   NOT NULL DEFAULT 'LOCAL' COMMENT '来源系统：LOCAL/UPSTREAM_xxx',
    `source_id`           VARCHAR(128)           DEFAULT NULL COMMENT '来源系统中的原始ID',
    `source_version`      VARCHAR(64)            DEFAULT NULL COMMENT '来源系统版本号',
    `ingestion_channel`   VARCHAR(16)   NOT NULL DEFAULT 'LOCAL' COMMENT '接入通道：LOCAL/KAFKA/FEIGN',
    `ingestion_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '接入时间',
    `source_payload_hash` VARCHAR(64)            DEFAULT NULL COMMENT '来源消息体哈希',
    `version`             INT           NOT NULL DEFAULT 1 COMMENT '业务版本号',
    `effective_from`      DATETIME               DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`        DATETIME               DEFAULT NULL COMMENT '生效结束时间',
    `status`              VARCHAR(16)   NOT NULL DEFAULT 'DRAFT' COMMENT '状态：ACTIVE/INACTIVE/DRAFT',
    `create_by`           VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_car_line_code` (`car_line_code`),
    KEY `idx_platform_code` (`platform_code`),
    KEY `idx_source` (`source_system`, `source_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='车型表';

-- ============================================================
-- 2. 版本表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_variant` (
    `id`                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`                VARCHAR(64)   NOT NULL COMMENT '业务主键',
    `name`                VARCHAR(128)  NOT NULL COMMENT '官方名称',
    `name_local`          VARCHAR(128)           DEFAULT NULL COMMENT '本地化名称',
    `model_code`          VARCHAR(64)   NOT NULL COMMENT '车型code（逻辑引用 mdm_model.code）',
    `description`         VARCHAR(512)           DEFAULT NULL COMMENT '版本描述',
    `source_system`       VARCHAR(32)   NOT NULL DEFAULT 'LOCAL' COMMENT '来源系统',
    `source_id`           VARCHAR(128)           DEFAULT NULL COMMENT '来源系统中的原始ID',
    `source_version`      VARCHAR(64)            DEFAULT NULL COMMENT '来源系统版本号',
    `ingestion_channel`   VARCHAR(16)   NOT NULL DEFAULT 'LOCAL' COMMENT '接入通道',
    `ingestion_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '接入时间',
    `source_payload_hash` VARCHAR(64)            DEFAULT NULL COMMENT '来源消息体哈希',
    `version`             INT           NOT NULL DEFAULT 1 COMMENT '业务版本号',
    `effective_from`      DATETIME               DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`        DATETIME               DEFAULT NULL COMMENT '生效结束时间',
    `status`              VARCHAR(16)   NOT NULL DEFAULT 'DRAFT' COMMENT '状态：ACTIVE/INACTIVE/DRAFT',
    `create_by`           VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_model_code` (`model_code`),
    KEY `idx_source` (`source_system`, `source_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='版本表';

-- ============================================================
-- 3. 配置表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_configuration` (
    `id`                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`                VARCHAR(64)   NOT NULL COMMENT '业务主键',
    `name`                VARCHAR(128)  NOT NULL COMMENT '配置名称',
    `name_local`          VARCHAR(128)           DEFAULT NULL COMMENT '本地化名称',
    `variant_code`        VARCHAR(64)   NOT NULL COMMENT '版本code（逻辑引用 mdm_variant.code）',
    `description`         VARCHAR(512)           DEFAULT NULL COMMENT '配置描述',
    `source_system`       VARCHAR(32)   NOT NULL DEFAULT 'LOCAL' COMMENT '来源系统',
    `source_id`           VARCHAR(128)           DEFAULT NULL COMMENT '来源系统中的原始ID',
    `source_version`      VARCHAR(64)            DEFAULT NULL COMMENT '来源系统版本号',
    `ingestion_channel`   VARCHAR(16)   NOT NULL DEFAULT 'LOCAL' COMMENT '接入通道',
    `ingestion_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '接入时间',
    `source_payload_hash` VARCHAR(64)            DEFAULT NULL COMMENT '来源消息体哈希',
    `version`             INT           NOT NULL DEFAULT 1 COMMENT '业务版本号',
    `effective_from`      DATETIME               DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`        DATETIME               DEFAULT NULL COMMENT '生效结束时间',
    `status`              VARCHAR(16)   NOT NULL DEFAULT 'DRAFT' COMMENT '状态：ACTIVE/INACTIVE/DRAFT',
    `create_by`           VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_variant_code` (`variant_code`),
    KEY `idx_source` (`source_system`, `source_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='配置表';

-- ============================================================
-- 4. 选项族表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_option_family` (
    `id`                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`                VARCHAR(64)   NOT NULL COMMENT '业务主键',
    `name`                VARCHAR(128)  NOT NULL COMMENT '选项族名称（如外观颜色、内饰材质）',
    `name_local`          VARCHAR(128)           DEFAULT NULL COMMENT '本地化名称',
    `description`         VARCHAR(512)           DEFAULT NULL COMMENT '选项族描述',
    `source_system`       VARCHAR(32)   NOT NULL DEFAULT 'LOCAL' COMMENT '来源系统',
    `source_id`           VARCHAR(128)           DEFAULT NULL COMMENT '来源系统中的原始ID',
    `source_version`      VARCHAR(64)            DEFAULT NULL COMMENT '来源系统版本号',
    `ingestion_channel`   VARCHAR(16)   NOT NULL DEFAULT 'LOCAL' COMMENT '接入通道',
    `ingestion_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '接入时间',
    `source_payload_hash` VARCHAR(64)            DEFAULT NULL COMMENT '来源消息体哈希',
    `version`             INT           NOT NULL DEFAULT 1 COMMENT '业务版本号',
    `effective_from`      DATETIME               DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`        DATETIME               DEFAULT NULL COMMENT '生效结束时间',
    `status`              VARCHAR(16)   NOT NULL DEFAULT 'DRAFT' COMMENT '状态：ACTIVE/INACTIVE/DRAFT',
    `create_by`           VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_source` (`source_system`, `source_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='选项族表';

-- ============================================================
-- 5. 选项码表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_option_code` (
    `id`                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`                VARCHAR(64)   NOT NULL COMMENT '业务主键',
    `name`                VARCHAR(128)  NOT NULL COMMENT '选项码名称（如珍珠白、Nappa真皮）',
    `name_local`          VARCHAR(128)           DEFAULT NULL COMMENT '本地化名称',
    `option_family_code`  VARCHAR(64)   NOT NULL COMMENT '选项族code（逻辑引用 mdm_option_family.code）',
    `description`         VARCHAR(512)           DEFAULT NULL COMMENT '选项码描述',
    `source_system`       VARCHAR(32)   NOT NULL DEFAULT 'LOCAL' COMMENT '来源系统',
    `source_id`           VARCHAR(128)           DEFAULT NULL COMMENT '来源系统中的原始ID',
    `source_version`      VARCHAR(64)            DEFAULT NULL COMMENT '来源系统版本号',
    `ingestion_channel`   VARCHAR(16)   NOT NULL DEFAULT 'LOCAL' COMMENT '接入通道',
    `ingestion_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '接入时间',
    `source_payload_hash` VARCHAR(64)            DEFAULT NULL COMMENT '来源消息体哈希',
    `version`             INT           NOT NULL DEFAULT 1 COMMENT '业务版本号',
    `effective_from`      DATETIME               DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`        DATETIME               DEFAULT NULL COMMENT '生效结束时间',
    `status`              VARCHAR(16)   NOT NULL DEFAULT 'DRAFT' COMMENT '状态：ACTIVE/INACTIVE/DRAFT',
    `create_by`           VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_option_family_code` (`option_family_code`),
    KEY `idx_source` (`source_system`, `source_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='选项码表';

-- ============================================================
-- 6. 版本-选项码绑定关系表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_variant_option_code_binding` (
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `variant_code`       VARCHAR(64)  NOT NULL COMMENT '版本code（逻辑引用 mdm_variant.code）',
    `option_code_code`   VARCHAR(64)  NOT NULL COMMENT '选项码code（逻辑引用 mdm_option_code.code）',
    `option_family_code` VARCHAR(64)  NOT NULL COMMENT '选项族code（冗余，用于互斥校验）',
    `create_by`          VARCHAR(64)  NOT NULL COMMENT '创建人',
    `create_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`          VARCHAR(64)  NOT NULL COMMENT '修改人',
    `modify_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`        INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`          TINYINT      NOT NULL DEFAULT 1 COMMENT '行有效标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_variant_option_code` (`variant_code`, `option_code_code`),
    UNIQUE KEY `uk_variant_option_family` (`variant_code`, `option_family_code`),
    KEY `idx_option_code` (`option_code_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='版本-选项码绑定关系表（同一版本下同一选项族最多绑定一个选项码）';

-- ============================================================
-- 7. 配置-选项码绑定关系表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_configuration_option_code_binding` (
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `configuration_code` VARCHAR(64)  NOT NULL COMMENT '配置code（逻辑引用 mdm_configuration.code）',
    `option_code_code`   VARCHAR(64)  NOT NULL COMMENT '选项码code（逻辑引用 mdm_option_code.code）',
    `option_family_code` VARCHAR(64)  NOT NULL COMMENT '选项族code（冗余，用于互斥校验）',
    `create_by`          VARCHAR(64)  NOT NULL COMMENT '创建人',
    `create_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`          VARCHAR(64)  NOT NULL COMMENT '修改人',
    `modify_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`        INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`          TINYINT      NOT NULL DEFAULT 1 COMMENT '行有效标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_configuration_option_code` (`configuration_code`, `option_code_code`),
    UNIQUE KEY `uk_configuration_option_family` (`configuration_code`, `option_family_code`),
    KEY `idx_option_code` (`option_code_code`),
    KEY `idx_configuration` (`configuration_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='配置-选项码绑定关系表（同一配置下同一选项族最多绑定一个选项码，支持按选项码组合反查配置）';

-- ============================================================
-- 8. 车型历史快照表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_model_history` (
    `snapshot_id`         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '快照主键',
    `entity_id`           BIGINT        NOT NULL COMMENT '关联主表id',
    `code`                VARCHAR(64)   NOT NULL COMMENT '业务主键',
    `name`                VARCHAR(128)  NOT NULL COMMENT '官方名称',
    `name_local`          VARCHAR(128)           DEFAULT NULL COMMENT '本地化名称',
    `car_line_code`       VARCHAR(64)   NOT NULL COMMENT '车系code',
    `platform_code`       VARCHAR(64)   NOT NULL COMMENT '平台code',
    `model_year`          VARCHAR(8)             DEFAULT NULL COMMENT '年款',
    `description`         VARCHAR(512)           DEFAULT NULL COMMENT '车型描述',
    `source_system`       VARCHAR(32)   NOT NULL COMMENT '来源系统',
    `source_id`           VARCHAR(128)           DEFAULT NULL COMMENT '来源系统中的原始ID',
    `source_version`      VARCHAR(64)            DEFAULT NULL COMMENT '来源系统版本号',
    `ingestion_channel`   VARCHAR(16)   NOT NULL COMMENT '接入通道',
    `ingestion_time`      DATETIME      NOT NULL COMMENT '接入时间',
    `source_payload_hash` VARCHAR(64)            DEFAULT NULL COMMENT '来源消息体哈希',
    `version`             INT           NOT NULL COMMENT '业务版本号',
    `effective_from`      DATETIME               DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`        DATETIME               DEFAULT NULL COMMENT '生效结束时间',
    `status`              VARCHAR(16)   NOT NULL COMMENT '状态',
    `operation_type`      VARCHAR(16)   NOT NULL COMMENT '操作类型：CREATE/UPDATE/DEACTIVATE/DELETE',
    `snapshot_time`       DATETIME      NOT NULL COMMENT '快照时间',
    `operator`            VARCHAR(64)   NOT NULL COMMENT '操作人',
    `create_by`           VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记',
    PRIMARY KEY (`snapshot_id`),
    KEY `idx_entity_id` (`entity_id`),
    KEY `idx_code` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='车型历史快照表';

-- ============================================================
-- 9. 版本历史快照表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_variant_history` (
    `snapshot_id`         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '快照主键',
    `entity_id`           BIGINT        NOT NULL COMMENT '关联主表id',
    `code`                VARCHAR(64)   NOT NULL COMMENT '业务主键',
    `name`                VARCHAR(128)  NOT NULL COMMENT '官方名称',
    `name_local`          VARCHAR(128)           DEFAULT NULL COMMENT '本地化名称',
    `model_code`          VARCHAR(64)   NOT NULL COMMENT '车型code',
    `description`         VARCHAR(512)           DEFAULT NULL COMMENT '版本描述',
    `source_system`       VARCHAR(32)   NOT NULL COMMENT '来源系统',
    `source_id`           VARCHAR(128)           DEFAULT NULL COMMENT '来源系统中的原始ID',
    `source_version`      VARCHAR(64)            DEFAULT NULL COMMENT '来源系统版本号',
    `ingestion_channel`   VARCHAR(16)   NOT NULL COMMENT '接入通道',
    `ingestion_time`      DATETIME      NOT NULL COMMENT '接入时间',
    `source_payload_hash` VARCHAR(64)            DEFAULT NULL COMMENT '来源消息体哈希',
    `version`             INT           NOT NULL COMMENT '业务版本号',
    `effective_from`      DATETIME               DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`        DATETIME               DEFAULT NULL COMMENT '生效结束时间',
    `status`              VARCHAR(16)   NOT NULL COMMENT '状态',
    `operation_type`      VARCHAR(16)   NOT NULL COMMENT '操作类型',
    `snapshot_time`       DATETIME      NOT NULL COMMENT '快照时间',
    `operator`            VARCHAR(64)   NOT NULL COMMENT '操作人',
    `create_by`           VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记',
    PRIMARY KEY (`snapshot_id`),
    KEY `idx_entity_id` (`entity_id`),
    KEY `idx_code` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='版本历史快照表';

-- ============================================================
-- 10. 配置历史快照表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_configuration_history` (
    `snapshot_id`         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '快照主键',
    `entity_id`           BIGINT        NOT NULL COMMENT '关联主表id',
    `code`                VARCHAR(64)   NOT NULL COMMENT '业务主键',
    `name`                VARCHAR(128)  NOT NULL COMMENT '配置名称',
    `name_local`          VARCHAR(128)           DEFAULT NULL COMMENT '本地化名称',
    `variant_code`        VARCHAR(64)   NOT NULL COMMENT '版本code',
    `description`         VARCHAR(512)           DEFAULT NULL COMMENT '配置描述',
    `source_system`       VARCHAR(32)   NOT NULL COMMENT '来源系统',
    `source_id`           VARCHAR(128)           DEFAULT NULL COMMENT '来源系统中的原始ID',
    `source_version`      VARCHAR(64)            DEFAULT NULL COMMENT '来源系统版本号',
    `ingestion_channel`   VARCHAR(16)   NOT NULL COMMENT '接入通道',
    `ingestion_time`      DATETIME      NOT NULL COMMENT '接入时间',
    `source_payload_hash` VARCHAR(64)            DEFAULT NULL COMMENT '来源消息体哈希',
    `version`             INT           NOT NULL COMMENT '业务版本号',
    `effective_from`      DATETIME               DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`        DATETIME               DEFAULT NULL COMMENT '生效结束时间',
    `status`              VARCHAR(16)   NOT NULL COMMENT '状态',
    `operation_type`      VARCHAR(16)   NOT NULL COMMENT '操作类型',
    `snapshot_time`       DATETIME      NOT NULL COMMENT '快照时间',
    `operator`            VARCHAR(64)   NOT NULL COMMENT '操作人',
    `create_by`           VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记',
    PRIMARY KEY (`snapshot_id`),
    KEY `idx_entity_id` (`entity_id`),
    KEY `idx_code` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='配置历史快照表';

-- ============================================================
-- 11. 选项族历史快照表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_option_family_history` (
    `snapshot_id`         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '快照主键',
    `entity_id`           BIGINT        NOT NULL COMMENT '关联主表id',
    `code`                VARCHAR(64)   NOT NULL COMMENT '业务主键',
    `name`                VARCHAR(128)  NOT NULL COMMENT '选项族名称',
    `name_local`          VARCHAR(128)           DEFAULT NULL COMMENT '本地化名称',
    `description`         VARCHAR(512)           DEFAULT NULL COMMENT '选项族描述',
    `source_system`       VARCHAR(32)   NOT NULL COMMENT '来源系统',
    `source_id`           VARCHAR(128)           DEFAULT NULL COMMENT '来源系统中的原始ID',
    `source_version`      VARCHAR(64)            DEFAULT NULL COMMENT '来源系统版本号',
    `ingestion_channel`   VARCHAR(16)   NOT NULL COMMENT '接入通道',
    `ingestion_time`      DATETIME      NOT NULL COMMENT '接入时间',
    `source_payload_hash` VARCHAR(64)            DEFAULT NULL COMMENT '来源消息体哈希',
    `version`             INT           NOT NULL COMMENT '业务版本号',
    `effective_from`      DATETIME               DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`        DATETIME               DEFAULT NULL COMMENT '生效结束时间',
    `status`              VARCHAR(16)   NOT NULL COMMENT '状态',
    `operation_type`      VARCHAR(16)   NOT NULL COMMENT '操作类型',
    `snapshot_time`       DATETIME      NOT NULL COMMENT '快照时间',
    `operator`            VARCHAR(64)   NOT NULL COMMENT '操作人',
    `create_by`           VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记',
    PRIMARY KEY (`snapshot_id`),
    KEY `idx_entity_id` (`entity_id`),
    KEY `idx_code` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='选项族历史快照表';

-- ============================================================
-- 12. 选项码历史快照表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_option_code_history` (
    `snapshot_id`         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '快照主键',
    `entity_id`           BIGINT        NOT NULL COMMENT '关联主表id',
    `code`                VARCHAR(64)   NOT NULL COMMENT '业务主键',
    `name`                VARCHAR(128)  NOT NULL COMMENT '选项码名称',
    `name_local`          VARCHAR(128)           DEFAULT NULL COMMENT '本地化名称',
    `option_family_code`  VARCHAR(64)   NOT NULL COMMENT '选项族code',
    `description`         VARCHAR(512)           DEFAULT NULL COMMENT '选项码描述',
    `source_system`       VARCHAR(32)   NOT NULL COMMENT '来源系统',
    `source_id`           VARCHAR(128)           DEFAULT NULL COMMENT '来源系统中的原始ID',
    `source_version`      VARCHAR(64)            DEFAULT NULL COMMENT '来源系统版本号',
    `ingestion_channel`   VARCHAR(16)   NOT NULL COMMENT '接入通道',
    `ingestion_time`      DATETIME      NOT NULL COMMENT '接入时间',
    `source_payload_hash` VARCHAR(64)            DEFAULT NULL COMMENT '来源消息体哈希',
    `version`             INT           NOT NULL COMMENT '业务版本号',
    `effective_from`      DATETIME               DEFAULT NULL COMMENT '生效开始时间',
    `effective_to`        DATETIME               DEFAULT NULL COMMENT '生效结束时间',
    `status`              VARCHAR(16)   NOT NULL COMMENT '状态',
    `operation_type`      VARCHAR(16)   NOT NULL COMMENT '操作类型',
    `snapshot_time`       DATETIME      NOT NULL COMMENT '快照时间',
    `operator`            VARCHAR(64)   NOT NULL COMMENT '操作人',
    `create_by`           VARCHAR(64)   NOT NULL COMMENT '创建人',
    `create_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`           VARCHAR(64)   NOT NULL COMMENT '修改人',
    `modify_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`         INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`           TINYINT       NOT NULL DEFAULT 1 COMMENT '行有效标记',
    PRIMARY KEY (`snapshot_id`),
    KEY `idx_entity_id` (`entity_id`),
    KEY `idx_code` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='选项码历史快照表';
