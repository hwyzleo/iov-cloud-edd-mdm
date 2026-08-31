-- ============================================================
-- V28: CR-035 Option Family 治理 - 名称防重查询索引
-- 不新增业务表、不新增业务字段、不建立名称唯一索引（避免存量脏数据导致 Flyway 失败）；
-- 硬拒绝由应用层（OptionFamilyNamePolicy）完成，索引仅用于防重查询性能。
-- ============================================================

-- 英文名防重查询索引
CREATE INDEX `IDX_OF_NAME_STATUS` ON `mdm_option_family` (`name`, `status`, `row_valid`);

-- 中文名防重查询索引
CREATE INDEX `IDX_OF_NAME_LOCAL_STATUS` ON `mdm_option_family` (`name_local`, `status`, `row_valid`);
