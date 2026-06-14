# MDM-DSN-CR-024 PartType 枚举重构实施计划

> **For Hermes:** Use subagent-driven-development skill to implement this plan task-by-task.

**Goal:** 将 PartType 枚举从混合维度（含 SOFTWARE/ASSEMBLY）重构为单一维度「物料性质/类型」，移除 SOFTWARE 和 ASSEMBLY，统一由 is_software/is_assembly 布尔字段承载。

**Architecture:** 
- 修改 PartType 枚举，移除 SOFTWARE 和 ASSEMBLY
- 更新所有引用 PartType.ASSEMBLY/SOFTWARE 的代码
- 更新测试用例，使用 isAssembly/isSoftware 布尔字段替代
- 更新文档，同步设计变更

**Tech Stack:** Java 17, Spring Boot, JUnit 5, Mockito

---

## 任务清单

### Task 1: 修改 PartType 枚举定义

**Objective:** 移除 PartType 枚举中的 SOFTWARE 和 ASSEMBLY 值

**Files:**
- Modify: `edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/valueobject/PartType.java`

**Step 1: 查看当前枚举定义**

```bash
cat edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/valueobject/PartType.java
```

**Step 2: 修改枚举定义**

```java
package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

/**
 * 零件类型枚举（Material 子域）
 * 物料性质/类型维度：RAW_MATERIAL / STANDARD_PART / CUSTOM_PART
 * 软硬件判定：由 is_software 布尔字段承载
 * 总成判定：由 is_assembly 布尔字段承载
 *
 * @author hwyz_leo
 */
public enum PartType {
    RAW_MATERIAL,
    STANDARD_PART,
    CUSTOM_PART
}
```

**Step 3: 验证编译**

```bash
cd edd-mdm-service && mvn compile -DskipTests
```

**Step 4: 提交**

```bash
git add edd-mdm-service/src/main/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/valueobject/PartType.java
git commit -m "refactor: remove SOFTWARE and ASSEMBLY from PartType enum (MDM-DSN-CR-024)"
```

---

### Task 2: 更新 PartTest 测试用例

**Objective:** 将测试中使用的 PartType.ASSEMBLY 改为合适的 partType + isAssembly=true

**Files:**
- Modify: `edd-mdm-service/src/test/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/aggregate/PartTest.java`

**Step 1: 查看当前测试用例**

```bash
grep -n "PartType.ASSEMBLY\|PartType.SOFTWARE" edd-mdm-service/src/test/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/aggregate/PartTest.java
```

**Step 2: 批量替换测试用例**

将所有 `PartType.ASSEMBLY` 替换为 `PartType.STANDARD_PART`，并在对应的 Part 创建中添加 `.isAssembly(true)`

示例修改：
```java
// 修改前
Part part = Part.create(
    partCode, "Test Part", "测试零件", "描述",
    "MC001", PartType.ASSEMBLY, "NODE001", "SUP001",
    false, false, false, false,
    null, false, false, false, null, null,
    false, null, null, null, null, null,
    null, null, null, null, null,
    null, null, null, null, "test"
);

// 修改后
Part part = Part.create(
    partCode, "Test Part", "测试零件", "描述",
    "MC001", PartType.STANDARD_PART, "NODE001", "SUP001",
    false, true, false, false,  // isAssembly=true
    null, false, false, false, null, null,
    false, null, null, null, null, null,
    null, null, null, null, null,
    null, null, null, null, "test"
);
```

**Step 3: 更新断言**

```java
// 修改前
assertEquals(PartType.ASSEMBLY, part.getPartType());

// 修改后
assertEquals(PartType.STANDARD_PART, part.getPartType());
assertTrue(part.getIsAssembly());
```

**Step 4: 运行测试验证**

```bash
cd edd-mdm-service && mvn test -Dtest=PartTest
```

**Step 5: 提交**

```bash
git add edd-mdm-service/src/test/java/net/hwyz/iov/cloud/edd/mdm/service/domain/model/aggregate/PartTest.java
git commit -m "test: update PartTest to use isAssembly instead of PartType.ASSEMBLY (MDM-DSN-CR-024)"
```

---

### Task 3: 搜索并更新其他引用 PartType.ASSEMBLY/SOFTWARE 的代码

**Objective:** 确保代码库中没有其他地方引用已移除的枚举值

**Files:**
- 搜索并更新所有引用 `PartType.ASSEMBLY` 或 `PartType.SOFTWARE` 的文件

**Step 1: 搜索所有引用**

```bash
grep -rn "PartType.ASSEMBLY\|PartType.SOFTWARE" --include="*.java" edd-mdm-service/
```

**Step 2: 逐个更新引用**

根据搜索结果，更新每个引用：
- 如果是测试代码，使用 `PartType.STANDARD_PART` 或 `PartType.CUSTOM_PART` + `isAssembly=true`/`isSoftware=true`
- 如果是业务代码，检查是否需要调整逻辑

**Step 3: 运行全量测试**

```bash
cd edd-mdm-service && mvn test
```

**Step 4: 提交**

```bash
git add -A
git commit -m "refactor: update all references to removed PartType values (MDM-DSN-CR-024)"
```

---

### Task 4: 更新需求文档

**Objective:** 同步更新需求文档中关于 PartType 枚举的描述

**Files:**
- Modify: `specs/mdm/requirements.md`

**Step 1: 查找需要更新的内容**

```bash
grep -n "SOFTWARE\|ASSEMBLY" specs/mdm/requirements.md
```

**Step 2: 更新 PartType 枚举描述**

将：
```
part_type: 枚举，零件类型（沿用 CR-009 PartType：RAW_MATERIAL / STANDARD_PART / CUSTOM_PART / SOFTWARE / ASSEMBLY）
```

更新为：
```
part_type: 枚举，零件类型（物料性质/类型维度：RAW_MATERIAL / STANDARD_PART / CUSTOM_PART；软硬件判定由 is_software 承载，总成判定由 is_assembly 承载）
```

**Step 3: 更新发号规则描述**

将：
```
WHEN 系统为软件件（part_type=SOFTWARE 或 is_software=true，沿用 CR-009 isSoftware 字段语义）发号 THEN THE SYSTEM SHALL 在 code 与 base_no 前加 `S` 前缀
```

更新为：
```
WHEN 系统为软件件（is_software=true）发号 THEN THE SYSTEM SHALL 在 code 与 base_no 前加 `S` 前缀
```

**Step 4: 提交**

```bash
git add specs/mdm/requirements.md
git commit -m "docs: update requirements for PartType enum refactoring (MDM-DSN-CR-024)"
```

---

### Task 5: 更新设计文档

**Objective:** 同步更新设计文档中关于 PartType 枚举的描述

**Files:**
- Modify: `specs/mdm/design.md`

**Step 1: 查找需要更新的内容**

```bash
grep -n "SOFTWARE\|ASSEMBLY\|PartType" specs/mdm/design.md
```

**Step 2: 更新 PartType 枚举定义**

将：
```
| `service.domain.model.valueobject.PartType` | 零件类型枚举（5 类：RAW_MATERIAL / STANDARD_PART / CUSTOM_PART / SOFTWARE / ASSEMBLY） |
```

更新为：
```
| `service.domain.model.valueobject.PartType` | 零件类型枚举（3 类：RAW_MATERIAL / STANDARD_PART / CUSTOM_PART；软硬件/总成判定由 is_software/is_assembly 承载） |
```

**Step 3: 更新数据模型描述**

将：
```
| part_type | VARCHAR(32) | Y | 零件类型枚举（RAW_MATERIAL / STANDARD_PART / CUSTOM_PART / SOFTWARE / ASSEMBLY） |
```

更新为：
```
| part_type | VARCHAR(32) | Y | 零件类型枚举（RAW_MATERIAL / STANDARD_PART / CUSTOM_PART；软硬件/总成判定由 is_software/is_assembly 承载） |
```

**Step 4: 更新软硬件前缀决策**

将：
```
| 软硬件前缀 | **软件件（is_software / partType=SOFTWARE）加 `S` 前缀，硬件无前缀**；前缀仅作区分位，共用同一全局序列 |
```

更新为：
```
| 软硬件前缀 | **软件件（is_software=true）加 `S` 前缀，硬件无前缀**；前缀仅作区分位，共用同一全局序列 |
```

**Step 5: 提交**

```bash
git add specs/mdm/design.md
git commit -m "docs: update design document for PartType enum refactoring (MDM-DSN-CR-024)"
```

---

### Task 6: 更新 CR-024 设计变更记录

**Objective:** 在 CR-024 变更记录中添加实施状态

**Files:**
- 更新 Notion 中的 MDM-DSN-CR-024 变更记录（可选）

**Step 1: 验证所有修改**

```bash
cd edd-mdm-service && mvn clean test
```

**Step 2: 检查是否还有遗漏**

```bash
grep -rn "SOFTWARE\|ASSEMBLY" --include="*.java" edd-mdm-service/src/main/java/ | grep -v "isSoftware\|isAssembly\|//\|/\*\|\*"
```

**Step 3: 最终提交**

```bash
git add -A
git commit -m "feat: complete PartType enum refactoring for MDM-DSN-CR-024"
```

---

## 验证清单

- [ ] PartType 枚举只包含 RAW_MATERIAL、STANDARD_PART、CUSTOM_PART
- [ ] 所有测试通过
- [ ] 代码库中无 `PartType.ASSEMBLY` 或 `PartType.SOFTWARE` 引用
- [ ] 发号逻辑正常（使用 isSoftware 决定前缀）
- [ ] 需求文档已更新
- [ ] 设计文档已更新

---

## 注意事项

1. **向后兼容性**：本变更不涉及历史数据迁移
2. **下游影响**：暂不考虑下游系统同步更新
3. **测试覆盖**：确保所有使用 PartType 的测试用例都已更新
4. **文档同步**：需求文档和设计文档必须同步更新
