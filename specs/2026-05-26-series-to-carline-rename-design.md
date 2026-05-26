# Series to CarLine 重命名设计文档

## 1. 概述

### 1.1 目标
将产品MDM子域中的"车系"相关命名从"Series"统一改为"CarLine"，包括需求文档、设计文档、代码、数据库、API路径和Kafka Topic。

### 1.2 范围
- **文档**：需求文档（requirements.md）和设计文档（design.md）
- **代码**：所有Java类名、方法名、变量名
- **数据库**：表名、字段名
- **API路径**：所有包含"series"的API端点
- **Kafka Topic**：所有包含"series"的Topic名称

### 1.3 背景
用户要求统一命名规范，将"车系"的英文命名从"Series"改为"CarLine"，以提高代码可读性和一致性。

## 2. 设计方案

### 2.1 重命名策略
采用分阶段重命名策略：
1. 阶段1：更新需求文档和设计文档
2. 阶段2：修改代码中的类名、方法名、变量名
3. 阶段3：创建Flyway数据库迁移脚本
4. 阶段4：修改API路径和Kafka Topic配置
5. 阶段5：测试验证

### 2.2 具体变更清单

#### 2.2.1 文档文件（2个）
- `specs/product-mdm/requirements.md` - 需求文档
- `specs/product-mdm/design.md` - 设计文档

#### 2.2.2 Java类文件（31个文件需要重命名）

**API模块（6个）**：
- `SeriesService.java` → `CarLineService.java`
- `SeriesServiceFallbackFactory.java` → `CarLineServiceFallbackFactory.java`
- `SeriesResponse.java` → `CarLineResponse.java`
- `SeriesPageResponse.java` → `CarLinePageResponse.java`
- `SeriesHistoryResponse.java` → `CarLineHistoryResponse.java`
- `SeriesHistoryPageResponse.java` → `CarLineHistoryPageResponse.java`

**Service模块（25个）**：
- 控制器：`MptSeriesController.java` → `MptCarLineController.java`
- 控制器：`ServiceSeriesController.java` → `ServiceCarLineController.java`
- 控制器：`UpstreamSeriesController.java` → `UpstreamCarLineController.java`
- 汇编器：`SeriesAssembler.java` → `CarLineAssembler.java`
- 应用服务：`SeriesAppService.java` → `CarLineAppService.java`
- 领域模型：`Series.java` → `CarLine.java`
- 领域模型：`SeriesHistory.java` → `CarLineHistory.java`
- 值对象：`SeriesType.java` → `CarLineType.java`
- 值对象：`SeriesStatus.java` → `CarLineStatus.java`
- 事件：`SeriesCreatedEvent.java` → `CarLineCreatedEvent.java`
- 事件：`SeriesUpdatedEvent.java` → `CarLineUpdatedEvent.java`
- 事件：`SeriesDeactivatedEvent.java` → `CarLineDeactivatedEvent.java`
- 仓储：`SeriesRepository.java` → `CarLineRepository.java`
- 仓储实现：`SeriesRepositoryImpl.java` → `CarLineRepositoryImpl.java`
- DTO：`SeriesDto.java` → `CarLineDto.java`
- DTO：`SeriesHistoryDto.java` → `CarLineHistoryDto.java`
- DTO：`SeriesCreateCmd.java` → `CarLineCreateCmd.java`
- DTO：`SeriesUpdateCmd.java` → `CarLineUpdateCmd.java`
- DTO：`SeriesQuery.java` → `CarLineQuery.java`
- PO：`SeriesPo.java` → `CarLinePo.java`
- PO：`SeriesHistoryPo.java` → `CarLineHistoryPo.java`
- Mapper：`SeriesMapper.java` → `CarLineMapper.java`
- Mapper：`SeriesHistoryMapper.java` → `CarLineHistoryMapper.java`
- Converter：`SeriesConverter.java` → `CarLineConverter.java`
- Converter：`SeriesHistoryConverter.java` → `CarLineHistoryConverter.java`

#### 2.2.3 数据库变更
- 表名：`mdm_series` → `mdm_car_line`
- 表名：`mdm_series_history` → `mdm_car_line_history`
- 字段：`series_type` → `car_line_type`

#### 2.2.4 API路径变更
- `/api/mpt/mdm/series/v1/` → `/api/mpt/mdm/carline/v1/`
- `/api/service/series/v1/` → `/api/service/carline/v1/`
- `/api/upstream/mdm/series/v1/` → `/api/upstream/mdm/carline/v1/`

#### 2.2.5 Kafka Topic变更
- `mdm.product.series.created` → `mdm.product.carline.created`
- `mdm.product.series.updated` → `mdm.product.carline.updated`
- `mdm.product.series.deactivated` → `mdm.product.carline.deactivated`
- `upstream.<sourceSystem>.product.series` → `upstream.<sourceSystem>.product.carline`

#### 2.2.6 其他变更
- 错误码描述中的"Series"改为"CarLine"
- 日志和注释中的"Series"改为"CarLine"
- 枚举值：`EntityType.SERIES` → `EntityType.CAR_LINE`

### 2.3 风险评估

#### 2.3.1 主要风险
1. **数据库迁移风险**
   - 风险：表名重命名可能导致外键约束失效
   - 缓解：检查所有外键引用，更新约束名称

2. **API兼容性风险**
   - 风险：下游系统依赖旧API路径
   - 缓解：与下游系统协调，提供迁移指南

3. **Kafka消息兼容性风险**
   - 风险：消费者订阅旧Topic名称
   - 缓解：与下游系统协调，更新消费者配置

4. **代码编译风险**
   - 风险：重命名可能导致编译错误
   - 缓解：分阶段修改，每阶段后编译验证

#### 2.3.2 注意事项
1. **命名一致性**
   - 确保所有"Series"都改为"CarLine"，包括注释和日志
   - 保持大小写一致：`CarLine`（类名）、`carLine`（变量名）、`car_line`（数据库字段）

2. **数据库字段映射**
   - `series_type` → `car_line_type`（数据库字段）
   - `seriesType` → `carLineType`（Java字段）
   - `SeriesType` → `CarLineType`（枚举类）

3. **Flyway迁移脚本**
   - 创建迁移脚本：`V2__rename_series_to_car_line.sql`
   - 包含表重命名、字段重命名、索引更新

4. **测试验证**
   - 单元测试：更新所有测试类中的Series引用
   - 集成测试：验证API路径和Kafka Topic
   - 数据库测试：验证迁移脚本正确性

5. **文档同步**
   - 更新所有文档中的Series引用
   - 更新API文档（Swagger）

### 2.4 依赖关系

#### 2.4.1 内部依赖
- 所有包含Series的类都需要修改
- 所有引用Series的类都需要更新导入语句

#### 2.4.2 外部依赖
- 下游系统需要更新API调用路径
- 下游系统需要更新Kafka订阅配置

### 2.5 回滚策略

1. **代码回滚**
   - 使用Git回滚到重命名前的版本

2. **数据库回滚**
   - 创建回滚迁移脚本：`V2_rollback__rename_car_line_to_series.sql`

3. **API回滚**
   - 恢复旧API路径配置

## 3. 实施计划

### 3.1 阶段1：文档更新（预计时间：30分钟）

**步骤1.1：更新需求文档**
- 文件：`specs/product-mdm/requirements.md`
- 操作：将所有"Series"替换为"CarLine"
- 验证：检查文档中的术语一致性

**步骤1.2：更新设计文档**
- 文件：`specs/product-mdm/design.md`
- 操作：将所有"Series"替换为"CarLine"
- 验证：检查文档中的术语一致性

### 3.2 阶段2：代码重命名（预计时间：2小时）

**步骤2.1：创建重命名脚本**
- 创建Python脚本自动重命名文件
- 创建脚本更新文件内容中的Series引用

**步骤2.2：重命名Java类文件**
- 按照变更清单重命名31个Java文件
- 更新包声明和导入语句

**步骤2.3：更新类内容**
- 更新类名、方法名、变量名
- 更新注释和日志信息

### 3.3 阶段3：数据库迁移（预计时间：30分钟）

**步骤3.1：创建Flyway迁移脚本**
- 文件：`src/main/resources/db/migration/V2__rename_series_to_car_line.sql`
- 操作：
  ```sql
  -- 重命名主表
  RENAME TABLE mdm_series TO mdm_car_line;
  
  -- 重命名历史表
  RENAME TABLE mdm_series_history TO mdm_car_line_history;
  
  -- 重命名字段
  ALTER TABLE mdm_car_line CHANGE series_type car_line_type VARCHAR(16);
  ALTER TABLE mdm_car_line_history CHANGE series_type car_line_type VARCHAR(16);
  
  -- 更新索引（根据实际索引名称进行更新）
  -- 注意：需要检查并更新所有包含series的索引名称
  ```

**步骤3.2：创建回滚脚本**
- 文件：`src/main/resources/db/migration/V2_rollback__rename_car_line_to_series.sql`

### 3.4 阶段4：API和Kafka配置更新（预计时间：30分钟）

**步骤4.1：更新API路径**
- 更新控制器类中的`@RequestMapping`注解
- 更新Feign客户端接口中的路径

**步骤4.2：更新Kafka Topic配置**
- 更新`application.yml`中的Topic名称
- 更新生产者和消费者配置

### 3.5 阶段5：测试验证（预计时间：1小时）

**步骤5.1：编译验证**
- 运行`mvn clean compile`确保编译通过

**步骤5.2：单元测试**
- 运行`mvn test`确保所有测试通过

**步骤5.3：集成测试**
- 启动应用，测试API端点
- 测试Kafka消息发送和接收

**步骤5.4：数据库测试**
- 运行Flyway迁移脚本
- 验证数据库表结构正确

### 3.6 总体时间估计：4-5小时

### 3.7 依赖条件
1. 开发环境已配置
2. 数据库已备份
3. 下游系统已通知

### 3.8 验收标准
1. 所有文档中的Series已替换为CarLine
2. 所有Java文件中的Series已替换为CarLine
3. 数据库表名已更新
4. API路径已更新
5. Kafka Topic已更新
6. 所有测试通过
7. 应用能正常启动

## 4. 附录

### 4.1 相关文档
- 需求文档：`specs/product-mdm/requirements.md`
- 设计文档：`specs/product-mdm/design.md`

### 4.2 变更历史
| 日期 | 变更ID | 类型 | 描述 |
|------|--------|------|------|
| 2026-05-26 | CR-001 | Added | 首版产出：Series到CarLine重命名设计文档 |