-- V27: CR-033 放宽 Configuration 名称字段长度
-- mdm_configuration 与 mdm_configuration_history 的 name/name_local 由 VARCHAR(128) 放宽至 VARCHAR(512)
-- 根因：MPT 前端将车型/系列/颜色/轮毂/备胎/平台拼接为配置完整名称，实际可达 165 字符，超出原 128 上限，
--       主表 INSERT 触发 Data truncation。
-- 约束：只扩大字段容量，不改字段名、类型类别、必填性、索引或事件结构；主表与历史表在同一迁移内完成，
--       避免主表成功而快照失败。存量数据无需回填。

ALTER TABLE `mdm_configuration`
    MODIFY COLUMN `name`       VARCHAR(512) NOT NULL COMMENT '配置名称',
    MODIFY COLUMN `name_local` VARCHAR(512) DEFAULT NULL COMMENT '本地化名称';

ALTER TABLE `mdm_configuration_history`
    MODIFY COLUMN `name`       VARCHAR(512) NOT NULL COMMENT '配置名称',
    MODIFY COLUMN `name_local` VARCHAR(512) DEFAULT NULL COMMENT '本地化名称';
