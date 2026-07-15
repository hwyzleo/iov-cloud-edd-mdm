-- 添加 version 列到 RXSWIN 登记表
ALTER TABLE `mdm_eead_rxswin_registry`
ADD COLUMN `version` INT NOT NULL DEFAULT 1 COMMENT '业务版本号' AFTER `trace_id`;
