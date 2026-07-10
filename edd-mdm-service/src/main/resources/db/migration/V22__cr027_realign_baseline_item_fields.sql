-- V22: CR-027 修正软件基线项字段，对齐需求 CR 与设计 CR
-- mdm_material_software_baseline_item 表字段按 CR 定义修正：
--   移除: part_name, part_version, part_spec, part_drawing_no, sort_order
--   新增: vehicle_node_code, remark

ALTER TABLE `mdm_material_software_baseline_item`
    DROP COLUMN `part_name`,
    DROP COLUMN `part_version`,
    DROP COLUMN `part_spec`,
    DROP COLUMN `part_drawing_no`,
    DROP COLUMN `sort_order`,
    ADD COLUMN `vehicle_node_code` VARCHAR(64) DEFAULT NULL COMMENT '冗余承载节点（取自 Part.vehicle_node_code，便于 OTA 圈选，不做二次强校验）' AFTER `part_code`,
    ADD COLUMN `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注' AFTER `vehicle_node_code`;
