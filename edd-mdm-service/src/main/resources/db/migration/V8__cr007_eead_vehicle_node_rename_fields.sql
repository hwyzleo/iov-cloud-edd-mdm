-- V8__cr007_eead_vehicle_node_rename_fields.sql
-- 将 VehicleNode 的 node_name/node_name_en 重命名为 name/name_local
-- 与 Product MDM / Party MDM 的命名约定保持一致

-- 重命名 mdm_eead_vehicle_node 表的列
ALTER TABLE mdm_eead_vehicle_node CHANGE node_name name VARCHAR(128) NOT NULL COMMENT '节点名称';
ALTER TABLE mdm_eead_vehicle_node CHANGE node_name_en name_local VARCHAR(128) DEFAULT NULL COMMENT '本地化名称';

-- 重命名 mdm_eead_vehicle_node_history 表的列
ALTER TABLE mdm_eead_vehicle_node_history CHANGE node_name name VARCHAR(128) NOT NULL COMMENT '节点名称';
ALTER TABLE mdm_eead_vehicle_node_history CHANGE node_name_en name_local VARCHAR(128) DEFAULT NULL COMMENT '本地化名称';
