-- V31_rollback__cr038_org_plant_restore_name_en.sql
-- 回滚 CR-038：由 name / name_local 重建旧字段语义
-- 旧语义：name=工厂正式名称（本地化），name_en=英文名称
-- 说明：对原 name_en 为空的记录，回滚后 name_en 会取到 name_local 值（治理边界），回滚前建议保留迁移审计快照

-- 1. 主表与历史表新增可空列 name_en
ALTER TABLE `mdm_org_plant` ADD COLUMN `name_en` VARCHAR(128) DEFAULT NULL COMMENT '英文名称' AFTER `name`;
ALTER TABLE `mdm_org_plant_history` ADD COLUMN `name_en` VARCHAR(128) DEFAULT NULL COMMENT '英文名称' AFTER `name`;

-- 2. 重建旧语义：name_en = 新 name（英文标准名称），name = name_local（本地化名称）
UPDATE `mdm_org_plant` SET `name_en` = `name`;
UPDATE `mdm_org_plant` SET `name` = `name_local`;
UPDATE `mdm_org_plant_history` SET `name_en` = `name`;
UPDATE `mdm_org_plant_history` SET `name` = `name_local`;

-- 3. 校验后删除 name_local
ALTER TABLE `mdm_org_plant` DROP COLUMN `name_local`;
ALTER TABLE `mdm_org_plant_history` DROP COLUMN `name_local`;
