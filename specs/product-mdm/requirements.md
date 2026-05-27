# Product MDM 子域 - Requirements

## 1. Overview

edd-mdm 是企业数字底座领域的横向微服务，负责承载产品树主数据的 Golden Record。本 feature 建立 Product MDM 子域，承接原 VMD 项目侧的产品树主数据 SSOT 上移至 edd-mdm，实现主数据统一治理与分发能力。

产品树主数据覆盖以下 8 类实体：
- **已落地**（CR-001 / CR-002 / CR-003）：Brand（品牌）、CarLine（车系）、Platform（平台）
- **本期纳入**（CR-004）：Model（车型）、Variant（版本）、Configuration（配置）、Option Family（选项族）、Option Code（选项码）

主数据的来源分为两类：(1) **本地维护**：由 MDM-User 通过后台直接 CRUD 录入；(2) **上游系统接入**：由具备业务源头权威的上游系统（如 PLM / DMS / 集团主数据平台 / CPQ）通过 Kafka 消息或 Feign / HTTP 接口推送至 edd-mdm，由 edd-mdm 统一治理、版本化并对外分发。本 feature 同时支持上述两类来源，并在主数据上记录来源系统、来源 ID、来源版本、接收通道与接收时间等信息以支持幂等校验、冲突裁决与全链路审计。

**业务属性**：横向  
**领域**：企业数字底座  
**微服务名**：edd-mdm

## 2. Background & Goals

### 背景

当前品牌 / 车系 / 平台主数据由 VMD 项目维护，随着车联网平台业务扩展，多个下游系统（订单、销售配置器、BI 等）都需要消费这些主数据，但缺乏统一的主数据管理入口。为解决主数据分散、质量不一致、分发困难等问题，需要将产品树主数据的 SSOT 上移至横向的 edd-mdm 服务。

### 目标

- **G1**：建立 edd-mdm 作为 Brand / CarLine / Platform 三类主数据的 SSOT（Single Source of Truth）
- **G2**：提供统一的主数据 CRUD 维护能力，支持 MDM-User 后台管理
- **G3**：实现版本控制与生效期管理，支持历史追溯与审计
- **G4**：通过事务性发件箱（Outbox Pattern）实现可靠的事件分发，支持下游订阅同步
- **G5**：提供 Feign 全量快照接口，支持下游 Bootstrap 与对账
- **G6**：遵循 DDD 设计原则，保持与 VMD 项目架构风格一致
- **G7**：支持上游系统通过 Kafka 消息或 Feign / HTTP 接口推送 Brand / CarLine / Platform 主数据至 edd-mdm，并在主数据与对外事件 payload 中记录来源系统、来源 ID、来源版本、接收通道、接收时间等字段，支持审计与下游识别
- **G8**：对上游推送的数据实施 schema 校验、来源鉴权、权威源校验、幂等校验与冲突裁决，保证 Golden Record 的一致性、可追溯性与可监控性
- **G9**：将产品树底层 5 类主数据（Model / Variant / Configuration / Option Family / Option Code）纳入 edd-mdm 统一治理，建立完整的产品树 SSOT，消除上下游对产品树底层数据的冗余维护
- **G10**：提供按选项码组合反查配置的能力，支撑订单 / 销售域的核心高频业务场景

### 非目标（明确不做）

- **NG1**：不接管 VMD 的车辆实例（VIN）/ 零部件 / 生命周期 / 制造厂商（Manufacturer）/ 供应商（Supplier）等领域
- **NG2**：不接管 Customer / Material / Employee / Location 等其他 MDM 子域
- **NG3**：不实施跨系统主数据的字段级合并裁决（multi-source field-level merge）。首期采用 **单一权威源（Single Authoritative Source）** 策略：每条主数据在任一时刻仅由配置中指定的一个权威源（LOCAL 或某个上游系统）负责写入；非权威源的推送将被拒绝或仅记录审计日志，不更新主表
- **NG4**：不实施数据质量打分引擎
- **NG5**：不实施跨系统 ID 映射表
- **NG6**：不实施审批工作流（首期 CRUD 即发布）
- **NG7**：不实施 Manufacturer / Supplier（留待后续 CR）
- **NG8**：不实施对账机制（留待后续 CR）

## 3. Glossary（术语表）

| 英文 | 中文 | 行业对齐说明 |
|------|------|-------------|
| Brand | 品牌 | 已在 CR-001 落地 |
| Carline | 车系 | 已在 CR-002 落地，等价于 SAP Model Family |
| Platform | 平台 | 已在 CR-003 落地 |
| Model | 车型 | CR-004 纳入。指车系下的代次/年款层（如"2024 款理想 L9"），对应 SAP Vehicle Model |
| Variant | 版本 | CR-004 纳入。指车型下的差异化版本（如 Pro / Max / Ultra、Long Range / Performance），等价于 SAP / PLM 通用语境下的 Variant / Trim |
| Configuration | 配置 | CR-004 纳入。指具体的可生产规格组合，等价于 SAP Sales Configuration |
| Option Family | 选项族 | CR-004 纳入。等价于 SAP Characteristic Family、GM RPO Family、VW PR-Familie |
| Option Code | 选项码 | CR-004 纳入。等价于 SAP Characteristic Value、GM RPO Code、VW PR-Nummer |

> **语义说明**："选项族 / 选项码"在 MDM 体系内承载**两种语义**：既可代表"版本/配置的标配识别特征"，也可代表"客户选装项"。MDM 不区分这两种语义，由消费方根据上下文使用。

## 4. User Stories

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

#### US-002: CRUD CarLine

**As a** MDM-User, **I want** CRUD CarLine（含按 brandCode 过滤、按 status 过滤），**so that** 管理车系 Golden Record。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 CarLine 且 code 唯一且 brandCode 指向已存在且 status=ACTIVE 的 Brand THEN THE SYSTEM SHALL 持久化 CarLine 记录并设置 version=1，并自动填充 create_by（当前认证用户）、create_time（当前时间）、modify_by、modify_time
- WHEN MDM-User 创建 CarLine 且 code 已存在 THEN THE SYSTEM SHALL 返回错误码 807001 并拒绝创建
- WHEN MDM-User 创建 CarLine 且 brandCode 指向不存在或 status≠ACTIVE 的 Brand THEN THE SYSTEM SHALL 返回错误码 807005 并拒绝创建
- WHEN MDM-User 更新 CarLine 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，并自动填充 modify_by（当前认证用户）、modify_time（当前时间）
- WHEN MDM-User 失效 CarLine 且 status=ACTIVE THEN THE SYSTEM SHALL 设置 status=INACTIVE 和 effectiveTo=now() 并自增 version，并自动填充 modify_by、modify_time
- WHEN MDM-User 删除 CarLine 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录
- WHEN MDM-User 查询 CarLine 列表 THEN THE SYSTEM SHALL 支持按 brandCode 和 status 过滤
- IF CarLine 的 effectiveFrom > effectiveTo THEN THE SYSTEM SHALL 返回错误码 807004 并拒绝保存

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

- WHEN Brand / CarLine / Platform 发生创建、更新、失效操作 THEN THE SYSTEM SHALL 在 history 表插入一条完整快照记录
- WHEN history 表插入快照 THEN THE SYSTEM SHALL 记录变更时间、操作人、变更前后的 version
- WHEN 查询历史版本 THEN THE SYSTEM SHALL 按 entityId 和 version 降序返回快照列表
- WHEN MDM-User 通过后台查询品牌历史版本 THEN THE SYSTEM SHALL 提供 GET /api/mpt/mdm/brand/v1/{code}/history 接口，按 version 降序返回快照列表，响应包含来源字段（sourceSystem / sourceId / sourceVersion / ingestionChannel / ingestionTime）
- WHEN MDM-User 通过后台查询车系历史版本 THEN THE SYSTEM SHALL 提供 GET /api/mpt/mdm/carline/v1/{code}/history 接口，按 version 降序返回快照列表，响应包含来源字段
- WHEN MDM-User 通过后台查询平台历史版本 THEN THE SYSTEM SHALL 提供 GET /api/mpt/mdm/platform/v1/{code}/history 接口，按 version 降序返回快照列表，响应包含来源字段
- WHEN 调用历史版本查询接口且 code 不存在 THEN THE SYSTEM SHALL 返回错误码 807002 并拒绝查询

#### US-005: 生效期合法性校验

**As a** System, **I want** 校验 effectiveFrom / effectiveTo 的合法性, **so that** 避免无效时间窗。

**Acceptance Criteria** (EARS 语法):

- WHEN 保存 Brand / CarLine / Platform 且 effectiveFrom 不为空且 effectiveTo 不为空 IF effectiveFrom > effectiveTo THEN THE SYSTEM SHALL 返回错误码 807004 并拒绝保存
- WHEN 保存 Brand / CarLine / Platform 且 effectiveFrom 为空或 effectiveTo 为空 THEN THE SYSTEM SHALL 允许保存（单边或无限制）

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

#### US-007: CarLine 事件发布

**As a** System, **I want** 同上规则发布 CarLine 事件（`mdm.product.series.*`），**so that** 下游同步车系数据。

**Acceptance Criteria** (EARS 语法):

- WHEN CarLine 被创建且 status=ACTIVE THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=mdm.product.series.created）
- WHEN CarLine 事件 payload 构建 THEN THE SYSTEM SHALL 包含 brandCode 字段（避免下游跨表 join）
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

#### US-010: CarLine 全量快照

**As a** Service-Caller, **I want** 通过 Feign 拉取 CarLine 全量快照（GET /api/service/carline/v1/listAll，支持 brandCode 过滤），**so that** 下游可执行 Bootstrap 与对账。

**Acceptance Criteria** (EARS 语法):

- WHEN Service-Caller 调用 GET /api/service/carline/v1/listAll 且传 brandCode 参数 THEN THE SYSTEM SHALL 仅返回 brandCode 匹配的记录
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

### 域 5: 上游系统数据接入

#### US-013: 接收上游系统推送的主数据（Kafka 通道）

**As an** Upstream-System, **I want** 通过 Kafka 向 edd-mdm 推送 Brand / CarLine / Platform 主数据，**so that** 由 edd-mdm 作为 Golden Record 统一治理与分发。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 订阅约定的上游 Topic（命名约定：upstream.<sourceSystem>.product.brand / .series / .platform）消费推送消息
- THE SYSTEM SHALL 校验消息 schema 合法性，必填字段包括：sourceSystem、sourceId、sourceVersion、entityType、occurredAt、payload
- WHEN 上游消息 schema 非法或必填字段缺失 THEN THE SYSTEM SHALL 拒绝该消息、记录错误日志并投递至死信队列，返回错误码 807010
- WHEN 上游消息 schema 合法 THEN THE SYSTEM SHALL 依次执行：来源鉴权 → 权威源校验（见 US-017） → 幂等校验（见 US-016） → 业务字段校验（与本地维护一致）
- WHEN 所有校验通过 THEN THE SYSTEM SHALL 在同一本地事务内完成主表 upsert、history 写入与 outbox 事件写入，并自动填充审计字段（create_by/modify_by 使用配置的 sourceSystem 标识；create_time/modify_time 使用服务端当前时间）
- WHEN 消息处理成功 THEN THE SYSTEM SHALL 提交 Kafka offset
- WHEN 消息处理失败（非幂等丢弃，非业务校验拒绝）THEN THE SYSTEM SHALL 按默认 3 次重试，超出后投递死信队列并告警
- THE SYSTEM SHALL 在 mdm_ingestion_log 中记录每一次消息的处理结果（见 US-018）

#### US-014: 接收上游系统推送的主数据（Feign / HTTP 通道）

**As an** Upstream-System, **I want** 通过 Feign / HTTP 接口向 edd-mdm 同步推送 Brand / CarLine / Platform 主数据，**so that** 在不具备 Kafka 通道的场景下完成主数据接入。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 提供以下接收接口：
    - POST /api/upstream/mdm/brand/v1/ingest
    - POST /api/upstream/mdm/carline/v1/ingest
    - POST /api/upstream/mdm/platform/v1/ingest
- WHEN Upstream-System 调用 ingest 接口 THEN THE SYSTEM SHALL 从请求头（如 X-Source-System）或请求体提取 sourceSystem 标识并完成鉴权（API Key / OAuth2）
- WHEN 鉴权失败、来源未注册或来源被禁用 THEN THE SYSTEM SHALL 返回错误码 807011 并拒绝接入
- WHEN 鉴权通过 THEN THE SYSTEM SHALL 复用 US-013 同一接入处理链路（schema 校验 → 权威源校验 → 幂等校验 → 业务校验 → upsert + history + outbox）
- WHEN 接入处理成功 THEN THE SYSTEM SHALL 返回 200 OK 与接入结果摘要（entityId、新 version、operationType：CREATED/UPDATED/DUPLICATED/REJECTED）
- WHEN 接入处理失败 THEN THE SYSTEM SHALL 返回对应错误码（807010 ~ 807013）与错误描述，由上游系统决定是否重试

#### US-015: 数据来源记录

**As a** System, **I want** 在主数据上记录来源信息, **so that** 支持审计、追溯、裁决与下游识别。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 在 Brand / CarLine / Platform 主表及对应 history 表上扩展以下字段：
    - source_system：来源系统编码（LOCAL / PLM / DMS / GROUP_MDM 等），不可为空
    - source_id：上游系统中的业务主键（本地维护时与本地 code 相同或为空，由 LOCAL 适配器写入）
    - source_version：上游系统中的版本号（本地维护时可为空）
    - ingestion_channel：接入通道（LOCAL / KAFKA / FEIGN），不可为空
    - ingestion_time：最近一次接收/变更时间，不可为空
    - source_payload_hash：可选，最近一次接入消息体的哈希值，便于排查与冲突识别
- WHEN 本地维护写入 THEN THE SYSTEM SHALL 设置 source_system=LOCAL、ingestion_channel=LOCAL、ingestion_time=now()
- WHEN 上游接入写入 THEN THE SYSTEM SHALL 按消息携带的 sourceSystem / sourceId / sourceVersion 和实际接入通道（KAFKA/FEIGN）填充对应字段
- WHEN 对外发布 Kafka 事件 THEN THE SYSTEM SHALL 在 payload 中携带来源字段（sourceSystem / sourceId / sourceVersion / ingestionChannel / ingestionTime）以便下游识别与对账
- WHEN MDM-User 通过后台查询主数据详情或历史版本 THEN THE SYSTEM SHALL 在响应中展示来源字段

#### US-016: 上游消息幂等处理

**As a** System, **I want** 对上游推送实施幂等校验, **so that** 重复消息不会造成重复写入或版本回退。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 以 (source_system, source_id) 作为上游记录的逻辑主键定位本地记录
- WHEN 上游消息到达且本地不存在对应记录 THEN THE SYSTEM SHALL 视为新增并执行写入
- WHEN 上游消息到达且本地已存在对应记录 IF 上游 sourceVersion > 本地 source_version THEN THE SYSTEM SHALL 执行更新（version 自增、写 history、写 outbox）
- WHEN 上游消息到达且本地已存在对应记录 IF 上游 sourceVersion < 本地 source_version THEN THE SYSTEM SHALL 视为过期消息，丢弃并记录 INFO 日志，不更新主表，不写 outbox，不返回错误
- WHEN 上游消息到达且本地已存在对应记录 IF 上游 sourceVersion = 本地 source_version 且 source_payload_hash 一致 THEN THE SYSTEM SHALL 视为重复消息，丢弃并记录 INFO 日志
- WHEN 上游消息到达且本地已存在对应记录 IF 上游 sourceVersion = 本地 source_version 且 source_payload_hash 不一致 THEN THE SYSTEM SHALL 视为同版本不一致冲突，记录告警日志并返回错误码 807012，由人工或权威源策略干预

#### US-017: 权威源配置与冲突裁决

**As a** MDM-Admin, **I want** 为每个实体（Brand / CarLine / Platform）配置权威源, **so that** 同一实体仅由唯一权威源写入，避免多源覆盖。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 提供权威源配置，维度包括：entityType（BRAND / SERIES / PLATFORM）、code 模式（精确 code 或通配 *）、authoritativeSource（LOCAL / PLM / DMS / ...）、conflictPolicy（REJECT / AUDIT_ONLY）
- THE SYSTEM SHALL 支持通过 Nacos 配置或配置表维护，并支持热更新
- WHEN 接收到上游消息或本地维护请求 IF 当前来源 = 配置的 authoritativeSource THEN THE SYSTEM SHALL 正常写入
- WHEN 接收到上游消息或本地维护请求 IF 当前来源 != 配置的 authoritativeSource 且 conflictPolicy=REJECT THEN THE SYSTEM SHALL 拒绝写入，返回错误码 807013，并记录告警日志
- WHEN 接收到上游消息或本地维护请求 IF 当前来源 != 配置的 authoritativeSource 且 conflictPolicy=AUDIT_ONLY THEN THE SYSTEM SHALL 仅在 mdm_ingestion_log 与 history 表中记录该次尝试，不更新主表，不写 outbox
- WHEN MDM-Admin 切换某实体的权威源（如 LOCAL 切换到 PLM）THEN THE SYSTEM SHALL 记录权威源变更审计（操作人、时间、entityType + code、原 authoritativeSource、新 authoritativeSource）
- IF 未匹配到任何精确配置 THEN THE SYSTEM SHALL 回退到 entityType 级默认配置；如仍未匹配，回退到全局默认（默认 authoritativeSource=LOCAL、conflictPolicy=REJECT）

#### US-018: 上游接入审计与监控

**As a** MDM-Admin, **I want** 查看上游接入处理记录与监控指标, **so that** 排查问题并保障数据质量。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 在独立审计表 mdm_ingestion_log 中记录每一次接入处理：
    - messageId（Kafka offset 或 HTTP 请求 ID）、sourceSystem、sourceId、sourceVersion、entityType、ingestionChannel、receivedAt、processedAt、status（SUCCESS / DUPLICATED / OUTDATED / REJECTED / FAILED）、errorCode、errorMessage、payloadHash
- THE SYSTEM SHALL 暴露 Prometheus 指标，按 sourceSystem / entityType / status 维度统计接入总量、成功率、失败率、平均处理耗时、消息积压量
- WHEN 某 sourceSystem + entityType 维度连续失败次数超过阈值（默认 10 次 / 5 分钟，可通过 Nacos 配置）THEN THE SYSTEM SHALL 触发告警通知
- THE SYSTEM SHALL 提供 MPT 端查询接口 GET /api/mpt/mdm/ingestion/v1/log，支持按 sourceSystem / entityType / status / 时间窗 过滤分页查询
- THE SYSTEM SHALL 提供 MPT 端查询接口 GET /api/mpt/mdm/ingestion/v1/{messageId} 查询单条接入处理的明细

### 域 6: 车型（Model）管理

#### US-019: CRUD Model

**As a** MDM-User, **I want** CRUD Model（含按车系、平台组合过滤），**so that** 管理车型 Golden Record。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Model 且 code 唯一且 carlineCode 指向已存在且 status=ACTIVE 的 CarLine 且 platformCode 指向已存在且 status=ACTIVE 的 Platform THEN THE SYSTEM SHALL 持久化 Model 记录并设置 version=1，并自动填充 create_by、create_time、modify_by、modify_time
- WHEN MDM-User 创建 Model 且 code 已存在 THEN THE SYSTEM SHALL 拒绝创建并返回业务错误
- WHEN MDM-User 创建 Model 且 carlineCode 指向不存在或 status≠ACTIVE 的 CarLine THEN THE SYSTEM SHALL 拒绝创建并返回引用完整性错误
- WHEN MDM-User 创建 Model 且 platformCode 指向不存在或 status≠ACTIVE 的 Platform THEN THE SYSTEM SHALL 拒绝创建并返回引用完整性错误
- WHEN MDM-User 更新 Model 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，并自动填充 modify_by、modify_time
- WHEN MDM-User 失效 Model 且 status=ACTIVE THEN THE SYSTEM SHALL 设置 status=INACTIVE 和 effectiveTo=now() 并自增 version，并自动填充 modify_by、modify_time
- WHEN MDM-User 删除 Model 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录
- WHEN MDM-User 删除 Model 且 status≠DRAFT THEN THE SYSTEM SHALL 拒绝删除并返回业务错误
- WHEN MDM-User 删除 Model 且存在下层 Variant 引用该 Model THEN THE SYSTEM SHALL 拒绝删除并返回引用依赖错误
- WHEN MDM-User 查询 Model 列表 THEN THE SYSTEM SHALL 支持按 carlineCode、platformCode、status 任意组合过滤
- IF Model 的 effectiveFrom > effectiveTo THEN THE SYSTEM SHALL 拒绝保存并返回业务错误

### 域 7: 版本（Variant）管理

#### US-020: CRUD Variant

**As a** MDM-User, **I want** CRUD Variant（含按平台、车系、车型任意组合过滤），**so that** 管理版本 Golden Record。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Variant 且 code 唯一且 modelCode 指向已存在且 status=ACTIVE 的 Model THEN THE SYSTEM SHALL 持久化 Variant 记录并设置 version=1，并自动填充 create_by、create_time、modify_by、modify_time
- WHEN MDM-User 创建 Variant 且 code 已存在 THEN THE SYSTEM SHALL 拒绝创建并返回业务错误
- WHEN MDM-User 创建 Variant 且 modelCode 指向不存在或 status≠ACTIVE 的 Model THEN THE SYSTEM SHALL 拒绝创建并返回引用完整性错误
- WHEN MDM-User 更新 Variant 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，并自动填充 modify_by、modify_time
- WHEN MDM-User 失效 Variant 且 status=ACTIVE THEN THE SYSTEM SHALL 设置 status=INACTIVE 和 effectiveTo=now() 并自增 version，并自动填充 modify_by、modify_time
- WHEN MDM-User 删除 Variant 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录
- WHEN MDM-User 删除 Variant 且 status≠DRAFT THEN THE SYSTEM SHALL 拒绝删除并返回业务错误
- WHEN MDM-User 删除 Variant 且存在下层 Configuration 引用该 Variant THEN THE SYSTEM SHALL 拒绝删除并返回引用依赖错误
- WHEN MDM-User 查询 Variant 列表 THEN THE SYSTEM SHALL 支持按 modelCode、carlineCode、platformCode、status 任意组合过滤
- IF Variant 的 effectiveFrom > effectiveTo THEN THE SYSTEM SHALL 拒绝保存并返回业务错误

#### US-021: Variant 绑定 / 解绑 Option Code

**As a** MDM-User, **I want** 为 Variant 绑定或解绑 Option Code，**so that** 标识该版本的标配/识别特征。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 为 Variant 绑定 Option Code 且该 Option Code 存在且 status=ACTIVE THEN THE SYSTEM SHALL 建立绑定关系并自增 Variant 的 version
- WHEN MDM-User 为 Variant 绑定 Option Code 且该 Option Code 不存在或 status≠ACTIVE THEN THE SYSTEM SHALL 拒绝绑定并返回引用完整性错误
- WHEN MDM-User 为 Variant 绑定 Option Code 且该 Variant 下已存在同一 Option Family 的另一个 Option Code THEN THE SYSTEM SHALL 拒绝绑定并返回互斥约束错误
- WHEN MDM-User 为 Variant 解绑 Option Code THEN THE SYSTEM SHALL 移除绑定关系并自增 Variant 的 version
- WHEN MDM-User 为 Variant 解绑 Option Code 且该绑定关系不存在 THEN THE SYSTEM SHALL 拒绝解绑并返回业务错误

### 域 8: 配置（Configuration）管理

#### US-022: CRUD Configuration

**As a** MDM-User, **I want** CRUD Configuration（含按版本过滤），**so that** 管理配置 Golden Record。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Configuration 且 code 唯一且 variantCode 指向已存在且 status=ACTIVE 的 Variant THEN THE SYSTEM SHALL 持久化 Configuration 记录并设置 version=1，并自动填充 create_by、create_time、modify_by、modify_time
- WHEN MDM-User 创建 Configuration 且 code 已存在 THEN THE SYSTEM SHALL 拒绝创建并返回业务错误
- WHEN MDM-User 创建 Configuration 且 variantCode 指向不存在或 status≠ACTIVE 的 Variant THEN THE SYSTEM SHALL 拒绝创建并返回引用完整性错误
- WHEN MDM-User 更新 Configuration 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，并自动填充 modify_by、modify_time
- WHEN MDM-User 失效 Configuration 且 status=ACTIVE THEN THE SYSTEM SHALL 设置 status=INACTIVE 和 effectiveTo=now() 并自增 version，并自动填充 modify_by、modify_time
- WHEN MDM-User 删除 Configuration 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录
- WHEN MDM-User 删除 Configuration 且 status≠DRAFT THEN THE SYSTEM SHALL 拒绝删除并返回业务错误
- WHEN MDM-User 查询 Configuration 列表 THEN THE SYSTEM SHALL 支持按 variantCode、status 过滤
- IF Configuration 的 effectiveFrom > effectiveTo THEN THE SYSTEM SHALL 拒绝保存并返回业务错误

#### US-023: Configuration 绑定 / 解绑 Option Code

**As a** MDM-User, **I want** 为 Configuration 绑定或解绑 Option Code，**so that** 描述该配置的全量特征规格。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 为 Configuration 绑定 Option Code 且该 Option Code 存在且 status=ACTIVE THEN THE SYSTEM SHALL 建立绑定关系并自增 Configuration 的 version
- WHEN MDM-User 为 Configuration 绑定 Option Code 且该 Option Code 不存在或 status≠ACTIVE THEN THE SYSTEM SHALL 拒绝绑定并返回引用完整性错误
- WHEN MDM-User 为 Configuration 绑定 Option Code 且该 Configuration 下已存在同一 Option Family 的另一个 Option Code THEN THE SYSTEM SHALL 拒绝绑定并返回互斥约束错误
- WHEN MDM-User 为 Configuration 解绑 Option Code THEN THE SYSTEM SHALL 移除绑定关系并自增 Configuration 的 version
- WHEN MDM-User 为 Configuration 解绑 Option Code 且该绑定关系不存在 THEN THE SYSTEM SHALL 拒绝解绑并返回业务错误

#### US-024: 按 Option Code 组合反查 Configuration

**As a** Service-Caller, **I want** 给定一组 Option Code，反查匹配的 Configuration 列表，**so that** 支撑订单/销售域根据客户选择的选项码组合定位可生产配置。

**Acceptance Criteria** (EARS 语法):

- WHEN Service-Caller 提供一组 Option Code 进行反查 THEN THE SYSTEM SHALL 返回所有绑定的 Option Code 集合完全包含所提供组合的 Configuration 列表（包含匹配）
- WHEN Service-Caller 提供一组 Option Code 进行反查且无匹配结果 THEN THE SYSTEM SHALL 返回空列表
- WHEN Service-Caller 提供一组 Option Code 进行反查 THEN THE SYSTEM SHALL 仅返回 status=ACTIVE 的 Configuration
- WHEN Service-Caller 提供空的 Option Code 集合 THEN THE SYSTEM SHALL 拒绝查询并返回参数校验错误

### 域 9: 选项族（Option Family）与选项码（Option Code）管理

#### US-025: CRUD Option Family

**As a** MDM-User, **I want** CRUD Option Family，**so that** 管理选项的分组维度。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Option Family 且 code 唯一 THEN THE SYSTEM SHALL 持久化 Option Family 记录并设置 version=1，并自动填充 create_by、create_time、modify_by、modify_time
- WHEN MDM-User 创建 Option Family 且 code 已存在 THEN THE SYSTEM SHALL 拒绝创建并返回业务错误
- WHEN MDM-User 更新 Option Family 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，并自动填充 modify_by、modify_time
- WHEN MDM-User 失效 Option Family 且 status=ACTIVE THEN THE SYSTEM SHALL 设置 status=INACTIVE 和 effectiveTo=now() 并自增 version，并自动填充 modify_by、modify_time
- WHEN MDM-User 删除 Option Family 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录
- WHEN MDM-User 删除 Option Family 且 status≠DRAFT THEN THE SYSTEM SHALL 拒绝删除并返回业务错误
- WHEN MDM-User 删除 Option Family 且存在下层 Option Code 引用该 Option Family THEN THE SYSTEM SHALL 拒绝删除并返回引用依赖错误

#### US-026: CRUD Option Code

**As a** MDM-User, **I want** CRUD Option Code，**so that** 管理选项族下的具体取值。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Option Code 且 code 唯一且 optionFamilyCode 指向已存在且 status=ACTIVE 的 Option Family THEN THE SYSTEM SHALL 持久化 Option Code 记录并设置 version=1，并自动填充 create_by、create_time、modify_by、modify_time
- WHEN MDM-User 创建 Option Code 且 code 已存在 THEN THE SYSTEM SHALL 拒绝创建并返回业务错误
- WHEN MDM-User 创建 Option Code 且 optionFamilyCode 指向不存在或 status≠ACTIVE 的 Option Family THEN THE SYSTEM SHALL 拒绝创建并返回引用完整性错误
- WHEN MDM-User 更新 Option Code 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，并自动填充 modify_by、modify_time
- WHEN MDM-User 失效 Option Code 且 status=ACTIVE THEN THE SYSTEM SHALL 设置 status=INACTIVE 和 effectiveTo=now() 并自增 version，并自动填充 modify_by、modify_time
- WHEN MDM-User 删除 Option Code 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录
- WHEN MDM-User 删除 Option Code 且 status≠DRAFT THEN THE SYSTEM SHALL 拒绝删除并返回业务错误
- WHEN MDM-User 删除 Option Code 且存在 Variant 或 Configuration 绑定该 Option Code THEN THE SYSTEM SHALL 拒绝删除并返回引用依赖错误

### 域 10: CR-004 新增实体的跨切面能力

#### US-027: 5 类新实体的事件发布

**As a** System, **I want** 在 Model / Variant / Configuration / Option Family / Option Code 发生变更时通过事务性发件箱发布事件，**so that** 下游可订阅同步产品树底层主数据变更。

**Acceptance Criteria** (EARS 语法):

- WHEN Model / Variant / Configuration / Option Family / Option Code 被创建且 status=ACTIVE THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType 含实体类型与操作类型）
- WHEN Model / Variant / Configuration / Option Family / Option Code 被更新（含失效）THEN THE SYSTEM SHALL 写入 Outbox 事件
- WHEN 事件写入 Outbox THEN THE SYSTEM SHALL 与业务变更在同一本地事务内完成
- WHEN 事件 payload 构建 THEN THE SYSTEM SHALL 包含 eventId / eventType / occurredAt / entityId / version / payload 主体字段及来源字段
- WHEN Variant 或 Configuration 的 Option Code 绑定关系发生变更 THEN THE SYSTEM SHALL 写入对应实体的 updated 事件

#### US-028: 5 类新实体的全量快照消费

**As a** Service-Caller, **I want** 通过 Feign 拉取 Model / Variant / Configuration / Option Family / Option Code 的全量快照，**so that** 下游可执行 Bootstrap 与对账。

**Acceptance Criteria** (EARS 语法):

- WHEN Service-Caller 调用全量快照接口且未传 includeInactive 参数 THEN THE SYSTEM SHALL 仅返回 status=ACTIVE 的记录
- WHEN Service-Caller 调用全量快照接口且 includeInactive=true THEN THE SYSTEM SHALL 返回所有状态的记录
- WHEN 返回结果 THEN THE SYSTEM SHALL 支持分页
- WHEN Service-Caller 按 code 单点查询且记录存在且 status=ACTIVE THEN THE SYSTEM SHALL 返回实体详情
- WHEN Service-Caller 按 code 单点查询且记录不存在或 status≠ACTIVE THEN THE SYSTEM SHALL 返回空
- WHEN Feign 调用失败 THEN THE SYSTEM SHALL 通过 FallbackFactory 返回空列表或 null 并记录日志

#### US-029: 5 类新实体的历史版本追溯

**As a** System, **I want** 每次 Model / Variant / Configuration / Option Family / Option Code 发生变更时生成历史版本快照，**so that** 支持回溯与审计。

**Acceptance Criteria** (EARS 语法):

- WHEN Model / Variant / Configuration / Option Family / Option Code 发生创建、更新、失效操作 THEN THE SYSTEM SHALL 在对应 history 表插入一条完整快照记录
- WHEN history 表插入快照 THEN THE SYSTEM SHALL 记录变更时间、操作人、变更前后的 version
- WHEN 查询历史版本 THEN THE SYSTEM SHALL 按 entityId 和 version 降序返回快照列表，响应包含来源字段
- WHEN Variant 或 Configuration 的 Option Code 绑定关系发生变更 THEN THE SYSTEM SHALL 在对应实体的 history 表中记录快照

#### US-030: 5 类新实体的上游接入扩展

**As an** Upstream-System, **I want** 通过 Kafka 或 Feign / HTTP 向 edd-mdm 推送 Model / Variant / Configuration / Option Family / Option Code 主数据，**so that** 由 edd-mdm 统一治理产品树底层主数据。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 复用 US-013 / US-014 已建立的上游接入处理链路（schema 校验 → 来源鉴权 → 权威源校验 → 幂等校验 → 业务校验 → upsert + history + outbox）
- THE SYSTEM SHALL 对 5 类新实体的上游推送执行与本地维护一致的引用完整性校验
- THE SYSTEM SHALL 对 5 类新实体的上游推送执行与本地维护一致的互斥约束校验（Variant / Configuration 下同一 Option Family 最多一个 Option Code）
- THE SYSTEM SHALL 在 mdm_ingestion_log 中记录 5 类新实体的每一次接入处理结果
- THE SYSTEM SHALL 暴露 5 类新实体的 Prometheus 接入监控指标，与现有指标维度一致

## 5. Constraints & Assumptions

### 业务约束

- **横向治理层定位**：edd-mdm 是横向主数据治理层，不替代业务系统主权
- **单一权威源**：首期只支持单一权威源（由 edd-mdm 内部 CRUD 维护），不实施多源合并
- **VMD 对齐**：架构风格与 VMD 项目保持一致
- **产品树层级关系**：Brand → CarLine → Model → Variant → Configuration，上层失效/删除时须校验下层无引用
- **Model 双归属**：Model 同时归属于 CarLine 和 Platform，创建时两者均须为 ACTIVE 状态
- **Option Family 互斥**：同一 Variant 下，同一 Option Family 最多只能绑定一个 Option Code；同一 Configuration 下同理
- **Configuration 归属**：Configuration 必须归属于一个 Variant
- **Option Code 归属**：Option Code 必须归属于一个 Option Family
- **删除前置依赖**：任何实体在物理删除前，须校验不存在下层实体或绑定关系的引用

### 前置条件

- VMD 项目已完成品牌 / 车系 / 平台主数据的本地投影副本改造（引用 MDM 数据）
- 下游系统（订单、销售配置器、BI）已具备消费 Kafka 事件或调用 Feign 接口的能力

### 角色定义

- **MDM-User**：MDM 后台运营 / 工程师，持有 `mdm:product:*` 权限点
- **Service-Caller**：内部微服务（VMD / 订单 / 销售配置器等）通过 Feign 或 Kafka 订阅消费 MDM 数据
- **System**：edd-mdm 自身后台异步流程（事件发布、Outbox Relay、上游消息消费等）
- **Upstream-System**：业务源头权威的上游系统（如 PLM / DMS / 集团主数据平台），通过 Kafka 消息或 Feign / HTTP 接口推送 Brand / CarLine / Platform 主数据至 edd-mdm；每个上游系统对应唯一的 sourceSystem 编码
- **MDM-Admin**：MDM 管理员，持有 `mdm:admin:*` 权限点，负责权威源配置、上游来源注册、接入审计与监控查询等运维操作

## 6. Out of Scope

| 编号 | 内容 | 原因 |
|------|------|------|
| OS-1 | VMD 的 VIN / 零部件 / 生命周期 | 仍由 VMD 持有 |
| OS-2 | Customer / Material / Employee / Location MDM 子域 | 未来扩展 |
| OS-3 | Manufacturer / Supplier | 留待后续 CR |
| OS-4 | 字段级多源合并裁决（field-level merge） | 首期采用单一权威源策略，每条主数据由唯一权威源写入；支持多上游接入但不做字段级合并 |
| OS-5 | 数据质量打分引擎 | 未来扩展 |
| OS-6 | 跨系统 ID 映射表 | 留待后续 CR |
| OS-7 | 审批工作流 | 留待后续 CR；首期 CRUD 即发布 |
| OS-8 | 对账机制 | 留待后续 CR |
| OS-9 | 跨车型的选项码兼容矩阵（哪些选项码可用于哪些车型） | 业务复杂度高，留待后续 CR |
| OS-10 | 选项码间的依赖/排斥规则引擎（如选了 A 则必须选 B、不能选 C） | 属于销售配置器/CPQ 领域能力，不在 MDM 范围 |
| OS-11 | 选项码定价与商务属性 | 属于销售/财务域，MDM 仅管理主数据标识 |
| OS-12 | 配置与车辆实例（VIN）的关联 | 仍由 VMD 持有 |

## 7. Open Questions

| 编号 | 问题 | 影响范围 | 建议（待业务确认） |
|------|------|----------|-------------------|
| Q1 | 上层失效（如 Model → INACTIVE）时，是否级联失效下层（Variant / Configuration）？还是仅校验阻止？ | US-019 / US-020 / US-022 | 待定 |
| Q2 | Configuration 的**物理删除**应限定在何种状态？MDM 内无法感知车辆实例的引用情况。 | US-022 | 建议只允许 DRAFT 状态删除，ACTIVE / INACTIVE 仅支持失效，由业务方确认 |
| Q3 | 下层引用上层时，是否要求上层 status = ACTIVE？还是允许引用 INACTIVE 状态的上层？ | US-019 ~ US-026 | 待定 |
| Q4 | Option Code 是否需要跨车型/版本/配置的全局唯一性？还是允许在不同上下文中复用同一 Option Code？ | US-026 | 待定 |
| Q5 | Variant 与 Option Code 绑定的"同一 Option Family 互斥"约束，是否在所有 Variant 上都强制？是否存在例外场景？ | US-021 | 待定 |
| Q6 | Configuration 绑定的 Option Code 集合，是否要求"覆盖所有必选 Option Family"？还是允许部分缺失？ | US-023 | 待定 |
| Q7 | 按 Option Code 组合反查 Configuration 的匹配语义，是"精确匹配"还是"包含匹配"？是否支持模糊/通配？ | US-024 | 待定 |

## 8. Impact Analysis（业务层影响）

### VMD 项目

- VMD 侧的车型 / 版本 / 配置 / 选项族 / 选项码后台维护能力需降级为**只读**（数据来源切换为 MDM 本地投影副本）
- VMD 侧的车型 / 版本 / 配置查询能力需切换为消费 MDM 事件 + 本地副本模式（沿用 CR-010 已落地的本地投影副本模式）
- VMD 侧对应的下游迁移由 VMD-CR-011 承接

### 下游消费方

- VSO（车辆销售订单）、订单域、生产域、销售域需要扩展 Kafka 订阅范围，新增 Model / Variant / Configuration / Option Family / Option Code 5 类事件的消费
- 下游需扩展 Feign 全量快照对账范围，覆盖 5 类新实体
- 按选项码组合反查配置（US-024）为订单/销售域核心高频能力，下游需对接该查询接口

### 上游系统

- 需评估是否新增产品主数据的上游推送通道（如 PLM、CPQ、产品定义系统）
- 若存在上游系统直接维护车型/版本/配置/选项族/选项码，需完成来源注册与权威源配置

## 9. Changelog

| Date | Change ID | Type | Description |
|------|-----------|------|-------------|
| 2026-05-26 | CR-001 | Added | 首版产出：建立 Product MDM 子域（Brand / CarLine / Platform）需求文档 |
| 2026-05-26 | CR-002 | Added | 新增"域 5: 上游系统数据接入"（US-013 ~ US-018）：支持通过 Kafka / Feign 接收上游推送，新增数据来源字段（source_system / source_id / source_version / ingestion_channel / ingestion_time / source_payload_hash），新增幂等处理、权威源配置与冲突裁决（REJECT / AUDIT_ONLY）、接入审计表 mdm_ingestion_log 与监控指标；新增错误码 807010（消息 schema 非法）/ 807011（来源鉴权失败）/ 807012（同版本冲突）/ 807013（非权威源写入被拒绝）；新增角色 Upstream-System、MDM-Admin |
| 2026-05-26 | CR-003 | Modified | 补充 US-004 历史版本快照需求：新增 MPT 后台查询接口定义（GET /api/mpt/mdm/{entity}/v1/{code}/history），支持 Brand / CarLine / Platform 历史版本按 version 降序查询，响应包含来源字段 |
| 2026-05-27 | CR-004 | Added | 纳入产品树底层 5 类主数据（Model / Variant / Configuration / Option Family / Option Code）：新增术语表（§3）；新增域 6~10 共 12 条 US（US-019 ~ US-030）覆盖 CRUD、引用校验、Option Code 绑定/互斥、按选项码反查配置、事件发布、全量快照、历史追溯、上游接入扩展；修改 NG1 和 OS-1 移除已纳入实体的非目标声明；新增 G9/G10 业务目标；补充业务约束与 Out of Scope（OS-9 ~ OS-12）；新增 Open Questions（Q1 ~ Q7）与 Impact Analysis。VMD 侧对应的下游迁移由 VMD-CR-011 承接 |
