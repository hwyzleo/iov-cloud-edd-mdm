-- V13: CR-023 Part 零件号自增序列表
-- 1) 新增 mdm_material_part_seq 序列表，维护全局单一计数器（单行 next_seq，不按 category / 前缀分段计数）
--    - 在创建 Part 的同一本地事务内自增 next_seq，事务回滚时序号一并回滚，避免跳号
--    - next_seq 单调递增，不因 Part 物理删除（DRAFT 状态）而回退
--    - 当 next_seq > 99,999,999 时由应用层拒绝继续分配并返回错误码 812919
-- 2) 对齐 mdm_configuration_seq 范式

-- ============================================================
-- Part 零件号自增序列表（全局单一计数器）
-- ============================================================
CREATE TABLE IF NOT EXISTS `mdm_material_part_seq` (
    `seq_name`    VARCHAR(32) NOT NULL COMMENT '序列名称，固定PART_GLOBAL',
    `next_seq`    BIGINT      NOT NULL DEFAULT 0 COMMENT '下一个待分配流水号；分配时执行 UPDATE SET next_seq=next_seq+1，使用更新后的值零填充至8位作为流水段',
    `create_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '修改人',
    `modify_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version` INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`   TINYINT     NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`seq_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='Part 零件号自增序列表（CR-023，全局单一计数器）';

-- 初始化全局序列（首次分配时若行不存在，按INSERT...ON DUPLICATE KEY UPDATE幂等初始化）
INSERT INTO `mdm_material_part_seq` (`seq_name`, `next_seq`, `create_by`, `create_time`, `modify_by`, `modify_time`, `row_version`, `row_valid`)
VALUES ('PART_GLOBAL', 0, 'system', NOW(), 'system', NOW(), 0, 1);
