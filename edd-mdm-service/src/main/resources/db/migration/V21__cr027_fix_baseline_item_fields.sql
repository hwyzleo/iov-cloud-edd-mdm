-- V21: CR-027 修正软件基线项字段，对齐 SPEC 设计文档
-- mdm_material_software_baseline_item 表字段按 SPEC 定义修正：
--   移除: part_software_version, vehicle_node_code, fota_upgradeable, quantity, position, remark, effective_from, effective_to
--   新增: part_name, part_version, part_spec, part_drawing_no, sort_order

ALTER TABLE `mdm_material_software_baseline_item`
    DROP COLUMN `part_software_version`,
    DROP COLUMN `vehicle_node_code`,
    DROP COLUMN `fota_upgradeable`,
    DROP COLUMN `quantity`,
    DROP COLUMN `position`,
    DROP COLUMN `remark`,
    DROP COLUMN `effective_from`,
    DROP COLUMN `effective_to`,
    ADD COLUMN `part_name` VARCHAR(128) DEFAULT NULL COMMENT '冗余字段-零件名称（快照时随基线一并返回）' AFTER `part_code`,
    ADD COLUMN `part_version` VARCHAR(64) DEFAULT NULL COMMENT '冗余字段-零件版本（快照时随基线一并返回）' AFTER `part_name`,
    ADD COLUMN `part_spec` VARCHAR(512) DEFAULT NULL COMMENT '冗余字段-零件规格（快照时随基线一并返回）' AFTER `part_version`,
    ADD COLUMN `part_drawing_no` VARCHAR(64) DEFAULT NULL COMMENT '冗余字段-零件图纸编号（快照时随基线一并返回）' AFTER `part_spec`,
    ADD COLUMN `sort_order` INT DEFAULT NULL COMMENT '展示排序' AFTER `part_drawing_no`;
