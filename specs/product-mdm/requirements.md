# Product MDM 子域 - Requirements

## 1. Overview

edd-mdm 是企业数字底座领域的横向微服务，负责承载产品树主数据（Brand / Series / Platform）的 Golden Record。本 feature 建立 Product MDM 子域，承接原 VMD 项目侧的品牌 / 车系 / 平台主数据 SSOT 上移至 edd-mdm，实现主数据统一治理与分发能力。

**业务属性**：横向  
**领域**：企业数字底座  
**微服务名**：edd-mdm

## 2. Background & Goals

### 背景

当前品牌 / 车系 / 平台主数据由 VMD 项目维护，随着车联网平台业务扩展，多个下游系统（订单、销售配置器、BI 等）都需要消费这些主数据，但缺乏统一的主数据管理入口。为解决主数据分散、质量不一致、分发困难等问题，需要将产品树主数据的 SSOT 上移至横向的 edd-mdm 服务。

### 目标

- **G1**：建立 edd-mdm 作为 Brand / Series / Platform 三类主数据的 SSOT（Single Source of Truth）
- **G2**：提供统一的主数据 CRUD 维护能力，支持 MDM-User 后台管理
- **G3**：实现版本控制与生效期管理，支持历史追溯与审计
- **G4**：通过事务性发件箱（Outbox Pattern）实现可靠的事件分发，支持下游订阅同步
- **G5**：提供 Feign 全量快照接口，支持下游 Bootstrap 与对账
- **G6**：遵循 DDD 设计原则，保持与 VMD 项目架构风格一致

### 非目标（明确不做）

- **NG1**：不接管 VMD 的车辆实例（VIN）/ 零部件 / 生命周期 / 车型（Model）/ 基础车型（BaseModel）/ 生产配置（BuildConfig）/ 特征族（FeatureFamily）/ 制造厂商（Manufacturer）/ 供应商（Supplier）等领域
- **NG2**：不接管 Customer / Material / Employee / Location 等其他 MDM 子域
- **NG3**：不实施跨系统主数据合并（多源 Golden Record 合并裁决），首期只支持单一权威源
- **NG4**：不实施数据质量打分引擎
- **NG5**：不实施跨系统 ID 映射表
- **NG6**：不实施审批工作流（首期 CRUD 即发布）
- **NG7**：不实施 Manufacturer / Supplier（留待后续 CR）
- **NG8**：不实施对账机制（留待后续 CR）

## 3. User Stories

### 域 1: MDM 后台维护

#### US-001: CRUD Brand

**As a** MDM-User, **I want** CRUD Brand, **so that** 管理品牌 Golden Record。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Brand 且 code 唯一 THEN THE SYSTEM SHALL 持久化 Brand 记录并设置 version=1，并自动填充 create_by（当前认证用户）、create_time（当前时间）、modify_by、modify_time
- WHEN MDM-User 创建 Brand 且 code 已存在 THEN THE SYSTEM SHALL 返回错误码 807001 并拒绝创建
- WHEN MDM-User 更新 Brand 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，并自动填充 modify_by（当前认证用户）、modify_time（当前时间）
- WHEN MDM-User 更新 Brand 且记录不存在 THEN THE SYSTEM SHALL 返回错误码 807002 并拒绝更新
- WHEN MDM-User 失效 Brand 且 status=ACTIVE THEN THE SYSTEM SHALL 设置 status=INACTIVE 和 effectiveTo=now() 并自增 version，并自动填充 modify_by、modify_time
- WHEN MDM-User 删除 Brand 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录
- WHEN MDM-User 删除 Brand 且 status≠DRAFT THEN THE SYSTEM SHALL 返回错误码 807003 并拒绝删除
- IF Brand 的 effectiveFrom > effectiveTo THEN THE SYSTEM SHALL 返回错误码 807004 并拒绝保存

#### US-002: CRUD Series

**As a** MDM-User, **I want** CRUD Series（含按 brandCode 过滤、按 status 过滤），**so that** 管理车系 Golden Record。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Series 且 code 唯一且 brandCode 指向已存在且 status=ACTIVE 的 Brand THEN THE SYSTEM SHALL 持久化 Series 记录并设置 version=1，并自动填充 create_by（当前认证用户）、create_time（当前时间）、modify_by、modify_time
- WHEN MDM-User 创建 Series 且 code 已存在 THEN THE SYSTEM SHALL 返回错误码 807001 并拒绝创建
- WHEN MDM-User 创建 Series 且 brandCode 指向不存在或 status≠ACTIVE 的 Brand THEN THE SYSTEM SHALL 返回错误码 807005 并拒绝创建
- WHEN MDM-User 更新 Series 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，并自动填充 modify_by（当前认证用户）、modify_time（当前时间）
- WHEN MDM-User 失效 Series 且 status=ACTIVE THEN THE SYSTEM SHALL 设置 status=INACTIVE 和 effectiveTo=now() 并自增 version，并自动填充 modify_by、modify_time
- WHEN MDM-User 删除 Series 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录
- WHEN MDM-User 查询 Series 列表 THEN THE SYSTEM SHALL 支持按 brandCode 和 status 过滤
- IF Series 的 effectiveFrom > effectiveTo THEN THE SYSTEM SHALL 返回错误码 807004 并拒绝保存

#### US-003: CRUD Platform

**As a** MDM-User, **I want** CRUD Platform, **so that** 管理平台 Golden Record。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Platform 且 code 唯一 THEN THE SYSTEM SHALL 持久化 Platform 记录并设置 version=1，并自动填充 create_by（当前认证用户）、create_time（当前时间）、modify_by、modify_time
- WHEN MDM-User 创建 Platform 且 code 已存在 THEN THE SYSTEM SHALL 返回错误码 807001 并拒绝创建
- WHEN MDM-User 更新 Platform 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，并自动填充 modify_by（当前认证用户）、modify_time（当前时间）
- WHEN MDM-User 失效 Platform 且 status=ACTIVE THEN THE SYSTEM SHALL 设置 status=INACTIVE 和 effectiveTo=now() 并自增 version，并自动填充 modify_by、modify_time
- WHEN MDM-User 删除 Platform 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录
- IF Platform 的 effectiveFrom > effectiveTo THEN THE SYSTEM SHALL 返回错误码 807004 并拒绝保存

### 域 2: 版本控制与生效期

#### US-004: 历史版本快照

**As a** System, **I want** 每次变更生成历史版本快照, **so that** 支持回溯与审计。

**Acceptance Criteria** (EARS 语法):

- WHEN Brand / Series / Platform 发生创建、更新、失效操作 THEN THE SYSTEM SHALL 在 history 表插入一条完整快照记录
- WHEN history 表插入快照 THEN THE SYSTEM SHALL 记录变更时间、操作人、变更前后的 version
- WHEN 查询历史版本 THEN THE SYSTEM SHALL 按 entityId 和 version 降序返回快照列表

#### US-005: 生效期合法性校验

**As a** System, **I want** 校验 effectiveFrom / effectiveTo 的合法性, **so that** 避免无效时间窗。

**Acceptance Criteria** (EARS 语法):

- WHEN 保存 Brand / Series / Platform 且 effectiveFrom 不为空且 effectiveTo 不为空 IF effectiveFrom > effectiveTo THEN THE SYSTEM SHALL 返回错误码 807004 并拒绝保存
- WHEN 保存 Brand / Series / Platform 且 effectiveFrom 为空或 effectiveTo 为空 THEN THE SYSTEM SHALL 允许保存（单边或无限制）

### 域 3: 对外分发 - Kafka 事件（基于 Outbox Pattern）

#### US-006: Brand 事件发布

**As a** System, **I want** 在 Brand 发布（首次激活或更新）时通过事务性发件箱写入事件，后台 Relay 任务推送到 Kafka topic `mdm.product.brand.created` / `mdm.product.brand.updated` / `mdm.product.brand.deactivated`, **so that** 下游可订阅同步且不丢消息。

**Acceptance Criteria** (EARS 语法):

- WHEN Brand 被创建且 status=ACTIVE THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=mdm.product.brand.created）
- WHEN Brand 被更新（含失效）THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=mdm.product.brand.updated 或 mdm.product.brand.deactivated）
- WHEN 事件写入 Outbox THEN THE SYSTEM SHALL 与业务变更在同一本地事务内完成
- WHEN 后台 Relay 任务扫描 Outbox THEN THE SYSTEM SHALL 按 occurredAt 顺序投递 Kafka
- WHEN Kafka 投递成功 THEN THE SYSTEM SHALL 标记 Outbox 记录为已发送（sent=true, sent_at=now()）
- WHEN Kafka 投递失败 IF 超出重试次数（默认 3 次）THEN THE SYSTEM SHALL 投递死信队列并告警
- WHEN 事件 payload 构建 THEN THE SYSTEM SHALL 包含 eventId / eventType / occurredAt / entityId / version / payload 主体字段

#### US-007: Series 事件发布

**As a** System, **I want** 同上规则发布 Series 事件（`mdm.product.series.*`），**so that** 下游同步车系数据。

**Acceptance Criteria** (EARS 语法):

- WHEN Series 被创建且 status=ACTIVE THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=mdm.product.series.created）
- WHEN Series 事件 payload 构建 THEN THE SYSTEM SHALL 包含 brandCode 字段（避免下游跨表 join）
- 其他 AC 与 US-006 共通

#### US-008: Platform 事件发布

**As a** System, **I want** 同上规则发布 Platform 事件（`mdm.product.platform.*`），**so that** 下游同步平台数据。

**Acceptance Criteria** (EARS 语法):

- WHEN Platform 被创建且 status=ACTIVE THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=mdm.product.platform.created）
- 其他 AC 与 US-006 共通

### 域 4: 对外分发 - Feign 全量快照

#### US-009: Brand 全量快照

**As a** Service-Caller, **I want** 通过 Feign 拉取 Brand 全量快照（GET /api/service/brand/v1/listAll），**so that** 下游可执行 Bootstrap 与对账。

**Acceptance Criteria** (EARS 语法):

- WHEN Service-Caller 调用 GET /api/service/brand/v1/listAll 且未传 includeInactive 参数 THEN THE SYSTEM SHALL 仅返回 status=ACTIVE 的记录
- WHEN Service-Caller 调用 GET /api/service/brand/v1/listAll 且 includeInactive=true THEN THE SYSTEM SHALL 返回所有状态的记录
- WHEN 返回结果 THEN THE SYSTEM SHALL 支持分页（page / size 参数）
- WHEN Feign 调用失败 THEN THE SYSTEM SHALL 通过 FallbackFactory 返回空列表并记录日志

#### US-010: Series 全量快照

**As a** Service-Caller, **I want** 通过 Feign 拉取 Series 全量快照（GET /api/service/series/v1/listAll，支持 brandCode 过滤），**so that** 下游可执行 Bootstrap 与对账。

**Acceptance Criteria** (EARS 语法):

- WHEN Service-Caller 调用 GET /api/service/series/v1/listAll 且传 brandCode 参数 THEN THE SYSTEM SHALL 仅返回 brandCode 匹配的记录
- 其他 AC 与 US-009 共通

#### US-011: Platform 全量快照

**As a** Service-Caller, **I want** 通过 Feign 拉取 Platform 全量快照（GET /api/service/platform/v1/listAll），**so that** 下游可执行 Bootstrap 与对账。

**Acceptance Criteria** (EARS 语法):

- 与 US-009 共通

#### US-012: 按 code 单点查询

**As a** Service-Caller, **I want** 通过 Feign 按 code 单点查询（GET /api/service/brand/v1/{code} 等），**so that** 下游可补查未同步实体。

**Acceptance Criteria** (EARS 语法):

- WHEN Service-Caller 调用 GET /api/service/{entity}/v1/{code} 且记录存在且 status=ACTIVE THEN THE SYSTEM SHALL 返回实体详情
- WHEN Service-Caller 调用 GET /api/service/{entity}/v1/{code} 且记录不存在或 status≠ACTIVE THEN THE SYSTEM SHALL 返回 404 或空
- WHEN Feign 调用失败 THEN THE SYSTEM SHALL 通过 FallbackFactory 返回 null 并记录日志

## 4. Constraints & Assumptions

### 业务约束

- **横向治理层定位**：edd-mdm 是横向主数据治理层，不替代业务系统主权
- **单一权威源**：首期只支持单一权威源（由 edd-mdm 内部 CRUD 维护），不实施多源合并
- **VMD 对齐**：架构风格与 VMD 项目保持一致

### 前置条件

- VMD 项目已完成品牌 / 车系 / 平台主数据的本地投影副本改造（引用 MDM 数据）
- 下游系统（订单、销售配置器、BI）已具备消费 Kafka 事件或调用 Feign 接口的能力

### 角色定义

- **MDM-User**：MDM 后台运营 / 工程师，持有 `mdm:product:*` 权限点
- **Service-Caller**：内部微服务（VMD / 订单 / 销售配置器等）通过 Feign 或 Kafka 订阅消费 MDM 数据
- **System**：edd-mdm 自身后台异步流程（事件发布、Outbox Relay 等）

## 5. Out of Scope

| 编号 | 内容 | 原因 |
|------|------|------|
| OS-1 | VMD 的 VIN / 零部件 / 生命周期 / 车型 / 基础车型 / 生产配置 / 特征族 | 仍由 VMD 持有 |
| OS-2 | Customer / Material / Employee / Location MDM 子域 | 未来扩展 |
| OS-3 | Manufacturer / Supplier | 留待后续 CR |
| OS-4 | 多源 Golden Record 合并 | 首期只支持单一权威源 |
| OS-5 | 数据质量打分引擎 | 未来扩展 |
| OS-6 | 跨系统 ID 映射表 | 留待后续 CR |
| OS-7 | 审批工作流 | 留待后续 CR；首期 CRUD 即发布 |
| OS-8 | 对账机制 | 留待后续 CR |

## 6. Changelog

| Date | Change ID | Type | Description |
|------|-----------|------|-------------|
| 2026-05-26 | CR-001 | Added | 首版产出：建立 Product MDM 子域（Brand / Series / Platform）需求文档 |
