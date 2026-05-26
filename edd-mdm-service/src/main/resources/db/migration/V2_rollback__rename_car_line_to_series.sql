-- V2 Rollback: 回滚CarLine为Series
-- 回滚主表
RENAME TABLE mdm_car_line TO mdm_series;

-- 回滚历史表
RENAME TABLE mdm_car_line_history TO mdm_series_history;

-- 回滚字段：主表
ALTER TABLE mdm_series CHANGE car_line_type series_type VARCHAR(16) DEFAULT NULL COMMENT '车系类型：SEDAN/SUV/MPV/PICKUP/COMMERCIAL';

-- 回滚字段：历史表
ALTER TABLE mdm_series_history CHANGE car_line_type series_type VARCHAR(16) DEFAULT NULL COMMENT '车系类型';

-- 恢复主表注释
ALTER TABLE mdm_series COMMENT '车系表';

-- 恢复历史表注释
ALTER TABLE mdm_series_history COMMENT '车系历史快照表';
