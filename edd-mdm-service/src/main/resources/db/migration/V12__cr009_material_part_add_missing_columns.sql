-- CR-009-Enh: 补充 mdm_material_part 和 mdm_material_part_history 缺失的 12 个业务属性字段
-- 这些字段在 CR-009-Enh 中定义，但 V11 迁移脚本未包含

ALTER TABLE `mdm_material_part`
    ADD COLUMN `is_key_part` VARCHAR(16) DEFAULT NULL COMMENT '关重特性枚举（KEY/MAJOR/SIMPLE）' AFTER `is_safety_critical`,
    ADD COLUMN `is_regulatory_part` TINYINT(1) DEFAULT NULL COMMENT '是否法规件' AFTER `is_key_part`,
    ADD COLUMN `is_frame_part` TINYINT(1) DEFAULT NULL COMMENT '是否架构件' AFTER `is_regulatory_part`,
    ADD COLUMN `is_accurately_traced` TINYINT(1) DEFAULT NULL COMMENT '是否精准追溯' AFTER `is_frame_part`,
    ADD COLUMN `ffa_code` VARCHAR(64) DEFAULT NULL COMMENT '功能配置特征码（FFA）' AFTER `is_accurately_traced`,
    ADD COLUMN `ffa_desc` VARCHAR(256) DEFAULT NULL COMMENT '功能配置特征描述' AFTER `ffa_code`,
    ADD COLUMN `is_digitate` TINYINT(1) DEFAULT NULL COMMENT '是否有数模（数字化三维模型）' AFTER `ffa_desc`,
    ADD COLUMN `initial_model` VARCHAR(64) DEFAULT NULL COMMENT '初始车型（零件首次应用的车型）' AFTER `is_digitate`,
    ADD COLUMN `production_code` VARCHAR(64) DEFAULT NULL COMMENT '对应生产件号' AFTER `weight_uom`,
    ADD COLUMN `first_production_date` DATE DEFAULT NULL COMMENT '首次投产时间' AFTER `substitute_part_code`,
    ADD COLUMN `designer` VARCHAR(64) DEFAULT NULL COMMENT '设计工程师' AFTER `first_production_date`,
    ADD COLUMN `designer_dept` VARCHAR(128) DEFAULT NULL COMMENT '设计工程师部门' AFTER `designer`,
    ADD INDEX `IDX_PART_KEY_LEVEL` (`is_key_part`, `status`);

ALTER TABLE `mdm_material_part_history`
    ADD COLUMN `is_key_part` VARCHAR(16) DEFAULT NULL COMMENT '关重特性枚举' AFTER `is_safety_critical`,
    ADD COLUMN `is_regulatory_part` TINYINT(1) DEFAULT NULL COMMENT '是否法规件' AFTER `is_key_part`,
    ADD COLUMN `is_frame_part` TINYINT(1) DEFAULT NULL COMMENT '是否架构件' AFTER `is_regulatory_part`,
    ADD COLUMN `is_accurately_traced` TINYINT(1) DEFAULT NULL COMMENT '是否精准追溯' AFTER `is_frame_part`,
    ADD COLUMN `ffa_code` VARCHAR(64) DEFAULT NULL COMMENT '功能配置特征码' AFTER `is_accurately_traced`,
    ADD COLUMN `ffa_desc` VARCHAR(256) DEFAULT NULL COMMENT '功能配置特征描述' AFTER `ffa_code`,
    ADD COLUMN `is_digitate` TINYINT(1) DEFAULT NULL COMMENT '是否有数模' AFTER `ffa_desc`,
    ADD COLUMN `initial_model` VARCHAR(64) DEFAULT NULL COMMENT '初始车型' AFTER `is_digitate`,
    ADD COLUMN `production_code` VARCHAR(64) DEFAULT NULL COMMENT '对应生产件号' AFTER `weight_uom`,
    ADD COLUMN `first_production_date` DATE DEFAULT NULL COMMENT '首次投产时间' AFTER `substitute_part_code`,
    ADD COLUMN `designer` VARCHAR(64) DEFAULT NULL COMMENT '设计工程师' AFTER `first_production_date`,
    ADD COLUMN `designer_dept` VARCHAR(128) DEFAULT NULL COMMENT '设计工程师部门' AFTER `designer`;
