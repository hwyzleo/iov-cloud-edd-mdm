-- ============================================================
-- V32: CR-039 MaterialCategory 三级标准目录治理 - 名称防重/层级查询索引
-- 不新增业务表、不新增业务字段、不建立名称唯一索引（避免存量脏数据导致 Flyway 失败）；
-- 硬拒绝由应用层（MaterialCategoryNamePolicy / HierarchyPolicy / LeafPolicy）完成，
-- 索引仅用于防重与叶子判定查询性能。
-- ============================================================

-- 英文名防重查询索引
CREATE INDEX `IDX_MC_NAME_STATUS` ON `mdm_material_category` (`name`, `status`, `row_valid`);

-- 中文名防重查询索引
CREATE INDEX `IDX_MC_NAME_LOCAL_STATUS` ON `mdm_material_category` (`name_local`, `status`, `row_valid`);

-- 叶子/层级查询索引（按父节点 + 状态 + 行有效性快速判定 ACTIVE 子节点）
CREATE INDEX `IDX_MC_PARENT_STATUS` ON `mdm_material_category` (`parent_code`, `status`, `row_valid`);
