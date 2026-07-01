-- V19: SWIN编码方案表新增 structure_pattern 和 version_format 字段
-- 参考设计文档更新

-- ============================================================
-- 1. swin_scheme 主表新增字段
-- ============================================================
ALTER TABLE `mdm_eead_swin_scheme`
    ADD COLUMN `structure_pattern` VARCHAR(256) DEFAULT NULL COMMENT 'SWIN编码结构/格式模板或正则' AFTER `route`,
    ADD COLUMN `version_format`    VARCHAR(64)  DEFAULT NULL COMMENT '版本号格式说明' AFTER `structure_pattern`;

-- ============================================================
-- 2. swin_scheme_history 历史表新增字段
-- ============================================================
ALTER TABLE `mdm_eead_swin_scheme_history`
    ADD COLUMN `structure_pattern` VARCHAR(256) DEFAULT NULL COMMENT 'SWIN编码结构/格式模板或正则' AFTER `route`,
    ADD COLUMN `version_format`    VARCHAR(64)  DEFAULT NULL COMMENT '版本号格式说明' AFTER `structure_pattern`;
