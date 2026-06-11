-- V14: CR-023 Part 零件编码增强 - 新增字段
-- 1) mdm_material_part 主表 ALTER 新增 3 列：base_no / numbering_source / is_assembly
-- 2) mdm_material_part_history 历史表同步新增 3 列
-- 3) 新增索引 IDX_PART_BASE_NO、IDX_PART_NUMBERING_SOURCE

-- ============================================================
-- 主表新增字段
-- ============================================================
ALTER TABLE `mdm_material_part`
    ADD COLUMN `base_no` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '零件基础号=code去除末尾2位代次' AFTER `code`,
    ADD COLUMN `numbering_source` VARCHAR(16) NOT NULL DEFAULT 'MDM_GEN' COMMENT '发号来源(MDM_GEN/PLM/IMPORT)' AFTER `base_no`,
    ADD COLUMN `is_assembly` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否总成件' AFTER `is_software`;

-- 新增索引
ALTER TABLE `mdm_material_part`
    ADD INDEX `IDX_PART_BASE_NO` (`base_no`),
    ADD INDEX `IDX_PART_NUMBERING_SOURCE` (`numbering_source`);

-- ============================================================
-- 历史表同步新增字段
-- ============================================================
ALTER TABLE `mdm_material_part_history`
    ADD COLUMN `base_no` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '零件基础号' AFTER `code`,
    ADD COLUMN `numbering_source` VARCHAR(16) NOT NULL DEFAULT 'MDM_GEN' COMMENT '发号来源' AFTER `base_no`,
    ADD COLUMN `is_assembly` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否总成件' AFTER `is_software`;
