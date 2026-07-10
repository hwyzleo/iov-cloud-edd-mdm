-- CR-028: EEAD RXSWIN Registry
-- RXSWIN 登记表 + 专用序列表

-- RXSWIN 登记表
CREATE TABLE IF NOT EXISTS `mdm_eead_rxswin_registry` (
    `id`                     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '技术主键',
    `manifest_code`          VARCHAR(64)  NOT NULL COMMENT 'OTA manifest 业务键；全局唯一、幂等键、不可变',
    `manifest_digest`        VARCHAR(128) NOT NULL COMMENT '冻结 manifest 摘要，建议 sha256: 前缀；用于冲突检测',
    `swin_code`              VARCHAR(64)  NOT NULL COMMENT '松引用 mdm_eead_swin_definition.swin_code，须 ACTIVE',
    `rxswin_value`           VARCHAR(128) NOT NULL COMMENT 'MDM 生成的 RXSWIN，全球唯一、不可变',
    `software_baseline_code` VARCHAR(64)  NULL     COMMENT '松引用 MDM SoftwareBaseline.code，不建强外键',
    `status`                 VARCHAR(16)  NOT NULL DEFAULT 'REGISTERED' COMMENT '固定 REGISTERED；后续若扩展生命周期须另建 CR',
    `approved_at`            DATETIME     NULL     COMMENT 'OTA 传入的型批/批准时间',
    `registered_at`          DATETIME     NOT NULL COMMENT 'MDM 登记时间',
    `request_source`         VARCHAR(64)  NOT NULL COMMENT '调用方标识',
    `trace_id`               VARCHAR(128) NULL     COMMENT '链路追踪 ID',
    `create_by`              VARCHAR(64)  NOT NULL DEFAULT 'system' COMMENT '创建人',
    `create_time`            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `row_version`            INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`              TINYINT      NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_RXSWIN_MANIFEST_CODE` (`manifest_code`),
    UNIQUE KEY `UK_RXSWIN_VALUE` (`rxswin_value`),
    INDEX `IDX_RXSWIN_SWIN_CODE` (`swin_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='RXSWIN 登记表（CR-028，append-only 不可变登记记录）';

-- RXSWIN 专用序列表
CREATE TABLE IF NOT EXISTS `mdm_eead_rxswin_seq` (
    `seq_name`    VARCHAR(32) NOT NULL COMMENT '序列名称，固定 RXSWIN',
    `next_seq`    BIGINT      NOT NULL DEFAULT 0 COMMENT '全局序列当前值；分配时执行 UPDATE SET next_seq=next_seq+1',
    `create_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '修改人',
    `modify_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version` INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`   TINYINT     NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`seq_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='RXSWIN 序列表（CR-028，全局单一计数器）';

-- 初始化序列
INSERT INTO `mdm_eead_rxswin_seq` (`seq_name`, `next_seq`, `create_by`, `create_time`, `modify_by`, `modify_time`, `row_version`, `row_valid`)
VALUES ('RXSWIN', 0, 'system', NOW(), 'system', NOW(), 0, 1);
