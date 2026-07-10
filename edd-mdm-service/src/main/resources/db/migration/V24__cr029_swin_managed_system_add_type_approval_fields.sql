-- CR029: SWIN受管系统补充型式批准相关字段
-- US-043 型式批准影响评估 / US-045 SWIN投影 需要 is_type_approval_relevant 和 approved_software_baseline

ALTER TABLE `mdm_eead_swin_managed_system`
    ADD COLUMN `is_type_approval_relevant` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否与型式批准相关：1-是，0-否' AFTER `vehicle_node_code`,
    ADD COLUMN `approved_software_baseline` VARCHAR(128) NULL DEFAULT NULL COMMENT '已批准的软件基线代码' AFTER `is_type_approval_relevant`;
