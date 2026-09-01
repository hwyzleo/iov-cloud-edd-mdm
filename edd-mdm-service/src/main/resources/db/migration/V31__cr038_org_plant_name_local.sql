-- V31__cr038_org_plant_name_local.sql
-- CR-038: Plant 双语名称统一 name / name_local
-- name 固定为英文标准名称（NOT NULL），name_local 固定为本地化名称（可空）；废弃 name_en
-- 迁移采用「新增—回填—校验—删除」方式，主表与历史表同步执行
-- 语义交换风险：必须先保存原 name（回填 name_local）再覆盖 name 为英文标准名称

-- 1. 主表与历史表新增可空列 name_local
ALTER TABLE `mdm_org_plant` ADD COLUMN `name_local` VARCHAR(128) DEFAULT NULL COMMENT '本地化名称' AFTER `name`;
ALTER TABLE `mdm_org_plant_history` ADD COLUMN `name_local` VARCHAR(128) DEFAULT NULL COMMENT '本地化名称' AFTER `name`;

-- 2. 回填：name_local = name，保存原本地化名称
UPDATE `mdm_org_plant` SET `name_local` = `name` WHERE `name_local` IS NULL;
UPDATE `mdm_org_plant_history` SET `name_local` = `name` WHERE `name_local` IS NULL;

-- 3. 对 name_en 非空记录：name = name_en（英文标准名称）
UPDATE `mdm_org_plant` SET `name` = `name_en` WHERE `name_en` IS NOT NULL AND `name_en` <> '';
UPDATE `mdm_org_plant_history` SET `name` = `name_en` WHERE `name_en` IS NOT NULL AND `name_en` <> '';

-- 4. 校验提示：name_en 为空的记录保留原 name 作为占位名称，纳入英文名称待补齐治理清单（不阻断迁移）
-- SELECT `code`, `name`, `name_local` FROM `mdm_org_plant` WHERE `name_en` IS NULL OR `name_en` = '';

-- 5. 校验记录数、空值、长度及抽样语义后删除 name_en
ALTER TABLE `mdm_org_plant` DROP COLUMN `name_en`;
ALTER TABLE `mdm_org_plant_history` DROP COLUMN `name_en`;
