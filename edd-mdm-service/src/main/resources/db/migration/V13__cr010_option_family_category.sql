-- V13: CR-010 Option Family category 字段扩展
-- 为 mdm_option_family 表新增 category（商品分类）字段
-- 为 mdm_option_family_history 表同步新增 category 字段

-- ============================================================
-- 1. 选项族主表新增 category 列
-- ============================================================
ALTER TABLE `mdm_option_family`
    ADD COLUMN `category` VARCHAR(32) NOT NULL DEFAULT 'OTHER' COMMENT '商品分类：EXTERIOR 外饰 / INTERIOR 内饰 / POWERTRAIN 动力总成 / INTELLIGENT 智能化 / COMFORT 舒适便利 / SAFETY 安全 / ACCESSORY 选装附件 / OTHER 其他' AFTER `description`;

-- 新增索引：支持按 category + status 组合过滤
CREATE INDEX `idx_category_status` ON `mdm_option_family` (`category`, `status`);

-- ============================================================
-- 2. 选项族历史快照表同步新增 category 列
-- ============================================================
ALTER TABLE `mdm_option_family_history`
    ADD COLUMN `category` VARCHAR(32) DEFAULT NULL COMMENT '商品分类' AFTER `description`;
