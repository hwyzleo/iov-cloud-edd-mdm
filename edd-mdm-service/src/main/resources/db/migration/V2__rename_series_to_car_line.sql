-- V2: 重命名Series为CarLine
-- 重命名主表
RENAME TABLE mdm_series TO mdm_car_line;

-- 重命名历史表
RENAME TABLE mdm_series_history TO mdm_car_line_history;

-- 重命名字段：主表
ALTER TABLE mdm_car_line CHANGE series_type car_line_type VARCHAR(16) DEFAULT NULL COMMENT '车系类型：SEDAN/SUV/MPV/PICKUP/COMMERCIAL';

-- 重命名字段：历史表
ALTER TABLE mdm_car_line_history CHANGE series_type car_line_type VARCHAR(16) DEFAULT NULL COMMENT '车系类型';

-- 更新主表注释
ALTER TABLE mdm_car_line COMMENT '车系表';

-- 更新历史表注释
ALTER TABLE mdm_car_line_history COMMENT '车系历史快照表';
