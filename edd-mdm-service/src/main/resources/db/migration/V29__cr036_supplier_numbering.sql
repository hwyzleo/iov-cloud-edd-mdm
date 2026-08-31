-- ============================================================
-- V29: CR-036 Supplier.code 系统发号 - 全局流水序列表
-- 落地 MDM-REQ-CR-036：Supplier.code 改为 SUP + 8 位全局流水系统发号
-- 1) 新增 mdm_supplier_seq 序列表，维护全局单一计数器（固定行 SUPPLIER_GLOBAL）
--    - 发号与 Supplier 创建在同一本地事务内，SELECT ... FOR UPDATE 行锁分配
--    - next_seq 单调递增，已提交 code 不回收，DRAFT 物理删除不回退
--    - 超过 99,999,999 由应用层拒绝并返回 812703
-- 2) 初始化：取符合 ^SUP[0-9]{8}$ 的存量 Supplier.code 最大数字段 + 1；无符合记录时 next_seq=1
-- 3) 不更新任何存量 Supplier.code，不修改 mdm_supplier / mdm_supplier_history 表结构
-- ============================================================

CREATE TABLE IF NOT EXISTS `mdm_supplier_seq` (
    `seq_name`    VARCHAR(32) NOT NULL COMMENT '序列名称，固定SUPPLIER_GLOBAL',
    `next_seq`    BIGINT      NOT NULL DEFAULT 1 COMMENT '下一可分配流水号；分配时行锁读取并 +1',
    `create_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '修改人',
    `modify_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `row_version` INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `row_valid`   TINYINT     NOT NULL DEFAULT 1 COMMENT '行有效标记：1-有效，0-无效',
    PRIMARY KEY (`seq_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Supplier 全局流水序列表（CR-036，全局单一计数器）';

-- 初始化全局序列：取符合 SUP+8 位格式的存量 code 最大数字段 + 1；无符合记录时 next_seq=1
INSERT INTO `mdm_supplier_seq`
    (`seq_name`, `next_seq`, `create_by`, `create_time`, `modify_by`, `modify_time`, `row_version`, `row_valid`)
SELECT 'SUPPLIER_GLOBAL',
       IFNULL(MAX(CAST(SUBSTRING(code, 4) AS UNSIGNED)) + 1, 1),
       'system', NOW(), 'system', NOW(), 0, 1
FROM `mdm_supplier`
WHERE row_valid = 1
  AND code REGEXP '^SUP[0-9]{8}$';
