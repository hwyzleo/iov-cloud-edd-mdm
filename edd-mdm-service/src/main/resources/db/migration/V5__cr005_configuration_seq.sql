-- V5: CR-005 Configuration code 自动生成
-- 1) 新增 mdm_configuration_seq 序列号表，按 variant_code 维度维护 7 位零填充自增序号
--    - 在创建 Configuration 的同一本地事务内自增 next_seq，事务回滚时序号一并回滚，避免跳号
--    - next_seq 单调递增，不因 Configuration 物理删除（DRAFT 状态）而回退
--    - 当 next_seq > 9,999,999 时由应用层拒绝继续分配并返回错误码 807014
-- 2) Configuration code 字段长度保持 VARCHAR(64) 不变（Variant code 上限 57 + 7 位序号 = 64）

-- ============================================================
-- Configuration code 自增序列表
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_configuration_seq` (
    `variant_code` VARCHAR(64) NOT NULL COMMENT '版本code（逻辑引用 mdm_variant.code），按此维度独立计数',
    `next_seq`     BIGINT      NOT NULL DEFAULT 0 COMMENT '下一个待分配序号；分配时执行 UPDATE SET next_seq=next_seq+1，使用更新后的值拼接 7 位零填充 code 后缀',
    `create_by`    VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '创建人',
    `create_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`    VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '修改人',
    `modify_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version`  INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`    TINYINT     NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`variant_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='Configuration code 自增序列表（CR-005）';
