# Series to CarLine 重命名实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将产品MDM子域中的"车系"相关命名从"Series"统一改为"CarLine"，包括需求文档、设计文档、代码、数据库、API路径和Kafka Topic。

**Architecture:** 采用分阶段重命名策略，先更新文档，然后修改代码，最后更新数据库和配置。每个阶段完成后进行编译验证，确保系统稳定性。

**Tech Stack:** Java 17, Spring Boot 2.7.x, MyBatis-Plus, Flyway, Kafka

---

## 文件结构映射

### 需要修改的文件

#### 文档文件
- `specs/product-mdm/requirements.md` - 需求文档
- `specs/product-mdm/design.md` - 设计文档

#### API模块文件（6个）
- `edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/service/SeriesService.java` → `CarLineService.java`
- `edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/fallback/SeriesServiceFallbackFactory.java` → `CarLineServiceFallbackFactory.java`
- `edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/SeriesResponse.java` → `CarLineResponse.java`
- `edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/SeriesPageResponse.java` → `CarLinePageResponse.java`
- `edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/SeriesHistoryResponse.java` → `CarLineHistoryResponse.java`
- `edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/SeriesHistoryPageResponse.java` → `CarLineHistoryPageResponse.java`

#### Service模块文件（25个）
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

#### 数据库迁移文件
- `src/main/resources/db/migration/V2__rename_series_to_car_line.sql` - 迁移脚本
- `src/main/resources/db/migration/V2_rollback__rename_car_line_to_series.sql` - 回滚脚本

#### 配置文件
- `src/main/resources/application.yml` - Kafka Topic配置

---

## 任务分解

### Task 1: 更新需求文档

**Files:**
- Modify: `specs/product-mdm/requirements.md`

- [ ] **Step 1: 读取需求文档**

```bash
cat specs/product-mdm/requirements.md
```

- [ ] **Step 2: 替换Series为CarLine**

```bash
sed -i '' 's/Series/CarLine/g' specs/product-mdm/requirements.md
```

- [ ] **Step 3: 验证替换结果**

```bash
grep -n "Series" specs/product-mdm/requirements.md || echo "No Series found - replacement successful"
```

- [ ] **Step 4: 提交更改**

```bash
git add specs/product-mdm/requirements.md
git commit -m "docs: rename Series to CarLine in requirements.md"
```

### Task 2: 更新设计文档

**Files:**
- Modify: `specs/product-mdm/design.md`

- [ ] **Step 1: 读取设计文档**

```bash
cat specs/product-mdm/design.md
```

- [ ] **Step 2: 替换Series为CarLine**

```bash
sed -i '' 's/Series/CarLine/g' specs/product-mdm/design.md
```

- [ ] **Step 3: 验证替换结果**

```bash
grep -n "Series" specs/product-mdm/design.md || echo "No Series found - replacement successful"
```

- [ ] **Step 4: 提交更改**

```bash
git add specs/product-mdm/design.md
git commit -m "docs: rename Series to CarLine in design.md"
```

### Task 3: 创建重命名脚本

**Files:**
- Create: `scripts/rename-series-to-carline.sh`

- [ ] **Step 1: 创建重命名脚本**

```bash
cat > scripts/rename-series-to-carline.sh << 'EOF'
#!/bin/bash

# 重命名Java文件
rename_file() {
    local old_path="$1"
    local new_path="$2"
    
    if [ -f "$old_path" ]; then
        mv "$old_path" "$new_path"
        echo "Renamed: $old_path -> $new_path"
    else
        echo "File not found: $old_path"
    fi
}

# 更新文件内容
update_content() {
    local file="$1"
    if [ -f "$file" ]; then
        sed -i '' 's/Series/CarLine/g' "$file"
        sed -i '' 's/series/carLine/g' "$file"
        echo "Updated content: $file"
    fi
}

# API模块文件
rename_file "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/service/SeriesService.java" \
            "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/service/CarLineService.java"

rename_file "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/fallback/SeriesServiceFallbackFactory.java" \
            "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/fallback/CarLineServiceFallbackFactory.java"

rename_file "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/SeriesResponse.java" \
            "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/CarLineResponse.java"

rename_file "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/SeriesPageResponse.java" \
            "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/CarLinePageResponse.java"

rename_file "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/SeriesHistoryResponse.java" \
            "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/CarLineHistoryResponse.java"

rename_file "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/SeriesHistoryPageResponse.java" \
            "edd-mdm-api/src/main/java/net/hwyz/iov/cloud/edd/mdm/api/vo/response/CarLineHistoryPageResponse.java"

# Service模块文件
rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/adapter/web/controller/mpt/MptSeriesController.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/adapter/web/controller/mpt/MptCarLineController.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/adapter/web/controller/service/ServiceSeriesController.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/adapter/web/controller/service/ServiceCarLineController.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/adapter/web/controller/upstream/UpstreamSeriesController.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/adapter/web/controller/upstream/UpstreamCarLineController.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/adapter/web/assembler/SeriesAssembler.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/adapter/web/assembler/CarLineAssembler.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/service/SeriesAppService.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/service/CarLineAppService.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/aggregate/Series.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/aggregate/CarLine.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/entity/SeriesHistory.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/entity/CarLineHistory.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/valueobject/SeriesType.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/valueobject/CarLineType.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/valueobject/SeriesStatus.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/valueobject/CarLineStatus.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/event/SeriesCreatedEvent.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/event/CarLineCreatedEvent.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/event/SeriesUpdatedEvent.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/event/CarLineUpdatedEvent.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/event/SeriesDeactivatedEvent.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/event/CarLineDeactivatedEvent.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/repository/SeriesRepository.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/repository/CarLineRepository.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/repository/SeriesRepositoryImpl.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/repository/CarLineRepositoryImpl.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/result/SeriesDto.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/result/CarLineDto.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/result/SeriesHistoryDto.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/result/CarLineHistoryDto.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/cmd/SeriesCreateCmd.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/cmd/CarLineCreateCmd.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/cmd/SeriesUpdateCmd.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/cmd/CarLineUpdateCmd.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/query/SeriesQuery.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/application/dto/query/CarLineQuery.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/po/SeriesPo.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/po/CarLinePo.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/po/SeriesHistoryPo.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/po/CarLineHistoryPo.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/mapper/SeriesMapper.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/mapper/CarLineMapper.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/mapper/SeriesHistoryMapper.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/mapper/CarLineHistoryMapper.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/converter/SeriesConverter.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/converter/CarLineConverter.java"

rename_file "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/converter/SeriesHistoryConverter.java" \
            "edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/infrastructure/persistence/converter/CarLineHistoryConverter.java"

# 更新所有Java文件内容
find . -name "*.java" -type f -exec grep -l "Series" {} \; | while read file; do
    update_content "$file"
done

echo "Rename completed!"
EOF
```

- [ ] **Step 2: 添加执行权限**

```bash
chmod +x scripts/rename-series-to-carline.sh
```

- [ ] **Step 3: 提交脚本**

```bash
git add scripts/rename-series-to-carline.sh
git commit -m "feat: add rename script for Series to CarLine migration"
```

### Task 4: 执行重命名脚本

**Files:**
- Modify: 所有Java文件

- [ ] **Step 1: 执行重命名脚本**

```bash
./scripts/rename-series-to-carline.sh
```

- [ ] **Step 2: 验证文件重命名**

```bash
find . -name "*Series*.java" -type f | head -5
```

Expected: 应该没有找到Series文件

- [ ] **Step 3: 验证内容更新**

```bash
grep -r "Series" --include="*.java" . | head -5
```

Expected: 应该没有找到Series引用

- [ ] **Step 4: 提交更改**

```bash
git add .
git commit -m "refactor: rename all Series classes and references to CarLine"
```

### Task 5: 创建Flyway迁移脚本

**Files:**
- Create: `src/main/resources/db/migration/V2__rename_series_to_car_line.sql`
- Create: `src/main/resources/db/migration/V2_rollback__rename_car_line_to_series.sql`

- [ ] **Step 1: 创建迁移脚本**

```bash
cat > src/main/resources/db/migration/V2__rename_series_to_car_line.sql << 'EOF'
-- 重命名主表
RENAME TABLE mdm_series TO mdm_car_line;

-- 重命名历史表
RENAME TABLE mdm_series_history TO mdm_car_line_history;

-- 重命名字段
ALTER TABLE mdm_car_line CHANGE series_type car_line_type VARCHAR(16);
ALTER TABLE mdm_car_line_history CHANGE series_type car_line_type VARCHAR(16);

-- 更新索引（根据实际索引名称进行更新）
-- 注意：需要检查并更新所有包含series的索引名称
EOF
```

- [ ] **Step 2: 创建回滚脚本**

```bash
cat > src/main/resources/db/migration/V2_rollback__rename_car_line_to_series.sql << 'EOF'
-- 回滚主表
RENAME TABLE mdm_car_line TO mdm_series;

-- 回滚历史表
RENAME TABLE mdm_car_line_history TO mdm_series_history;

-- 回滚字段
ALTER TABLE mdm_series CHANGE car_line_type series_type VARCHAR(16);
ALTER TABLE mdm_series_history CHANGE car_line_type series_type VARCHAR(16);

-- 回滚索引（根据实际索引名称进行更新）
EOF
```

- [ ] **Step 3: 提交迁移脚本**

```bash
git add src/main/resources/db/migration/V2__rename_series_to_car_line.sql
git add src/main/resources/db/migration/V2_rollback__rename_car_line_to_series.sql
git commit -m "feat: add Flyway migration script for Series to CarLine rename"
```

### Task 6: 更新Kafka配置

**Files:**
- Modify: `src/main/resources/application.yml`

- [ ] **Step 1: 读取配置文件**

```bash
cat src/main/resources/application.yml
```

- [ ] **Step 2: 更新Kafka Topic配置**

```bash
sed -i '' 's/mdm\.product\.series/mdm.product.carline/g' src/main/resources/application.yml
sed -i '' 's/upstream\.\*\.product\.series/upstream.*.product.carline/g' src/main/resources/application.yml
```

- [ ] **Step 3: 验证配置更新**

```bash
grep -n "series" src/main/resources/application.yml || echo "No series found in config - replacement successful"
```

- [ ] **Step 4: 提交配置更改**

```bash
git add src/main/resources/application.yml
git commit -m "config: update Kafka topic names from series to carline"
```

### Task 7: 编译验证

**Files:**
- None

- [ ] **Step 1: 清理并编译项目**

```bash
mvn clean compile -DskipTests
```

Expected: 编译成功，没有错误

- [ ] **Step 2: 运行单元测试**

```bash
mvn test
```

Expected: 所有测试通过

- [ ] **Step 3: 提交测试结果**

```bash
git add .
git commit -m "test: verify Series to CarLine rename compilation and tests"
```

### Task 8: 更新API文档

**Files:**
- Modify: Swagger/OpenAPI配置（如果有）

- [ ] **Step 1: 检查Swagger配置**

```bash
find . -name "*swagger*" -o -name "*openapi*" | head -5
```

- [ ] **Step 2: 更新API路径**

```bash
find . -name "*.java" -type f -exec grep -l "/series/" {} \; | while read file; do
    sed -i '' 's|/series/|/carline/|g' "$file"
    echo "Updated API path: $file"
done
```

- [ ] **Step 3: 提交API文档更新**

```bash
git add .
git commit -m "docs: update API paths from series to carline"
```

### Task 9: 最终验证

**Files:**
- None

- [ ] **Step 1: 全面搜索Series引用**

```bash
grep -r "Series" --include="*.java" --include="*.yml" --include="*.xml" --include="*.sql" . | grep -v "CarLine" | head -10
```

Expected: 应该没有找到Series引用

- [ ] **Step 2: 验证数据库迁移脚本**

```bash
cat src/main/resources/db/migration/V2__rename_series_to_car_line.sql
```

- [ ] **Step 3: 验证API路径**

```bash
grep -r "/carline/" --include="*.java" . | head -5
```

- [ ] **Step 4: 最终提交**

```bash
git add .
git commit -m "feat: complete Series to CarLine rename migration"
```

---

## 验收标准

1. ✅ 所有文档中的Series已替换为CarLine
2. ✅ 所有Java文件中的Series已替换为CarLine
3. ✅ 数据库表名已更新（通过Flyway迁移脚本）
4. ✅ API路径已更新
5. ✅ Kafka Topic已更新
6. ✅ 所有测试通过
7. ✅ 应用能正常启动

---

## 回滚计划

如果出现问题，可以按照以下步骤回滚：

1. **代码回滚**
   ```bash
   git reset --hard HEAD~1
   ```

2. **数据库回滚**
   ```bash
   # 执行回滚迁移脚本
   mysql -u username -p database_name < src/main/resources/db/migration/V2_rollback__rename_car_line_to_series.sql
   ```

3. **配置回滚**
   ```bash
   git checkout HEAD~1 -- src/main/resources/application.yml
   ```