# MDM 平台 - Requirements

## 1. Overview

edd-mdm 是企业数字底座领域的横向微服务，定位为**多子域主数据管理（MDM）平台**，按业务域划分子域，逐步纳入企业核心主数据的 Golden Record 治理。当前已纳入治理的子域：

| 子域 | 管理范畴 | 落地状态 |
|------|----------|----------|
| **Product MDM** | 管"卖什么/造什么"——品牌、车系、平台、车型、配置、选项等产品树主数据 | CR-001 ~ CR-005 已落地 |
| **Party MDM** | 管"跟谁交易"——供应商、经销商、客户、合作方等外部业务伙伴主数据 | CR-006 本期纳入 |
| **EEAD MDM** | 管"车上有什么"——E/E 架构层产品/工程定义类主数据，外延规划包括节点字典、通讯矩阵、诊断架构、刷写/OTA 拓扑、安全架构五块 | CR-007 本期纳入（仅车载节点字典首版） |
| **Org MDM** | 管"企业自身组织结构"——工厂 / 法人 / BU / 部门 / 成本中心 / 仓库等组织架构主数据 | CR-008 本期纳入（仅工厂 Plant 首版） |
| **Material MDM** | 管"用什么造"——物料、零件、品类等物料主数据 | CR-009 本期纳入（Part + MaterialCategory 首版） |

> 后续规划子域：Location MDM（地理位置）等，留待后续 CR。

### Product MDM 子域

承接原 VMD 项目侧的产品树主数据 SSOT 上移至 edd-mdm，覆盖以下 8 类实体：
- **已落地**（CR-001 / CR-002 / CR-003）：Brand（品牌）、CarLine（车系）、Platform（平台）
- **已纳入**（CR-004）：Model（车型）、Variant（版本）、Configuration（配置）、Option Family（选项族）、Option Code（选项码）

> CR-010 本期纳入：为 Option Family 实体新增 `category` 字段，将商品分类维度纳入 Golden Record 治理。

### Party MDM 子域

建立业务伙伴主数据的统一治理能力。首期范围 = **Supplier（供应商）**，后续扩展 Dealer（经销商）、Customer（客户）等实体（见 OS-13）。

### EEAD 子域

承载所有 E/E 架构（Electrical/Electronic Architecture）层的产品/工程定义类主数据，与 Product MDM 子域**同级、物理隔离、逻辑独立**。EEAD 子域的完整外延规划包括五大块：**节点字典**（车载节点 Vehicle Node 等）、**通讯矩阵**（Communication Matrix）、**诊断架构**（Diagnostic Architecture）、**刷写 / OTA 拓扑**（Flashing / OTA Topology）、**信息安全架构**（Security Architecture）。本期 CR-007 仅交付节点字典中的车载节点（Vehicle Node）首版能力，承载"节点身份 + 节点能力声明"的横向产品定义字典。其余四块以及节点 ↔ 平台 ↔ 车型 ↔ 架构代次的关联表，留待后续 CR 立项（见 OS-15 / OS-16）。

> **行业语境**：车载节点（Vehicle Node）原由 `edd-vmd` 的 `Device` 实体承载，本次随 EEAD 子域落地一并上移到 `edd-mdm`，VMD 降级为本地投影副本（参考 VMD-CR-010 的 `source / external_ref_id / external_version / last_sync_time` 投影副本模式：source=MDM 的记录在 VMD 后台只读）。

### Org MDM 子域

管理企业自身组织结构主数据的统一治理能力，定位为"管企业内部怎么组织"——工厂（Plant）、法人（Legal Entity）、业务单元（BU）、部门（Department）、成本中心（Cost Center）、仓库（Warehouse）等。Org 子域与 Product MDM / Party MDM / EEAD 子域**同级、物理隔离、逻辑独立**。

首期范围 = **Plant（工厂）**，承载整车工厂 / 生产基地的 Golden Record。

> **行业语境**：Plant（工厂）的行业 SSOT 是 ERP（SAP S/4HANA 的 `Plant / WERKS`、Oracle ERP 的 Inventory Organization 等），MDM 是分发枢纽不是产生方。"Plant"是车企行业标准词：VW "Plant Wolfsburg" / Toyota Motor Manufacturing "Plant Kentucky" / GM "Assembly Plant" / BMW "Plant Spartanburg" / Mercedes "Plant Sindelfingen" / Stellantis "Assembly Plant"；蔚小理对外英文资料也均用 Plant。本期 ERP 尚未上线，MDM 暂代行 Plant SSOT 职责；未来 ERP 上线后，Plant 子域降级为 ERP 的本地副本 + 治理层（详见 §2 G17 与 §5 约束）。工厂同时具备**组织属性**（法人归属 / 成本中心 / 启停状态）与**位置属性**（地址 / GPS / 占地面积）；本期归 Org 子域（组织维度优先），位置属性以扁平字段形式内嵌，不抽出独立 Location 子域。

> **术语锁定**：英文统一用 **Plant**；"Factory"仅在 §3 术语表注明为通用别名，正文与字段名一律不使用；中文统一用"工厂"。

> 其余 Org 子域候选实体（Legal Entity / BU / Department / Cost Center / Warehouse）本期不落地，列入 Out of Scope（见 OS-19），留待后续 CR。

### Material MDM 子域

建立物料主数据的统一治理能力，覆盖企业全生命周期物料主数据。首期范围 = **MaterialCategory（物料品类）** 与 **Part（零件）**，Material 子域与 Product MDM / Party MDM / EEAD / Org 子域**同级、物理隔离、逻辑独立**。

> **行业语境**：物料（Material）是制造业核心主数据之一，与 Product MDM（管"卖什么/造什么"）形成互补——Material MDM 管"用什么造"。原 VMD 中的零件数据降级为 MDM Material 的本地投影（VMD 不再作为零件主数据源）。

> **术语锁定**：英文统一用 **Material**（物料域）、**Part**（零件）、**MaterialCategory**（物料品类）；中文统一用"物料"、"零件"、"物料品类"。

> 其余 Material 子域候选实体（BOM / SubstituteRelation 等）本期不落地，列入 Out of Scope（见 OS-25），留待后续 CR。

---

主数据的来源分为两类：(1) **本地维护**：由 MDM-User 通过后台直接 CRUD 录入；(2) **上游系统接入**：由具备业务源头权威的上游系统（如 PLM / DMS / 集团主数据平台 / CPQ / ERP / SRM）通过 Kafka 消息或 Feign / HTTP 接口推送至 edd-mdm，由 edd-mdm 统一治理、版本化并对外分发。所有子域共享同一套接入/分发/审计/版本化基础设施，并在主数据上记录来源系统、来源 ID、来源版本、接收通道与接收时间等信息以支持幂等校验、冲突裁决与全链路审计。

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
- **G11**：建立 Party MDM 子域，以 Supplier（供应商）作为首个实体落地，形成 Supplier 的 SSOT（Single Source of Truth）
- **G12**：Party MDM 复用 Product MDM 已建立的接入/分发/审计/版本化基础设施（Outbox Pattern、Ingestion Log、权威源配置、幂等校验等），不做重复造轮子
- **G13**：建立 EEAD 子域，承载所有 E/E 架构层产品/工程定义类主数据，与 Product MDM / Party MDM 子域**同级、物理隔离、逻辑独立**（包路径、表前缀、Kafka topic、Feign 路径前缀、权限点、审批治理流程六个维度全部隔离，详见 §5）
- **G14**：在 EEAD 子域下落地车载节点（Vehicle Node）首版能力，作为"节点身份 + 节点能力声明"的横向产品定义字典，形成 Vehicle Node 的 SSOT；不承载工程态的通讯/诊断/刷写/安全参数（这些归 EEAD 子域内其他子能力，由后续 CR 落地）
- **G15**：EEAD 子域基础设施治理与命名空间隔离并存——一方面与 Product MDM / Party MDM 共享 edd-mdm 服务实例、共享 DDD 四层骨架、共享 Java 包扁平结构、共享技术栈与运维治理（DB schema、Outbox Pattern、Ingestion Log、权威源配置、审计监控等），另一方面在表前缀（`mdm_eead_*`）、Kafka topic（`mdm.eead.*`）、Feign 路径前缀（`/api/service/mdm/eead/v1/**`）、权限点前缀（`mdm:eead:*`）、Java 类名命名前缀（VehicleNode* / NodeType* 等业务前缀）上保持物理与逻辑隔离
- **G16**：建立 Org 子域，以 Plant（工厂）作为首个实体落地，形成 Plant 的 SSOT；Org 子域与 Product MDM / Party MDM / EEAD 子域**同级、物理隔离、逻辑独立**（包路径、表前缀、Kafka topic、Feign 路径前缀、权限点、审批治理流程六个维度全部隔离，详见 §5）
- **G17**：本期 ERP 尚未上线，MDM 暂代行 Plant SSOT 职责（source_system 默认 LOCAL）；未来 ERP 上线后，Plant 子域降级为 ERP 的本地副本 + 治理层，保留 source_system 扩展语义供未来 ERP 接入使用；Org 子域基础设施治理与命名空间隔离并存——与 Product MDM / Party MDM / EEAD 共享 edd-mdm 服务实例、共享 DDD 四层骨架、共享技术栈与运维治理，同时在表前缀（`mdm_org_*`）、Kafka topic（`mdm.org.*`）、Feign 路径前缀（`/api/service/mdm/org/v1/**`）、权限点前缀（`mdm:org:*`）、Java 类名命名前缀（Plant* / PlantType* / ProductionCapability* 等业务前缀）上保持物理与逻辑隔离
- **G18**：建立 Material 子域，覆盖企业全生命周期物料主数据，以 MaterialCategory（物料品类）与 Part（零件）作为首批实体落地，形成 Part 与 MaterialCategory 的 SSOT
- **G19**：Material 子域基础设施治理与命名空间隔离并存——与 Product MDM / Party MDM / EEAD / Org 共享 edd-mdm 服务实例、共享 DDD 四层骨架、共享技术栈与运维治理，同时在表前缀（`mdm_material_*`）、Kafka topic（`mdm.material.*`）、Feign 路径前缀（`/api/service/mdm/material/v1/**`）、权限点前缀（`mdm:material:*`）、Java 类名命名前缀（Part* / MaterialCategory* / PartType* / LifecycleStage* 等业务前缀）上保持物理与逻辑隔离
- **G20**：VMD 中的零件数据降级为 MDM Material 的本地投影（VMD 不再作为零件主数据源），通过订阅 MDM Material 事件完成本地副本同步
- **G21**：将 Option Family 的商品分类维度（`category`）纳入 Golden Record 治理，为下游销售配置器 / 商品中心提供统一的选项族分组标识（CR-010）

### 非目标（明确不做）

- **NG1**：不接管 VMD 的车辆实例（VIN）/ 零部件 / 生命周期 / 制造厂商（Manufacturer）等领域
- **NG2**：不接管 Customer / Material / Employee / Location 等其他 MDM 子域
- **NG3**：不实施跨系统主数据的字段级合并裁决（multi-source field-level merge）。首期采用 **单一权威源（Single Authoritative Source）** 策略：每条主数据在任一时刻仅由配置中指定的一个权威源（LOCAL 或某个上游系统）负责写入；非权威源的推送将被拒绝或仅记录审计日志，不更新主表
- **NG4**：不实施数据质量打分引擎
- **NG5**：不实施跨系统 ID 映射表
- **NG6**：不实施审批工作流（首期 CRUD 即发布）
- **NG7**：不实施 Manufacturer（留待后续 CR）
- **NG8**：不实施对账机制（留待后续 CR）
- **NG9**：不实施 Org 子域中除 Plant 以外的其余实体（Legal Entity / BU / Department / Cost Center / Warehouse 等，见 OS-19）

## 3. Glossary（术语表）

### Product MDM 术语

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
| Option Family Category | 选项族商品分类 | CR-010 纳入。商品/销售视角的分组维度（外饰/内饰/动力/智能化/舒适/安全/附件），区别于 EEAD `functionalDomain`（工程视角） |

> **语义说明**："选项族 / 选项码"在 MDM 体系内承载**两种语义**：既可代表"版本/配置的标配识别特征"，也可代表"客户选装项"。MDM 不区分这两种语义，由消费方根据上下文使用。

### Party MDM 术语

| 英文 | 中文 | 行业对齐说明 |
|------|------|-------------|
| Party | 业务伙伴 | 泛指与企业发生业务往来的外部组织或个人。Party MDM 管"跟谁交易"（供应商、经销商、客户、合作方等） |
| Supplier | 供应商 | CR-006 纳入。指向企业提供物料、零部件、服务或物流等资源的外部组织。等价于 SAP Vendor / Supplier、Oracle Supplier |

### EEAD 术语

| 英文 | 中文 | 行业对齐说明 |
|------|------|-------------|
| EEAD | 电子电气架构数据 | Electrical/Electronic Architecture Data 的缩写。指车辆电子电气架构层的产品/工程定义类主数据，包含节点字典、通讯矩阵、诊断架构、刷写/OTA 拓扑、安全架构五块；CR-007 本期仅纳入节点字典 |
| Vehicle Node | 车载节点 | CR-007 纳入。指 E/E 架构上的逻辑节点单元，承载"节点身份 + 节点能力声明"的横向产品定义。等价于 AUTOSAR ECU、行业 BOM 中的"电子件"或"控制器"。原由 `edd-vmd` 的 `Device` 实体承载，本次上移至 EEAD 子域 |
| Node Type | 节点类型 | Vehicle Node 的分类属性枚举。取值含域控制器（DCU）、电控单元（ECU）、传感器（Sensor）、执行器（Actuator）、网关（Gateway）、通讯终端（Telematics）等 |
| Functional Domain | 功能域 | Vehicle Node 的功能域归属枚举。取值含动力（Powertrain）、底盘（Chassis）、车身（Body）、智驾（ADAS）、智舱（Cockpit）、车联（Connectivity）、能源（Energy）等 |
| Device Category | 设备分类 | Vehicle Node 的设备分类（比 Node Type 更细的子分类），用于工程视角的设备归类 |
| OTA Support Type | OTA 支持类型 | Vehicle Node 的 OTA 能力声明枚举。取值 FOTA（固件 OTA）、SOTA（软件 OTA）、BOTH（两者皆支持）、NOT_SUPPORTED（不支持） |
| HSM Capability | HSM 能力 | Vehicle Node 的硬件安全模块（Hardware Security Module）能力声明枚举。取值 NONE / SHE（Secure Hardware Extension）/ HSM_LIGHT / HSM_FULL，对齐行业惯例（AUTOSAR、EVITA） |
| Security Level | 信息安全等级 | Vehicle Node 的信息安全等级声明，与 ISO/SAE 21434《道路车辆 - 网络安全工程》的资产安全等级映射 |

### Org MDM 术语

| 英文 | 中文 | 行业对齐说明 |
|------|------|-------------|
| Org | 组织架构 | Organization 的缩写。指企业内部组织结构主数据域，管"企业自身怎么组织"（工厂、法人、BU、部门、成本中心、仓库等） |
| Plant | 工厂 | CR-008 纳入。指独立的生产制造基地或整车工厂，承载工厂 Golden Record。等价于 SAP S/4HANA Plant / WERKS、Oracle Inventory Organization。VW "Plant Wolfsburg" / Toyota "Plant Kentucky" / GM "Assembly Plant" / BMW "Plant Spartanburg" / Mercedes "Plant Sindelfingen" 均使用 Plant。**"Factory"为通用制造业用词，仅在此处注明为别名，正文与字段名一律不使用** |
| Plant Type | 工厂类型 | Plant 的分类属性枚举。取值含整车总装（VEHICLE_ASSEMBLY）、动力总成（POWERTRAIN）、电池（BATTERY）、冲压（STAMPING）、焊装（WELDING）、涂装（PAINTING）、其他（OTHER） |
| Legal Entity | 法人 | Org 子域候选实体（本期不落地，见 OS-19）。指独立法人主体 |
| Business Unit | 业务单元 | Org 子域候选实体（本期不落地，见 OS-19）。BU |
| Department | 部门 | Org 子域候选实体（本期不落地，见 OS-19） |
| Cost Center | 成本中心 | Org 子域候选实体（本期不落地，见 OS-19）。等价于 SAP Cost Center / KOSTL |
| Warehouse | 仓库 | Org 子域候选实体（本期不落地，见 OS-19）。等价于 SAP Warehouse / LGORT |

### Material MDM 术语

| 英文 | 中文 | 行业对齐说明 |
|------|------|-------------|
| Material | 物料 | 泛指企业生产运营中使用的所有原材料、零部件、半成品、成品的统称。Material MDM 管"用什么造" |
| Part | 零件 | CR-009 纳入。指具体的物料单元，承载零件身份、分类、能力声明与生命周期。等价于 SAP Material / Material Number、PLM Part Number |
| MaterialCategory | 物料品类 | CR-009 纳入。指物料的分类维度，支持树形层级结构。等价于 SAP Material Group / Material Class、PLM Part Category |
| Part Type | 零件类型 | Part 的分类属性枚举。取值含原材料（RAW_MATERIAL）、标准件（STANDARD_PART）、定制件（CUSTOM_PART）、软件件（SOFTWARE）、总成件（ASSEMBLY）等 |
| Lifecycle Stage | 生命周期阶段 | Part 的生命周期状态枚举。取值含 PROTOTYPE（样件）/ PRE_PRODUCTION（预量产）/ MASS_PRODUCTION（量产）/ PHASE_OUT（停产过渡）/ OBSOLETE（淘汰，终态） |
| Substitute Part | 替代件 | Part 间的替代关系，指在特定场景下可替代另一 Part 使用的零件 |
| BOM | 物料清单 | Bill of Material，Material 子域候选实体（本期不落地，见 OS-25）。指产品结构化的零部件组成清单 |
| Drawing | 图纸 | Part 的工程图纸属性（drawingNo / drawingVersion），不作为独立实体 |
| Local Projection | 本地投影 | 指下游系统（如 VMD）将 MDM 主数据同步到本地的只读副本模式，source=MDM 的记录在下游后台只读 |

## 4. User Stories

### 域 1: MDM 后台维护

#### US-001: CRUD Brand

**As a** MDM-User, **I want** CRUD Brand, **so that** 管理品牌 Golden Record。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Brand 且 code 唯一 THEN THE SYSTEM SHALL 持久化 Brand 记录并设置 version=1，并自动填充 create_by（当前认证用户）、create_time（当前时间）、modify_by、modify_time
- WHEN MDM-User 创建 Brand 且 code 已存在 THEN THE SYSTEM SHALL 返回错误码 812101 并拒绝创建
- WHEN MDM-User 更新 Brand 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，并自动填充 modify_by（当前认证用户）、modify_time（当前时间）
- WHEN MDM-User 更新 Brand 且记录不存在 THEN THE SYSTEM SHALL 返回错误码 812102 并拒绝更新
- WHEN MDM-User 失效 Brand 且 status=ACTIVE THEN THE SYSTEM SHALL 设置 status=INACTIVE 和 effectiveTo=now() 并自增 version，并自动填充 modify_by、modify_time
- WHEN MDM-User 删除 Brand 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录
- WHEN MDM-User 删除 Brand 且 status≠DRAFT THEN THE SYSTEM SHALL 返回错误码 812103 并拒绝删除
- IF Brand 的 effectiveFrom > effectiveTo THEN THE SYSTEM SHALL 返回错误码 812104 并拒绝保存

#### US-002: CRUD CarLine

**As a** MDM-User, **I want** CRUD CarLine（含按 brandCode 过滤、按 status 过滤），**so that** 管理车系 Golden Record。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 CarLine 且 code 唯一且 brandCode 指向已存在且 status=ACTIVE 的 Brand THEN THE SYSTEM SHALL 持久化 CarLine 记录并设置 version=1，并自动填充 create_by（当前认证用户）、create_time（当前时间）、modify_by、modify_time
- WHEN MDM-User 创建 CarLine 且 code 已存在 THEN THE SYSTEM SHALL 返回错误码 812101 并拒绝创建
- WHEN MDM-User 创建 CarLine 且 brandCode 指向不存在或 status≠ACTIVE 的 Brand THEN THE SYSTEM SHALL 返回错误码 812105 并拒绝创建
- WHEN MDM-User 更新 CarLine 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，并自动填充 modify_by（当前认证用户）、modify_time（当前时间）
- WHEN MDM-User 失效 CarLine 且 status=ACTIVE THEN THE SYSTEM SHALL 设置 status=INACTIVE 和 effectiveTo=now() 并自增 version，并自动填充 modify_by、modify_time
- WHEN MDM-User 删除 CarLine 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录
- WHEN MDM-User 查询 CarLine 列表 THEN THE SYSTEM SHALL 支持按 brandCode 和 status 过滤
- IF CarLine 的 effectiveFrom > effectiveTo THEN THE SYSTEM SHALL 返回错误码 812104 并拒绝保存

#### US-003: CRUD Platform

**As a** MDM-User, **I want** CRUD Platform, **so that** 管理平台 Golden Record。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Platform 且 code 唯一 THEN THE SYSTEM SHALL 持久化 Platform 记录并设置 version=1，并自动填充 create_by（当前认证用户）、create_time（当前时间）、modify_by、modify_time
- WHEN MDM-User 创建 Platform 且 code 已存在 THEN THE SYSTEM SHALL 返回错误码 812101 并拒绝创建
- WHEN MDM-User 更新 Platform 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，并自动填充 modify_by（当前认证用户）、modify_time（当前时间）
- WHEN MDM-User 失效 Platform 且 status=ACTIVE THEN THE SYSTEM SHALL 设置 status=INACTIVE 和 effectiveTo=now() 并自增 version，并自动填充 modify_by、modify_time
- WHEN MDM-User 删除 Platform 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录
- IF Platform 的 effectiveFrom > effectiveTo THEN THE SYSTEM SHALL 返回错误码 812104 并拒绝保存

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
- WHEN 调用历史版本查询接口且 code 不存在 THEN THE SYSTEM SHALL 返回错误码 812102 并拒绝查询

#### US-005: 生效期合法性校验

**As a** System, **I want** 校验 effectiveFrom / effectiveTo 的合法性, **so that** 避免无效时间窗。

**Acceptance Criteria** (EARS 语法):

- WHEN 保存 Brand / CarLine / Platform 且 effectiveFrom 不为空且 effectiveTo 不为空 IF effectiveFrom > effectiveTo THEN THE SYSTEM SHALL 返回错误码 812104 并拒绝保存
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
- WHEN 上游消息 schema 非法或必填字段缺失 THEN THE SYSTEM SHALL 拒绝该消息、记录错误日志并投递至死信队列，返回错误码 812110
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
- WHEN 鉴权失败、来源未注册或来源被禁用 THEN THE SYSTEM SHALL 返回错误码 812111 并拒绝接入
- WHEN 鉴权通过 THEN THE SYSTEM SHALL 复用 US-013 同一接入处理链路（schema 校验 → 权威源校验 → 幂等校验 → 业务校验 → upsert + history + outbox）
- WHEN 接入处理成功 THEN THE SYSTEM SHALL 返回 200 OK 与接入结果摘要（entityId、新 version、operationType：CREATED/UPDATED/DUPLICATED/REJECTED）
- WHEN 接入处理失败 THEN THE SYSTEM SHALL 返回对应错误码（812110 ~ 812113）与错误描述，由上游系统决定是否重试

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
- WHEN 上游消息到达且本地已存在对应记录 IF 上游 sourceVersion = 本地 source_version 且 source_payload_hash 不一致 THEN THE SYSTEM SHALL 视为同版本不一致冲突，记录告警日志并返回错误码 812112，由人工或权威源策略干预

#### US-017: 权威源配置与冲突裁决

**As a** MDM-Admin, **I want** 为每个实体（Brand / CarLine / Platform）配置权威源, **so that** 同一实体仅由唯一权威源写入，避免多源覆盖。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 提供权威源配置，维度包括：entityType（BRAND / SERIES / PLATFORM）、code 模式（精确 code 或通配 *）、authoritativeSource（LOCAL / PLM / DMS / ...）、conflictPolicy（REJECT / AUDIT_ONLY）
- THE SYSTEM SHALL 支持通过 Nacos 配置或配置表维护，并支持热更新
- WHEN 接收到上游消息或本地维护请求 IF 当前来源 = 配置的 authoritativeSource THEN THE SYSTEM SHALL 正常写入
- WHEN 接收到上游消息或本地维护请求 IF 当前来源 != 配置的 authoritativeSource 且 conflictPolicy=REJECT THEN THE SYSTEM SHALL 拒绝写入，返回错误码 812113，并记录告警日志
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
- WHEN MDM-User 创建 Variant 且 code 长度大于 57 字符 THEN THE SYSTEM SHALL 返回错误码 812115 并拒绝创建（为下层 Configuration code 自动拼接 7 位序号预留空间，保证 Configuration code 最大长度不超过 64 字符）
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

> **Configuration code 生成规则（CR-005）**：Configuration 的 code 不由调用方传入，由系统按规则自动生成，规则为 `{variantCode}` + **7 位零填充自增序号**（同一 Variant 下从 0000001 起步、严格单调递增）。例如 variantCode = `XREHSLA26PA` → 该 Variant 下首个配置 code = `XREHSLA26PA0000001`，第二个 = `XREHSLA26PA0000002`。Configuration code 全局唯一且不可变。序号由独立的序列表（mdm_configuration_seq）按 variant_code 维度维护，DRAFT 状态物理删除时**不回收**序号（避免 history / outbox / 下游订阅出现"同 code 不同实体"的歧义）。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Configuration 且 variantCode 指向已存在且 status=ACTIVE 的 Variant THEN THE SYSTEM SHALL 在同一本地事务内执行：(1) 对 mdm_configuration_seq 按 variant_code 行锁自增 next_seq；(2) 拼接 code = `{variantCode}` + 自增序号零填充至 7 位；(3) 持久化 Configuration 记录并设置 version=1，自动填充 create_by、create_time、modify_by、modify_time
- WHEN MDM-User 创建 Configuration 且请求体携带 code 字段 THEN THE SYSTEM SHALL 静默忽略该字段并仍按系统规则生成 code（DTO 不暴露 code 入参，REST 风格）
- WHEN MDM-User 创建 Configuration 且 variantCode 指向不存在或 status≠ACTIVE 的 Variant THEN THE SYSTEM SHALL 拒绝创建并返回引用完整性错误
- WHEN MDM-User 创建 Configuration 且系统生成 code 在 mdm_configuration 表已存在（理论不应发生，UK 兜底）THEN THE SYSTEM SHALL 自动重试一次序号自增；仍冲突则返回错误码 812101 并告警
- WHEN MDM-User 创建 Configuration 且对应 Variant 的序号已超出 9,999,999（7 位上限）THEN THE SYSTEM SHALL 返回错误码 812114 并拒绝创建
- WHEN MDM-User 更新 Configuration 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，**忽略入参中的 code 字段**（保留原 code 不变），并自动填充 modify_by、modify_time
- WHEN MDM-User 失效 Configuration 且 status=ACTIVE THEN THE SYSTEM SHALL 设置 status=INACTIVE 和 effectiveTo=now() 并自增 version，并自动填充 modify_by、modify_time
- WHEN MDM-User 删除 Configuration 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录，**不回退 mdm_configuration_seq.next_seq**（序号只增不复用）
- WHEN MDM-User 删除 Configuration 且 status≠DRAFT THEN THE SYSTEM SHALL 拒绝删除并返回业务错误
- WHEN MDM-User 查询 Configuration 列表 THEN THE SYSTEM SHALL 支持按 variantCode、status 过滤
- WHEN 创建/更新接口返回响应 THEN THE SYSTEM SHALL 在响应体中回填本次操作产生或保持的 code 字段
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

#### US-072: 按 Variant 和 Option Code 组合反查 Configuration Code

**As a** Service-Caller（VSO）, **I want** 给定 variantCode 和一组 Option Code，反查匹配的 Configuration Code，**so that** 支撑 VSO 创建心愿单时根据版本和选项码组合定位对应的配置代码。

**Acceptance Criteria** (EARS 语法):

- WHEN Service-Caller 提供 variantCode 和一组 Option Code 进行反查 THEN THE SYSTEM SHALL 返回该 Variant 下所有绑定的 Option Code 集合完全包含所提供组合的 Configuration 的 code（包含匹配）
- WHEN Service-Caller 提供 variantCode 和一组 Option Code 进行反查且无匹配结果 THEN THE SYSTEM SHALL 返回空结果或错误码
- WHEN Service-Caller 提供 variantCode 和一组 Option Code 进行反查 THEN THE SYSTEM SHALL 仅返回 status=ACTIVE 的 Configuration
- WHEN Service-Caller 提供空的 Option Code 集合 THEN THE SYSTEM SHALL 拒绝查询并返回参数校验错误
- WHEN Service-Caller 提供的 variantCode 不存在或 status≠ACTIVE THEN THE SYSTEM SHALL 拒绝查询并返回引用完整性错误

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
- WHEN MDM-User 创建 Option Family 且 category 为空 THEN THE SYSTEM SHALL 返回参数校验错误（错误码 812123）并拒绝创建
- WHEN MDM-User 创建 Option Family 且 category 取值不在枚举范围 THEN THE SYSTEM SHALL 返回参数校验错误（错误码 812123）并拒绝创建
- WHEN MDM-User 查询 Option Family 列表 THEN THE SYSTEM SHALL 支持按 category、status 任意组合过滤

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

#### US-071: Option Family 商品分类（category）管理（CR-010）

**As a** MDM-User, **I want** 为每个 Option Family 维护 category 商品分类, **so that** 下游销售配置器 / 商品中心可按分类分组展示选项族。

> **Option Family 字段扩展（CR-010）**：
> - ✅ category: 枚举 VARCHAR(32)，商品分类。取值范围：EXTERIOR（外饰）/ INTERIOR（内饰）/ POWERTRAIN（动力总成）/ INTELLIGENT（智能化）/ COMFORT（舒适便利）/ SAFETY（安全）/ ACCESSORY（选装附件）/ OTHER（其他）
>
> **字段语义**：标识 Option Family 在销售配置器 / 商品中心的商品分类维度，便于下游按功能域分组展示与统计。该字段是**商品/销售视角**的分类，与 EEAD 子域 `VehicleNode.functionalDomain`（工程电子架构视角）属于不同维度，不复用枚举值。
>
> ✅ = 必填，⭕ = 可选

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Option Family 且 category 为空 THEN THE SYSTEM SHALL 返回参数校验错误（错误码 812123）并拒绝创建
- WHEN MDM-User 创建 Option Family 且 category 取值不在枚举范围（EXTERIOR / INTERIOR / POWERTRAIN / INTELLIGENT / COMFORT / SAFETY / ACCESSORY / OTHER）THEN THE SYSTEM SHALL 返回参数校验错误（错误码 812123）并拒绝创建
- WHEN MDM-User 创建 Option Family 且 category 取值合法 THEN THE SYSTEM SHALL 在持久化记录时一并保存 category 字段
- WHEN MDM-User 更新 Option Family 的 category 字段 THEN THE SYSTEM SHALL 自增 version、写入 history 表、写入 Outbox 事件
- WHEN MDM-User 更新 Option Family 的 category 字段且新值不在枚举范围 THEN THE SYSTEM SHALL 返回参数校验错误（错误码 812123）并拒绝更新
- WHEN MDM-User 查询 Option Family 列表 THEN THE SYSTEM SHALL 支持按 category、status 任意组合过滤
- WHEN Feign 全量快照 / 单点查询返回 Option Family 记录 THEN THE SYSTEM SHALL 在响应中包含 category 字段
- WHEN Outbox 事件 payload 构建且实体为 Option Family THEN THE SYSTEM SHALL 在 payload 中包含 category 字段
- WHEN history 表插入 Option Family 快照 THEN THE SYSTEM SHALL 在快照中保留 category 字段
- WHEN 上游系统通过 Kafka / Feign 推送 Option Family 且 category 为空或取值不在枚举范围 THEN THE SYSTEM SHALL 返回参数校验错误并拒绝接入

### 域 10: CR-004 新增实体的跨切面能力

#### US-027: 5 类新实体的事件发布

**As a** System, **I want** 在 Model / Variant / Configuration / Option Family / Option Code 发生变更时通过事务性发件箱发布事件，**so that** 下游可订阅同步产品树底层主数据变更。

**Acceptance Criteria** (EARS 语法):

- WHEN Model / Variant / Configuration / Option Family / Option Code 被创建且 status=ACTIVE THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType 含实体类型与操作类型）
- WHEN Model / Variant / Configuration / Option Family / Option Code 被更新（含失效）THEN THE SYSTEM SHALL 写入 Outbox 事件
- WHEN 事件写入 Outbox THEN THE SYSTEM SHALL 与业务变更在同一本地事务内完成
- WHEN 事件 payload 构建 THEN THE SYSTEM SHALL 包含 eventId / eventType / occurredAt / entityId / version / payload 主体字段及来源字段
- WHEN 事件 payload 构建且实体为 Option Family THEN THE SYSTEM SHALL 在 payload 中包含 category 字段
- WHEN Variant 或 Configuration 的 Option Code 绑定关系发生变更 THEN THE SYSTEM SHALL 写入对应实体的 updated 事件

#### US-028: 5 类新实体的全量快照消费

**As a** Service-Caller, **I want** 通过 Feign 拉取 Model / Variant / Configuration / Option Family / Option Code 的全量快照，**so that** 下游可执行 Bootstrap 与对账。

**Acceptance Criteria** (EARS 语法):

- WHEN Service-Caller 调用全量快照接口且未传 includeInactive 参数 THEN THE SYSTEM SHALL 仅返回 status=ACTIVE 的记录
- WHEN Service-Caller 调用全量快照接口且 includeInactive=true THEN THE SYSTEM SHALL 返回所有状态的记录
- WHEN 返回结果 THEN THE SYSTEM SHALL 支持分页
- WHEN Service-Caller 按 code 单点查询且记录存在且 status=ACTIVE THEN THE SYSTEM SHALL 返回实体详情
- WHEN Service-Caller 按 code 单点查询且记录不存在或 status≠ACTIVE THEN THE SYSTEM SHALL 返回空
- WHEN Service-Caller 查询 Option Family 记录 THEN THE SYSTEM SHALL 在响应中包含 category 字段
- WHEN Feign 调用失败 THEN THE SYSTEM SHALL 通过 FallbackFactory 返回空列表或 null 并记录日志

#### US-029: 5 类新实体的历史版本追溯

**As a** System, **I want** 每次 Model / Variant / Configuration / Option Family / Option Code 发生变更时生成历史版本快照，**so that** 支持回溯与审计。

**Acceptance Criteria** (EARS 语法):

- WHEN Model / Variant / Configuration / Option Family / Option Code 发生创建、更新、失效操作 THEN THE SYSTEM SHALL 在对应 history 表插入一条完整快照记录
- WHEN history 表插入快照 THEN THE SYSTEM SHALL 记录变更时间、操作人、变更前后的 version
- WHEN 查询历史版本 THEN THE SYSTEM SHALL 按 entityId 和 version 降序返回快照列表，响应包含来源字段
- WHEN Variant 或 Configuration 的 Option Code 绑定关系发生变更 THEN THE SYSTEM SHALL 在对应实体的 history 表中记录快照
- WHEN Option Family 发生创建、更新、失效操作 THEN THE SYSTEM SHALL 在 history 表快照中保留 category 字段

#### US-030: 5 类新实体的上游接入扩展

**As an** Upstream-System, **I want** 通过 Kafka 或 Feign / HTTP 向 edd-mdm 推送 Model / Variant / Configuration / Option Family / Option Code 主数据，**so that** 由 edd-mdm 统一治理产品树底层主数据。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 复用 US-013 / US-014 已建立的上游接入处理链路（schema 校验 → 来源鉴权 → 权威源校验 → 幂等校验 → 业务校验 → upsert + history + outbox）
- THE SYSTEM SHALL 对 5 类新实体的上游推送执行与本地维护一致的引用完整性校验
- THE SYSTEM SHALL 对 5 类新实体的上游推送执行与本地维护一致的互斥约束校验（Variant / Configuration 下同一 Option Family 最多一个 Option Code）
- THE SYSTEM SHALL 在 mdm_ingestion_log 中记录 5 类新实体的每一次接入处理结果
- THE SYSTEM SHALL 暴露 5 类新实体的 Prometheus 接入监控指标，与现有指标维度一致
- WHEN 上游推送 Configuration 主数据 THEN THE SYSTEM SHALL 按以下两层规则决定本地 code（CR-005）：
    - **第 1 层：幂等更新**——按 (source_system, source_id) 在本地查找；命中本地记录则走更新流程，本地 code 保持不变（即便上游本次 payload 的 code 与本地不同，亦不改 code，仅记录告警提示上游 code 漂移）
    - **第 2 层：未命中 → 进入新增流程**：
        - 若上游 payload 未携带 code → 由系统按 US-022 规则生成（`{variantCode}` + 7 位零填充自增序号）
        - 若上游 payload 携带 code 且该 code 在 MDM 全局**未被占用** → 直接采用上游 code 入库
        - 若上游 payload 携带 code 且该 code 在 MDM 全局**已被占用**（无论被占记录的来源是 LOCAL 还是其他上游 source）→ 视为 **code 命名空间冲突**，本地按 US-022 规则生成新 code 兜底入库，记录告警日志、累加监控指标 `mdm.configuration.code.upstream_conflict`，并在 mdm_ingestion_log 中记录 codeOverride=true 与 upstreamCode / finalCode
- WHEN 上游推送的 Configuration code 长度超过 64 字符 THEN THE SYSTEM SHALL 视同上游未携带 code，由系统按 US-022 规则生成本地 code，并在 mdm_ingestion_log 中记录告警
- WHEN 上游推送 Model / Variant / Option Family / Option Code 主数据（非 Configuration）THEN THE SYSTEM SHALL 沿用 US-013 / US-014 既有 code 处理策略，不应用本条 Configuration 专属规则
- WHEN 上游推送 Option Family 主数据 THEN THE SYSTEM SHALL 校验 category 字段必填且取值在枚举范围内（EXTERIOR / INTERIOR / POWERTRAIN / INTELLIGENT / COMFORT / SAFETY / ACCESSORY / OTHER），校验规则与本地维护一致；不合法则拒绝接入并返回参数校验错误

### 域 11: Party MDM - 供应商管理

#### US-031: CRUD Supplier

**As a** MDM-User, **I want** CRUD Supplier, **so that** 管理供应商 Golden Record。

> **Supplier 字段定义**：
> - ✅ code: VARCHAR(64)，业务主键，全局唯一
> - ✅ name: VARCHAR(128)，供应商正式名称
> - ⭕ name_local: VARCHAR(128)，本地化名称
> - ⭕ short_name: VARCHAR(64)，简称/品牌名
> - ⭕ supplier_type: VARCHAR(256)，业务分类，支持多选，逗号分隔（MATERIAL / COMPONENT / SERVICE / LOGISTICS / OTHER），如 "MATERIAL,SERVICE"
> - ⭕ country: VARCHAR(64)，所在国家/地区
> - ⭕ business_license_no: VARCHAR(64)，统一社会信用代码/工商注册号
> - ⭕ tax_id: VARCHAR(64)，税号
> - ⭕ registered_address: VARCHAR(256)，注册地址
> - ⭕ contact_name: VARCHAR(64)，联系人姓名
> - ⭕ contact_phone: VARCHAR(32)，联系人电话
> - ⭕ contact_email: VARCHAR(128)，联系人邮箱
> - ⭕ bank_name: VARCHAR(128)，开户银行
> - ⭕ bank_account: VARCHAR(64)，银行账号
> - ⭕ cooperation_start_date: DATE，合作开始日期
> - ⭕ description: VARCHAR(512)，描述
> - 通用治理字段（与 Product MDM 各表完全对齐）：source_system / source_id / source_version / ingestion_channel / ingestion_time / source_payload_hash / version / effective_from / effective_to / status / create_by / create_time / modify_by / modify_time / row_version / row_valid
>
> ✅ = 必填，⭕ = 可选

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Supplier 且 code 唯一 THEN THE SYSTEM SHALL 持久化 Supplier 记录并设置 version=1，并自动填充 create_by（当前认证用户）、create_time（当前时间）、modify_by、modify_time
- WHEN MDM-User 创建 Supplier 且 code 已存在 THEN THE SYSTEM SHALL 返回错误码 812701 并拒绝创建
- WHEN MDM-User 创建 Supplier 且 name 为空 THEN THE SYSTEM SHALL 返回参数校验错误并拒绝创建
- WHEN MDM-User 更新 Supplier 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，并自动填充 modify_by（当前认证用户）、modify_time（当前时间）
- WHEN MDM-User 更新 Supplier 且记录不存在 THEN THE SYSTEM SHALL 返回错误码 812102 并拒绝更新
- WHEN MDM-User 失效 Supplier 且 status=ACTIVE THEN THE SYSTEM SHALL 设置 status=INACTIVE 和 effective_to=now() 并自增 version，并自动填充 modify_by、modify_time；THE SYSTEM SHALL NOT 校验下层引用（Supplier 在 MDM 内无下层实体，失效时仅发布事件，由下游各自决定是否拒绝引用）
- WHEN MDM-User 删除 Supplier 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录
- WHEN MDM-User 删除 Supplier 且 status≠DRAFT THEN THE SYSTEM SHALL 返回错误码 812103 并拒绝删除
- IF Supplier 的 effective_from > effective_to THEN THE SYSTEM SHALL 返回错误码 812104 并拒绝保存
- WHEN 本地维护写入 Supplier THEN THE SYSTEM SHALL 设置 source_system=LOCAL、ingestion_channel=LOCAL、ingestion_time=now()
- THE SYSTEM SHALL 支持 Supplier 的状态机：DRAFT → ACTIVE → INACTIVE，状态流转规则与 Product MDM 各实体一致

#### US-032: Supplier 历史版本快照

**As a** System, **I want** 每次 Supplier 发生变更时生成历史版本快照, **so that** 支持回溯与审计。

**Acceptance Criteria** (EARS 语法):

- WHEN Supplier 发生创建、更新、失效操作 THEN THE SYSTEM SHALL 在 Supplier history 表插入一条完整快照记录（与 US-004 / US-029 同模式）
- WHEN history 表插入快照 THEN THE SYSTEM SHALL 记录变更时间、操作人、变更前后的 version
- WHEN 查询 Supplier 历史版本 THEN THE SYSTEM SHALL 按 entityId 和 version 降序返回快照列表，响应包含来源字段（source_system / source_id / source_version / ingestion_channel / ingestion_time）
- WHEN MDM-User 通过后台查询 Supplier 历史版本 THEN THE SYSTEM SHALL 提供 GET /api/mpt/mdm/supplier/v1/{code}/history 接口，按 version 降序返回快照列表
- WHEN 调用历史版本查询接口且 code 不存在 THEN THE SYSTEM SHALL 返回错误码 812102 并拒绝查询

#### US-033: Supplier 事件发布

**As a** System, **I want** 在 Supplier 发生变更时通过事务性发件箱写入事件，后台 Relay 任务推送到 Kafka topic `mdm.party.supplier.created` / `mdm.party.supplier.updated` / `mdm.party.supplier.deactivated`, **so that** 下游可订阅同步且不丢消息。

**Acceptance Criteria** (EARS 语法):

- WHEN Supplier 被创建且 status=ACTIVE THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=mdm.party.supplier.created）
- WHEN Supplier 被更新（含失效）THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=mdm.party.supplier.updated 或 mdm.party.supplier.deactivated）
- WHEN 事件写入 Outbox THEN THE SYSTEM SHALL 与业务变更在同一本地事务内完成
- WHEN 后台 Relay 任务扫描 Outbox THEN THE SYSTEM SHALL 按 occurredAt 顺序投递 Kafka
- WHEN Kafka 投递成功 THEN THE SYSTEM SHALL 标记 Outbox 记录为已发送（sent=true, sent_at=now()）
- WHEN Kafka 投递失败 IF 超出重试次数（默认 3 次）THEN THE SYSTEM SHALL 投递死信队列并告警
- WHEN 事件 payload 构建 THEN THE SYSTEM SHALL 包含 eventId / eventType / occurredAt / entityId / version / payload 主体字段及来源字段（source_system / source_id / source_version / ingestion_channel / ingestion_time）
- THE SYSTEM SHALL 使用 `mdm.party.supplier.*` 命名空间（区别于 Product MDM 的 `mdm.product.*`），体现 Party 子域的命名空间隔离

#### US-034: Supplier 全量快照

**As a** Service-Caller, **I want** 通过 Feign 拉取 Supplier 全量快照, **so that** 下游可执行 Bootstrap 与对账。

**Acceptance Criteria** (EARS 语法):

- WHEN Service-Caller 调用 GET /api/service/supplier/v1/listAll 且未传 includeInactive 参数 THEN THE SYSTEM SHALL 仅返回 status=ACTIVE 的记录
- WHEN Service-Caller 调用 GET /api/service/supplier/v1/listAll 且 includeInactive=true THEN THE SYSTEM SHALL 返回所有状态的记录
- WHEN 返回结果 THEN THE SYSTEM SHALL 支持分页（page / size 参数）
- WHEN Service-Caller 调用 GET /api/service/supplier/v1/{code} 且记录存在且 status=ACTIVE THEN THE SYSTEM SHALL 返回 Supplier 详情（与 US-009 / US-012 同模式）
- WHEN Service-Caller 调用 GET /api/service/supplier/v1/{code} 且记录不存在或 status≠ACTIVE THEN THE SYSTEM SHALL 返回 404 或空
- WHEN Feign 调用失败 THEN THE SYSTEM SHALL 通过 FallbackFactory 返回空列表或 null 并记录日志

#### US-035: Supplier 上游接入（Kafka 通道）

**As an** Upstream-System, **I want** 通过 Kafka 向 edd-mdm 推送 Supplier 主数据, **so that** 由 edd-mdm 作为 Supplier Golden Record 统一治理与分发。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 订阅约定的上游 Topic（命名约定：`upstream.<sourceSystem>.party.supplier`）消费推送消息
- THE SYSTEM SHALL 复用 US-013 已建立的接入处理链路（schema 校验 → 来源鉴权 → 权威源校验 → 幂等校验 → 业务字段校验 → upsert + history + outbox）
- WHEN 上游消息 schema 合法 THEN THE SYSTEM SHALL 依次执行：来源鉴权 → 权威源校验（见 US-017） → 幂等校验（见 US-016） → 业务字段校验（与本地维护一致）
- WHEN 所有校验通过 THEN THE SYSTEM SHALL 在同一本地事务内完成主表 upsert、history 写入与 outbox 事件写入，并自动填充审计字段（create_by/modify_by 使用配置的 sourceSystem 标识；create_time/modify_time 使用服务端当前时间）
- WHEN 消息处理成功 THEN THE SYSTEM SHALL 提交 Kafka offset
- WHEN 消息处理失败（非幂等丢弃，非业务校验拒绝）THEN THE SYSTEM SHALL 按默认 3 次重试，超出后投递死信队列并告警
- THE SYSTEM SHALL 在 mdm_ingestion_log 中记录每一次消息的处理结果（见 US-018）

#### US-036: Supplier 上游接入（Feign / HTTP 通道）

**As an** Upstream-System, **I want** 通过 Feign / HTTP 接口向 edd-mdm 同步推送 Supplier 主数据, **so that** 在不具备 Kafka 通道的场景下完成主数据接入。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 提供接收接口：POST /api/upstream/mdm/supplier/v1/ingest
- WHEN Upstream-System 调用 ingest 接口 THEN THE SYSTEM SHALL 从请求头（如 X-Source-System）或请求体提取 sourceSystem 标识并完成鉴权（API Key / OAuth2）
- WHEN 鉴权失败、来源未注册或来源被禁用 THEN THE SYSTEM SHALL 返回错误码 812111 并拒绝接入
- WHEN 鉴权通过 THEN THE SYSTEM SHALL 复用 US-014 同一接入处理链路（schema 校验 → 权威源校验 → 幂等校验 → 业务校验 → upsert + history + outbox）
- WHEN 接入处理成功 THEN THE SYSTEM SHALL 返回 200 OK 与接入结果摘要（entityId、新 version、operationType：CREATED/UPDATED/DUPLICATED/REJECTED）
- WHEN 接入处理失败 THEN THE SYSTEM SHALL 返回对应错误码（812110 ~ 812113 / 812701）与错误描述，由上游系统决定是否重试

#### US-037: Supplier 数据来源记录、幂等处理、权威源裁决

**As a** System, **I want** Supplier 纳入与 Product MDM 相同的数据来源记录、幂等处理与权威源裁决治理体系, **so that** 保证 Supplier Golden Record 的一致性、可追溯性与可监控性。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 复用 US-015 已定义的数据来源记录机制：Supplier 主表及 history 表包含 source_system / source_id / source_version / ingestion_channel / ingestion_time / source_payload_hash 字段，填充规则与 Product MDM 各实体一致
- THE SYSTEM SHALL 复用 US-016 已定义的幂等处理机制：以 (source_system, source_id) 作为上游记录的逻辑主键定位本地 Supplier 记录，版本比较与冲突处理规则不变
- THE SYSTEM SHALL 复用 US-017 已定义的权威源配置与冲突裁决机制：entityType 扩展 SUPPLIER 取值，支持为 Supplier 配置独立的 authoritativeSource 与 conflictPolicy
- THE SYSTEM SHALL NOT 重复定义上述机制的字段语义与处理规则，避免双源信息漂移

#### US-038: Supplier 上游接入审计与监控

**As a** MDM-Admin, **I want** 查看 Supplier 上游接入处理记录与监控指标, **so that** 排查问题并保障数据质量。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 复用 US-018 已定义的 mdm_ingestion_log 审计表：entityType 扩展 SUPPLIER 取值，记录 Supplier 每一次接入处理的完整信息
- THE SYSTEM SHALL 复用 US-018 已定义的 Prometheus 指标：按 sourceSystem / entityType=SUPPLIER / status 维度统计接入总量、成功率、失败率、平均处理耗时、消息积压量
- WHEN 某 sourceSystem + entityType=SUPPLIER 维度连续失败次数超过阈值 THEN THE SYSTEM SHALL 触发告警通知（阈值配置复用 US-018 已有机制）
- THE SYSTEM SHALL 复用 US-018 已有的 MPT 端查询接口（GET /api/mpt/mdm/ingestion/v1/log），支持按 entityType=SUPPLIER 过滤查询

### 域 12: EEAD - 车载节点字典管理（CR-007）

#### US-039: CRUD VehicleNode

**As a** MDM-User, **I want** CRUD VehicleNode（创建、查询、更新、失效、删除、列表、列出全部、导出），**so that** 管理车载节点 Golden Record。

> **VehicleNode 字段定义**（最终以 design 阶段落地）：
>
> 身份属性
> - ✅ nodeCode: VARCHAR(64)，业务主键，全局唯一（如 TBOX / CCP / IDCM / LIDAR_F 等）
> - ✅ nodeName: VARCHAR(128)，节点中文名称
> - ⭕ nodeNameEn: VARCHAR(128)，节点英文名称
> - ⭕ description: VARCHAR(512)，描述/备注
>
> 分类属性
> - ✅ nodeType: 枚举，节点类型（DCU / ECU / SENSOR / ACTUATOR / GATEWAY / TELEMATICS）
> - ✅ functionalDomain: 枚举，功能域（POWERTRAIN / CHASSIS / BODY / ADAS / COCKPIT / CONNECTIVITY / ENERGY）
> - ⭕ deviceCategory: VARCHAR(64)，设备分类（比 nodeType 更细的子分类）
>
> 能力声明
> - ✅ isCoreNode: BOOLEAN，是否核心节点
> - ✅ otaSupportType: 枚举，OTA 支持类型（FOTA / SOTA / BOTH / NOT_SUPPORTED）
> - ⭕ hsmCapability: 枚举，HSM 能力（NONE / SHE / HSM_LIGHT / HSM_FULL）
> - ⭕ securityLevel: VARCHAR(32) 或枚举，信息安全等级（与 ISO/SAE 21434 等级映射）
>
> MDM 同步与治理字段（强制，与 Product MDM / Party MDM 各表同构）
> - ✅ source: 枚举，数据来源（MDM / MANUAL），默认 MANUAL
> - ⭕ external_ref_id: VARCHAR(64)，上游系统主键（source=MANUAL 时为 NULL）
> - ⭕ external_version: BIGINT，上游系统版本号
> - ⭕ last_sync_time: DATETIME，最后同步时间
>
> 通用治理字段（与 Product MDM / Party MDM 各表对齐）：version / status（DRAFT / ACTIVE / INACTIVE）/ effective_from / effective_to / create_by / create_time / modify_by / modify_time / row_version / row_valid
>
> ✅ = 必填，⭕ = 可选

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 VehicleNode 且 nodeCode 唯一 THEN THE SYSTEM SHALL 持久化 VehicleNode 记录并设置 version=1，并自动填充 create_by（当前认证用户）、create_time（当前时间）、modify_by、modify_time
- WHEN MDM-User 创建 VehicleNode 且 nodeCode 已存在 THEN THE SYSTEM SHALL 返回错误码 812302（VEHICLE_NODE_CODE_EXIST）并拒绝创建
- WHEN MDM-User 创建 VehicleNode 且 nodeName 为空 THEN THE SYSTEM SHALL 返回参数校验错误并拒绝创建
- WHEN MDM-User 创建 VehicleNode 且 nodeType / functionalDomain / otaSupportType / hsmCapability 任一字段取值不在枚举范围 THEN THE SYSTEM SHALL 返回参数校验错误并拒绝创建
- WHEN MDM-User 更新 VehicleNode 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，并自动填充 modify_by、modify_time；THE SYSTEM SHALL NOT 允许修改 nodeCode（业务主键不可变）
- WHEN MDM-User 更新 VehicleNode 且记录不存在 THEN THE SYSTEM SHALL 返回错误码 812301（VEHICLE_NODE_NOT_EXIST）并拒绝更新
- WHEN MDM-User 失效 VehicleNode 且 status=ACTIVE THEN THE SYSTEM SHALL 设置 status=INACTIVE 和 effective_to=now() 并自增 version，并自动填充 modify_by、modify_time
- WHEN MDM-User 删除 VehicleNode THEN THE SYSTEM SHALL 按 US-045 定义的删除前置依赖检查规则处理（DRAFT 直接物理删除，ACTIVE/INACTIVE 须反查下游引用）
- WHEN MDM-User 查询 VehicleNode 列表 THEN THE SYSTEM SHALL 支持按 nodeType / functionalDomain / otaSupportType / isCoreNode / status 任意组合过滤，并支持分页
- WHEN MDM-User 调用列出全部接口 THEN THE SYSTEM SHALL 返回所有 status=ACTIVE 的 VehicleNode 简要列表（不分页，供下拉选择类场景使用）
- WHEN MDM-User 调用导出接口 THEN THE SYSTEM SHALL 按当前过滤条件导出 VehicleNode 列表为 Excel/CSV 文件
- WHEN MDM-User 通过后台查询单条 VehicleNode 详情 THEN THE SYSTEM SHALL 返回完整字段（含身份/分类/能力声明/来源字段/审计字段）
- THE SYSTEM SHALL 对 VehicleNode 后台接口实施权限控制，权限点采用以下六种细分点：mdm:eead:vehicleNode:list（列表查询） / mdm:eead:vehicleNode:query（单点详情查询） / mdm:eead:vehicleNode:add（新增） / mdm:eead:vehicleNode:edit（编辑/失效） / mdm:eead:vehicleNode:remove（删除） / mdm:eead:vehicleNode:export（导出）
- IF VehicleNode 的 effective_from > effective_to THEN THE SYSTEM SHALL 返回业务错误并拒绝保存（沿用 §5 通用约束）

#### US-040: nodeCode 全局唯一校验

**As a** System, **I want** 校验 nodeCode 在 EEAD 子域内全局唯一，**so that** 避免同一 nodeCode 出现重复定义导致下游引用歧义。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 在 mdm_eead_vehicle_node 表上对 node_code 列设置全局唯一约束（UK），由数据库层兜底
- WHEN MDM-User 创建 VehicleNode 且 nodeCode 与表内已存在记录冲突（无论已存在记录 status=DRAFT/ACTIVE/INACTIVE 任何状态）THEN THE SYSTEM SHALL 返回错误码 812302（VEHICLE_NODE_CODE_EXIST）并拒绝创建
- THE SYSTEM SHALL 在错误响应体中明确指出冲突的 nodeCode 取值与已占用记录的当前 status，便于排查
- THE SYSTEM SHALL NOT 复用已物理删除（DRAFT 状态删除）记录的 nodeCode 进行隐式覆盖；若 MDM-User 创建一条 nodeCode 与历史已物理删除记录相同的新节点，THE SYSTEM SHALL 视作正常新建并允许（数据库层物理删除后 UK 不再阻塞，下游通过 VehicleNodeDeleted 事件已感知历史记录消失，新记录按 VehicleNodeCreated 事件正常推送）

#### US-041: VehicleNode 事件发布

**As a** System, **I want** 在 VehicleNode 发生创建、更新、失效、删除操作时通过事务性发件箱写入事件，由后台 Relay 任务推送到 Kafka topic mdm.eead.vehicleNode.event，**so that** 下游可订阅同步且不丢消息。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 使用单一 Kafka topic：mdm.eead.vehicleNode.event；所有 VehicleNode 事件发布到同一 topic，由 payload 中的 eventType 字段区分事件子类型（与 Product MDM 的"每实体每操作一个 topic"模式不同，此为 EEAD 子域的命名空间策略）
- THE SYSTEM SHALL 定义三种 eventType 取值：VehicleNodeCreated / VehicleNodeUpdated / VehicleNodeDeleted
- WHEN VehicleNode 被创建且 status=ACTIVE THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=VehicleNodeCreated）
- WHEN VehicleNode 被更新 THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=VehicleNodeUpdated）
- WHEN VehicleNode 被失效（status=ACTIVE → INACTIVE）THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=VehicleNodeUpdated，并在 payload 中通过 status=INACTIVE 体现失效语义）
- WHEN VehicleNode 被删除（按 US-045 通过前置依赖检查后实际执行删除）THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=VehicleNodeDeleted）
- WHEN 事件写入 Outbox THEN THE SYSTEM SHALL 与业务变更在同一本地事务内完成
- WHEN 后台 Relay 任务扫描 Outbox THEN THE SYSTEM SHALL 按 occurredAt 顺序投递 Kafka
- WHEN Kafka 投递成功 THEN THE SYSTEM SHALL 标记 Outbox 记录为已发送（sent=true, sent_at=now()）
- WHEN Kafka 投递失败 IF 超出重试次数（默认 3 次）THEN THE SYSTEM SHALL 投递死信队列并告警
- WHEN 事件 payload 构建 THEN THE SYSTEM SHALL 包含以下字段：eventId / eventType / occurredAt / entityId（即 nodeCode）/ version / payload 主体（含全量身份/分类/能力声明字段）/ 来源字段（source / external_ref_id / external_version / last_sync_time）
- THE SYSTEM SHALL 复用 Product MDM / Party MDM 已建立的 Outbox 基础设施（mdm_outbox 表与 Relay 任务），仅在 topic 命名空间上保持 EEAD 子域隔离

#### US-042: VehicleNode 全量快照

**As a** Service-Caller, **I want** 通过 Feign 拉取 VehicleNode 全量快照（GET /api/service/mdm/eead/v1/vehicleNode/snapshot），**so that** 下游可执行 Bootstrap 与对账。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 提供 Feign 接口：GET /api/service/mdm/eead/v1/vehicleNode/snapshot
- WHEN Service-Caller 调用 snapshot 接口且未传 includeInactive 参数 THEN THE SYSTEM SHALL 仅返回 status=ACTIVE 的 VehicleNode 记录
- WHEN Service-Caller 调用 snapshot 接口且 includeInactive=true THEN THE SYSTEM SHALL 返回所有状态的 VehicleNode 记录（含 INACTIVE）
- WHEN 返回结果 THEN THE SYSTEM SHALL 支持分页（page / size 参数）
- WHEN 返回结果 THEN THE SYSTEM SHALL 在每条记录中包含全量字段（身份/分类/能力声明/来源字段/版本字段）
- WHEN Feign 调用失败 THEN THE SYSTEM SHALL 通过 FallbackFactory 返回空列表并记录日志

#### US-043: VehicleNode 节点能力查询（按 nodeCode 单点）

**As a** Service-Caller, **I want** 通过 Feign 按 nodeCode 单点查询 VehicleNode 完整定义（含能力声明），**so that** 下游可补查未同步实体或获取实时节点能力声明。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 提供 Feign 接口：GET /api/service/mdm/eead/v1/vehicleNode/{nodeCode}
- WHEN Service-Caller 调用 {nodeCode} 接口且记录存在且 status=ACTIVE THEN THE SYSTEM SHALL 返回 VehicleNode 完整定义（含身份/分类/能力声明/来源字段）
- WHEN Service-Caller 调用 {nodeCode} 接口且记录不存在 THEN THE SYSTEM SHALL 返回 404 或空响应
- WHEN Service-Caller 调用 {nodeCode} 接口且记录存在但 status=INACTIVE THEN THE SYSTEM SHALL 返回 404 或空响应（与 US-012 同模式：默认仅暴露 ACTIVE 记录）
- WHEN Feign 调用失败 THEN THE SYSTEM SHALL 通过 FallbackFactory 返回 null 并记录日志

#### US-044: VehicleNode 按 OTA 类型批量查询

**As a** Service-Caller（OTA 服务等），**I want** 按 OTA 支持类型批量查询 VehicleNode（GET /api/service/mdm/eead/v1/vehicleNode/listByOtaType?type=FOTA），**so that** OTA 服务可基于节点能力声明圈选可推送的目标节点集合。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 提供 Feign 接口：GET /api/service/mdm/eead/v1/vehicleNode/listByOtaType?type={FOTA|SOTA|BOTH|NOT_SUPPORTED}
- WHEN Service-Caller 调用 listByOtaType 接口且 type 参数取值为 FOTA / SOTA / BOTH / NOT_SUPPORTED 之一 THEN THE SYSTEM SHALL 返回 status=ACTIVE 且 otaSupportType 匹配的 VehicleNode 列表
- WHEN Service-Caller 调用 listByOtaType 接口且 type 参数取值不在枚举范围或为空 THEN THE SYSTEM SHALL 返回参数校验错误并拒绝查询
- WHEN Service-Caller 调用 listByOtaType 接口且无匹配记录 THEN THE SYSTEM SHALL 返回空列表
- WHEN 返回结果 THEN THE SYSTEM SHALL 在每条记录中包含 nodeCode / nodeName / nodeType / functionalDomain / otaSupportType / hsmCapability / securityLevel 字段（用于 OTA 服务做圈选与安全策略判定）
- WHEN Feign 调用失败 THEN THE SYSTEM SHALL 通过 FallbackFactory 返回空列表并记录日志

#### US-045: VehicleNode 删除前置依赖检查

**As a** System, **I want** 在 VehicleNode 删除前反查下游是否存在引用，存在则拒绝删除，**so that** 避免悬空引用导致下游业务异常。

> **决策说明**：本 US 在"反查下游引用 + 硬拒绝"与"软删除 + 下游事件自决"两种策略中，本期选择**前者（反查 + 硬拒绝）**。理由：(1) 任务方案已分配错误码 812303（VEHICLE_NODE_HAS_DOWNSTREAM_REF），与硬拒绝路径自洽；(2) 业务侧倾向于在主数据层面强阻塞，避免下游静默处理失败导致的数据漂移。是否未来切换为软删除策略由 Q13 跟踪。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 删除 VehicleNode 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录（DRAFT 状态下游尚未感知，无需做反查）
- WHEN MDM-User 删除 VehicleNode 且 status=ACTIVE 或 status=INACTIVE THEN THE SYSTEM SHALL 通过 Feign 反查下游服务（首期范围：edd-vmd 服务的 VehiclePart 实体）是否存在 source=MDM 且引用该 nodeCode 的本地副本记录
- WHEN 反查结果存在引用 THEN THE SYSTEM SHALL 返回错误码 812303（VEHICLE_NODE_HAS_DOWNSTREAM_REF）并拒绝删除；错误响应中 SHALL 包含引用记录的统计信息（引用方服务名、引用数量、至多前 10 条引用方业务标识）
- WHEN 反查结果不存在引用 THEN THE SYSTEM SHALL 物理删除记录并写入 Outbox 事件（eventType=VehicleNodeDeleted，按 US-041）
- IF 反查下游 Feign 接口调用失败（超时、降级、不可用）THEN THE SYSTEM SHALL 默认拒绝删除并返回业务错误（fail-safe：不可用即视为可能存在引用，禁止盲删）
- THE SYSTEM SHALL 限制删除接口的权限点为 mdm:eead:vehicleNode:remove，且在反查 + 删除全过程在同一审计日志中追踪
- THE SYSTEM SHOULD 提供管理员旁路标志（仅供 MDM-Admin 通过额外权限点 mdm:eead:vehicleNode:remove:force 使用），允许在已确认下游已清理的情况下跳过反查直接删除；旁路使用须记录审计日志（含操作人、时间、目标 nodeCode、原因说明）

#### US-046: source=MDM/MANUAL 治理（MDM 即权威源）

**As a** MDM-User, **I want** 在 MDM 后台对 VehicleNode 记录正常 CRUD，无论该记录的 source 字段为 MANUAL 还是 MDM, **so that** 体现"MDM 即权威源、source 标记仅对下游副本生效"的治理语义。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 在 mdm_eead_vehicle_node 主表上设置 source 字段，默认值为 MANUAL，取值范围 {MANUAL, MDM}
- WHEN MDM-User 在 MDM 后台创建 VehicleNode 且未显式指定 source THEN THE SYSTEM SHALL 自动填充 source=MANUAL、external_ref_id=NULL、external_version=NULL、last_sync_time=now()
- WHEN MDM-User 在 MDM 后台对任意 VehicleNode 记录（无论 source=MANUAL 还是 source=MDM）执行更新、失效、删除操作 THEN THE SYSTEM SHALL 按 US-039 / US-045 的常规规则处理，不因 source 字段差异施加额外限制（MDM 即权威源，MANUAL/MDM 标记仅对下游副本生效）
- THE SYSTEM SHALL 在对外发布的 Kafka 事件 payload 与 Feign 接口响应中携带来源字段（source / external_ref_id / external_version / last_sync_time），供下游识别与对账
- THE SYSTEM SHALL 在 MDM 后台 VehicleNode 详情页面 / 列表页面展示 source 字段
- THE SYSTEM SHOULD NOT 在 MDM 后台对 source=MDM 的记录施加只读限制（与下游 VMD 后台对 source=MDM 记录只读的策略形成明确对照）
- THE SYSTEM SHALL 在 design 阶段明确：本期 EEAD VehicleNode 不开启上游接入路径（无 Kafka ingest topic、无 Feign ingest 接口），所有 MDM 内 VehicleNode 记录由 MDM-User 在后台直接维护，因此实际写入数据 source 默认均为 MANUAL；source=MDM 取值保留语义，供后续若开启上游接入或承接 VMD Device 数据迁移时使用（详见 OS-17、Q14）

### 域 13: Org MDM - 工厂管理（CR-008）

#### US-047: CRUD Plant

**As a** MDM-User, **I want** CRUD Plant（创建、查询、更新、失效、删除、列表、列出全部、导出），**so that** 管理工厂 Golden Record。

> **Plant 字段定义**（最终以 design 阶段落地）：
>
> 身份属性
> - ✅ code: VARCHAR(64)，业务主键，全局唯一（建议命名规范如 `PLT_<国家>_<城市>_<序号>`，如 `PLT_CN_CD_01`）
> - ✅ name: VARCHAR(128)，工厂正式名称
> - ⭕ nameEn: VARCHAR(128)，英文名称
> - ⭕ shortName: VARCHAR(64)，简称
> - ⭕ description: VARCHAR(512)，描述
>
> 分类属性
> - ✅ plantType: 枚举，工厂类型（VEHICLE_ASSEMBLY 整车总装 / POWERTRAIN 动力总成 / BATTERY 电池 / STAMPING 冲压 / WELDING 焊装 / PAINTING 涂装 / OTHER）
> - ⭕ legalEntityCode: VARCHAR(64)，所属法人编码（本期为字符串字段，不做引用完整性校验；未来 Legal Entity 落地后升级为外键）
> - ⭕ costCenterCode: VARCHAR(64)，对应成本中心（同上，未来升级为引用）
>
> 位置属性
> - ⭕ country: VARCHAR(64)，国家
> - ⭕ province: VARCHAR(64)，省/州
> - ⭕ city: VARCHAR(64)，城市
> - ⭕ address: VARCHAR(256)，详细地址
> - ⭕ longitude: DECIMAL(10,7)，经度
> - ⭕ latitude: DECIMAL(10,7)，纬度
> - ⭕ timezone: VARCHAR(64)，时区（IANA，如 `Asia/Shanghai`）
>
> 运营属性
> - ⭕ annualCapacity: BIGINT，年产能（台/件）
> - ⭕ productionLines: INT，产线数量
> - ⭕ operationalStartDate: DATE，投产日期
> - ⭕ mesInstance: VARCHAR(64)，对应 MES 实例标识（供未来对接使用，本期为标识字段不做联动）
>
> MDM 同步与治理字段（强制，与 Product MDM / Party MDM 各表同构）
> - ✅ source_system: 来源系统编码（LOCAL / ERP / PLM / DMS / GROUP_MDM 等），不可为空，MDM 后台本地维护时默认 LOCAL
> - ⭕ source_id: 上游系统中的业务主键（本地维护时与本地 code 相同或为空，由 LOCAL 适配器写入）
> - ⭕ source_version: 上游系统中的版本号（本地维护时可为空）
> - ✅ ingestion_channel: 接入通道（LOCAL / KAFKA / FEIGN），不可为空，MDM 后台本地维护时默认 LOCAL
> - ✅ ingestion_time: 最近一次接收/变更时间，不可为空
> - ⭕ source_payload_hash: 可选，最近一次接入消息体的哈希值，便于排查与冲突识别
>
> 通用治理字段（与 Product MDM / Party MDM / EEAD 各表对齐）：version / status（DRAFT / ACTIVE / INACTIVE）/ effective_from / effective_to / create_by / create_time / modify_by / modify_time / row_version / row_valid
>
> ✅ = 必填，⭕ = 可选

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Plant 且 code 唯一 THEN THE SYSTEM SHALL 持久化 Plant 记录并设置 version=1，并自动填充 create_by（当前认证用户）、create_time（当前时间）、modify_by、modify_time
- WHEN MDM-User 创建 Plant 且 code 已存在 THEN THE SYSTEM SHALL 返回错误码 812502（PLANT_CODE_EXIST）并拒绝创建
- WHEN MDM-User 创建 Plant 且 name 为空 THEN THE SYSTEM SHALL 返回参数校验错误并拒绝创建
- WHEN MDM-User 创建 Plant 且 plantType 取值不在枚举范围 THEN THE SYSTEM SHALL 返回参数校验错误并拒绝创建
- WHEN MDM-User 更新 Plant 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，并自动填充 modify_by、modify_time；THE SYSTEM SHALL NOT 允许修改 code（业务主键不可变）
- WHEN MDM-User 更新 Plant 且记录不存在 THEN THE SYSTEM SHALL 返回错误码 812501（PLANT_NOT_EXIST）并拒绝更新
- WHEN MDM-User 失效 Plant 且 status=ACTIVE THEN THE SYSTEM SHALL 设置 status=INACTIVE 和 effective_to=now() 并自增 version，并自动填充 modify_by、modify_time
- WHEN MDM-User 删除 Plant THEN THE SYSTEM SHALL 按 US-054 定义的删除前置依赖检查规则处理（DRAFT 直接物理删除，ACTIVE/INACTIVE 须反查下游引用）
- WHEN MDM-User 查询 Plant 列表 THEN THE SYSTEM SHALL 支持按 plantType / country / status 任意组合过滤，并支持分页
- WHEN MDM-User 调用列出全部接口 THEN THE SYSTEM SHALL 返回所有 status=ACTIVE 的 Plant 简要列表（不分页，供下拉选择类场景使用）
- WHEN MDM-User 调用导出接口 THEN THE SYSTEM SHALL 按当前过滤条件导出 Plant 列表为 Excel/CSV 文件
- WHEN MDM-User 通过后台查询单条 Plant 详情 THEN THE SYSTEM SHALL 返回完整字段（含身份/分类/位置/运营/来源字段/审计字段）
- THE SYSTEM SHALL 对 Plant 后台接口实施权限控制，权限点采用以下六种细分点：mdm:org:plant:list（列表查询） / mdm:org:plant:query（单点详情查询） / mdm:org:plant:add（新增） / mdm:org:plant:edit（编辑/失效） / mdm:org:plant:remove（删除） / mdm:org:plant:export（导出）
- IF Plant 的 effective_from > effective_to THEN THE SYSTEM SHALL 返回错误码 812504（PLANT_EFFECTIVE_PERIOD_INVALID）并拒绝保存

#### US-048: plantCode 全局唯一校验

**As a** System, **I want** 校验 code 在 Org 子域 Plant 实体内全局唯一，**so that** 避免同一 code 出现重复定义导致下游引用歧义。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 在 mdm_org_plant 表上对 code 列设置全局唯一约束（UK），由数据库层兜底
- WHEN MDM-User 创建 Plant 且 code 与表内已存在记录冲突（无论已存在记录 status=DRAFT/ACTIVE/INACTIVE 任何状态）THEN THE SYSTEM SHALL 返回错误码 812502（PLANT_CODE_EXIST）并拒绝创建
- THE SYSTEM SHALL 在错误响应体中明确指出冲突的 code 取值与已占用记录的当前 status，便于排查
- THE SYSTEM SHALL NOT 复用已物理删除（DRAFT 状态删除）记录的 code 进行隐式覆盖；若 MDM-User 创建一条 code 与历史已物理删除记录相同的新工厂，THE SYSTEM SHALL 视作正常新建并允许（数据库层物理删除后 UK 不再阻塞，下游通过 PlantDeleted 事件已感知历史记录消失，新记录按 PlantCreated 事件正常推送）

#### US-049: Plant 历史版本快照

**As a** System, **I want** 每次 Plant 发生变更时生成历史版本快照, **so that** 支持回溯与审计。

**Acceptance Criteria** (EARS 语法):

- WHEN Plant 发生创建、更新、失效操作 THEN THE SYSTEM SHALL 在 mdm_org_plant_history 表插入一条完整快照记录（与 US-004 / US-032 同模式）
- WHEN history 表插入快照 THEN THE SYSTEM SHALL 记录变更时间、操作人、变更前后的 version
- WHEN 查询历史版本 THEN THE SYSTEM SHALL 按 entityId 和 version 降序返回快照列表，响应包含来源字段（source_system / source_id / source_version / ingestion_channel / ingestion_time）
- WHEN MDM-User 通过后台查询工厂历史版本 THEN THE SYSTEM SHALL 提供 GET /api/mpt/mdm/plant/v1/{code}/history 接口，按 version 降序返回快照列表
- WHEN 调用历史版本查询接口且 code 不存在 THEN THE SYSTEM SHALL 返回错误码 812501（PLANT_NOT_EXIST）并拒绝查询

#### US-050: Plant 事件发布

**As a** System, **I want** 在 Plant 发生创建、更新、失效、删除操作时通过事务性发件箱写入事件，由后台 Relay 任务推送到 Kafka topic `mdm.org.plant.event`，**so that** 下游可订阅同步且不丢消息。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 使用单一 Kafka topic：`mdm.org.plant.event`；所有 Plant 事件发布到同一 topic，由 payload 中的 eventType 字段区分事件子类型（与 EEAD 子域 `mdm.eead.vehicleNode.event` 同模式）
- THE SYSTEM SHALL 定义三种 eventType 取值：PlantCreated / PlantUpdated / PlantDeleted
- WHEN Plant 被创建且 status=ACTIVE THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=PlantCreated）
- WHEN Plant 被更新 THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=PlantUpdated）
- WHEN Plant 被失效（status=ACTIVE → INACTIVE）THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=PlantUpdated，并在 payload 中通过 status=INACTIVE 体现失效语义）
- WHEN Plant 被删除（按 US-054 通过前置依赖检查后实际执行删除）THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=PlantDeleted）
- WHEN 事件写入 Outbox THEN THE SYSTEM SHALL 与业务变更在同一本地事务内完成
- WHEN 后台 Relay 任务扫描 Outbox THEN THE SYSTEM SHALL 按 occurredAt 顺序投递 Kafka
- WHEN Kafka 投递成功 THEN THE SYSTEM SHALL 标记 Outbox 记录为已发送（sent=true, sent_at=now()）
- WHEN Kafka 投递失败 IF 超出重试次数（默认 3 次）THEN THE SYSTEM SHALL 投递死信队列并告警
- WHEN 事件 payload 构建 THEN THE SYSTEM SHALL 包含以下字段：eventId / eventType / occurredAt / entityId（即 code）/ version / payload 主体（含全量身份/分类/位置/运营字段）/ 来源字段（source_system / source_id / source_version / ingestion_channel / ingestion_time）
- THE SYSTEM SHALL 复用 Product MDM / Party MDM / EEAD 已建立的 Outbox 基础设施（mdm_outbox 表与 Relay 任务），仅在 topic 命名空间上保持 Org 子域隔离

#### US-051: Plant 全量快照

**As a** Service-Caller, **I want** 通过 Feign 拉取 Plant 全量快照（GET /api/service/mdm/org/v1/plant/snapshot），**so that** 下游可执行 Bootstrap 与对账。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 提供 Feign 接口：GET /api/service/mdm/org/v1/plant/snapshot
- WHEN Service-Caller 调用 snapshot 接口且未传 includeInactive 参数 THEN THE SYSTEM SHALL 仅返回 status=ACTIVE 的 Plant 记录
- WHEN Service-Caller 调用 snapshot 接口且 includeInactive=true THEN THE SYSTEM SHALL 返回所有状态的 Plant 记录（含 INACTIVE）
- WHEN 返回结果 THEN THE SYSTEM SHALL 支持分页（page / size 参数）
- WHEN 返回结果 THEN THE SYSTEM SHALL 在每条记录中包含全量字段（身份/分类/位置/运营/来源字段/版本字段）
- WHEN Feign 调用失败 THEN THE SYSTEM SHALL 通过 FallbackFactory 返回空列表并记录日志

#### US-052: 按 plantCode 单点查询

**As a** Service-Caller, **I want** 通过 Feign 按 code 单点查询 Plant 完整定义（GET /api/service/mdm/org/v1/plant/{code}），**so that** 下游可补查未同步实体或获取实时工厂信息。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 提供 Feign 接口：GET /api/service/mdm/org/v1/plant/{code}
- WHEN Service-Caller 调用 {code} 接口且记录存在且 status=ACTIVE THEN THE SYSTEM SHALL 返回 Plant 完整定义（含身份/分类/位置/运营/来源字段）
- WHEN Service-Caller 调用 {code} 接口且记录不存在 THEN THE SYSTEM SHALL 返回 404 或空响应
- WHEN Service-Caller 调用 {code} 接口且记录存在但 status=INACTIVE THEN THE SYSTEM SHALL 返回 404 或空响应（与 US-012 同模式：默认仅暴露 ACTIVE 记录）
- WHEN Feign 调用失败 THEN THE SYSTEM SHALL 通过 FallbackFactory 返回 null 并记录日志

#### US-053: 按 plantType 批量查询

**As a** Service-Caller（VMD 等），**I want** 按工厂类型批量查询 Plant（GET /api/service/mdm/org/v1/plant/listByType?type=VEHICLE_ASSEMBLY），**so that** 下游可按工厂类型圈选目标工厂集合（如 VMD 按"整车总装工厂"圈选可绑定的下线工厂）。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 提供 Feign 接口：GET /api/service/mdm/org/v1/plant/listByType?type={VEHICLE_ASSEMBLY|POWERTRAIN|BATTERY|STAMPING|WELDING|PAINTING|OTHER}
- WHEN Service-Caller 调用 listByType 接口且 type 参数取值为枚举范围内的合法值 THEN THE SYSTEM SHALL 返回 status=ACTIVE 且 plantType 匹配的 Plant 列表
- WHEN Service-Caller 调用 listByType 接口且 type 参数取值不在枚举范围或为空 THEN THE SYSTEM SHALL 返回参数校验错误并拒绝查询
- WHEN Service-Caller 调用 listByType 接口且无匹配记录 THEN THE SYSTEM SHALL 返回空列表
- WHEN 返回结果 THEN THE SYSTEM SHALL 在每条记录中包含 code / name / plantType / country / city / status 字段（用于下游做圈选与筛选）
- WHEN Feign 调用失败 THEN THE SYSTEM SHALL 通过 FallbackFactory 返回空列表并记录日志

#### US-054: Plant 删除前置依赖检查

**As a** System, **I want** 在 Plant 删除前反查下游是否存在引用，存在则拒绝删除，**so that** 避免悬空引用导致下游业务异常。

> **决策说明**：沿用 US-045（VehicleNode 删除）的"反查下游引用 + 硬拒绝"策略。理由：Manufacturer / Plant 在 VMD 中是 `Vehicle` 的强引用字段，悬空风险更高；与 US-045 保持一致的删除范式。Plant 失效（INACTIVE）时按 Q17 建议方向同样执行反查校验（待业务最终确认）。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 删除 Plant 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录（DRAFT 状态下游尚未感知，无需做反查）
- WHEN MDM-User 删除 Plant 且 status=ACTIVE 或 status=INACTIVE THEN THE SYSTEM SHALL 通过 Feign 反查下游服务（首期范围：edd-vmd 服务）是否存在 source=MDM 且引用该 plantCode 的本地副本记录（VMD-CR-014 落地后字段为 `Vehicle.plantCode` / `VehicleBasicInfo.plant_code`，过渡期可同时反查 `manufacturerCode`）
- WHEN 反查结果存在引用 THEN THE SYSTEM SHALL 返回错误码 812503（PLANT_HAS_DOWNSTREAM_REF）并拒绝删除；错误响应中 SHALL 包含引用记录的统计信息（引用方服务名、引用数量、至多前 10 条引用方业务标识）
- WHEN 反查结果不存在引用 THEN THE SYSTEM SHALL 物理删除记录并写入 Outbox 事件（eventType=PlantDeleted，按 US-050）
- IF 反查下游 Feign 接口调用失败（超时、降级、不可用）THEN THE SYSTEM SHALL 默认拒绝删除并返回业务错误（fail-safe：不可用即视为可能存在引用，禁止盲删）
- THE SYSTEM SHALL 限制删除接口的权限点为 mdm:org:plant:remove，且在反查 + 删除全过程在同一审计日志中追踪
- THE SYSTEM SHOULD 提供管理员旁路标志（仅供 MDM-Admin 通过额外权限点 mdm:org:plant:remove:force 使用），允许在已确认下游已清理的情况下跳过反查直接删除；旁路使用须记录审计日志（含操作人、时间、目标 code、原因说明）

#### US-055: 数据来源记录（source_system 治理）

**As a** MDM-User, **I want** 在 MDM 后台对 Plant 记录正常 CRUD，系统自动记录数据来源信息（source_system / source_id / source_version / ingestion_channel / ingestion_time），**so that** 支持审计、追溯、裁决与下游识别；当上游系统（如 ERP）创建记录时以上游为准并记录来源，无上游时 MDM 自行创建并标记 source_system=LOCAL。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 复用 US-015 已定义的数据来源记录机制：mdm_org_plant 主表及 history 表包含 source_system / source_id / source_version / ingestion_channel / ingestion_time / source_payload_hash 字段，填充规则与 Product MDM 各实体一致
- WHEN MDM-User 在 MDM 后台创建 Plant THEN THE SYSTEM SHALL 设置 source_system=LOCAL、ingestion_channel=LOCAL、ingestion_time=now()
- WHEN MDM-User 在 MDM 后台对任意 Plant 记录执行更新、失效、删除操作 THEN THE SYSTEM SHALL 按 US-047 / US-054 的常规规则处理，不因 source_system 字段差异施加额外限制
- THE SYSTEM SHALL 在对外发布的 Kafka 事件 payload 与 Feign 接口响应中携带来源字段（source_system / source_id / source_version / ingestion_channel / ingestion_time），供下游识别与对账
- THE SYSTEM SHALL 在 MDM 后台 Plant 详情页面 / 列表页面展示来源字段
- THE SYSTEM SHALL NOT 在 MDM 后台对 source_system≠LOCAL 的记录施加只读限制（与下游 VMD 后台对 source=MDM 记录只读的策略形成明确对照）
- THE SYSTEM SHALL 在 design 阶段明确：本期 Org Plant 不开启上游接入路径（无 Kafka ingest topic、无 Feign ingest 接口），所有 MDM 内 Plant 记录由 MDM-User 在后台直接维护，因此实际写入数据 source_system 均为 LOCAL；source_system 字段保留扩展语义，供后续 ERP 上线后开启上游接入时使用（届时上游推送的记录 source_system=ERP、ingestion_channel=KAFKA/FEIGN，详见 OS-23、Q18）

### 域 14: Material MDM - 物料品类管理（CR-009）

#### US-056: CRUD MaterialCategory

**As a** MDM-User, **I want** CRUD MaterialCategory（创建、查询、更新、删除、列表），**so that** 管理物料品类 Golden Record。

> **MaterialCategory 字段定义**：
>
> 身份属性
> - ✅ code: VARCHAR(64)，业务主键，全局唯一
> - ✅ name: VARCHAR(128)，品类名称
> - ⭕ nameLocal: VARCHAR(128)，本地化名称
> - ⭕ description: VARCHAR(512)，描述
>
> 层级属性
> - ⭕ parentCode: VARCHAR(64)，父品类编码（自关联，支持树形结构，不可成环）
>
> 通用治理字段（与 Product MDM / Party MDM / EEAD / Org 各表对齐）：source_system / source_id / source_version / ingestion_channel / ingestion_time / source_payload_hash / version / status（DRAFT / ACTIVE / INACTIVE）/ effective_from / effective_to / create_by / create_time / modify_by / modify_time / row_version / row_valid
>
> ✅ = 必填，⭕ = 可选

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 MaterialCategory 且 code 唯一 THEN THE SYSTEM SHALL 持久化 MaterialCategory 记录并设置 version=1，并自动填充 create_by（当前认证用户）、create_time（当前时间）、modify_by、modify_time
- WHEN MDM-User 创建 MaterialCategory 且 code 已存在 THEN THE SYSTEM SHALL 返回错误码 812902（MATERIAL_CATEGORY_CODE_EXIST）并拒绝创建
- WHEN MDM-User 创建 MaterialCategory 且 name 为空 THEN THE SYSTEM SHALL 返回参数校验错误并拒绝创建
- WHEN MDM-User 创建 MaterialCategory 且 parentCode 指向不存在的 MaterialCategory THEN THE SYSTEM SHALL 返回错误码 812905（MATERIAL_CATEGORY_PARENT_NOT_EXIST）并拒绝创建
- WHEN MDM-User 创建 MaterialCategory 且 parentCode 形成环路（A→B→...→A）THEN THE SYSTEM SHALL 返回错误码 812906（MATERIAL_CATEGORY_LOOP_DETECTED）并拒绝创建
- WHEN MDM-User 更新 MaterialCategory 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，并自动填充 modify_by、modify_time；THE SYSTEM SHALL NOT 允许修改 code（业务主键不可变）
- WHEN MDM-User 更新 MaterialCategory 且记录不存在 THEN THE SYSTEM SHALL 返回错误码 812901（MATERIAL_CATEGORY_NOT_EXIST）并拒绝更新
- WHEN MDM-User 删除 MaterialCategory 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录
- WHEN MDM-User 删除 MaterialCategory 且 status≠DRAFT THEN THE SYSTEM SHALL 返回错误码 812903 并拒绝删除
- WHEN MDM-User 删除 MaterialCategory 且存在子项 MaterialCategory（parentCode=该记录 code）THEN THE SYSTEM SHALL 返回错误码 812903（MATERIAL_CATEGORY_HAS_CHILDREN）并拒绝删除
- WHEN MDM-User 删除 MaterialCategory 且存在 Part 引用该 MaterialCategory（categoryCode=该记录 code）THEN THE SYSTEM SHALL 返回错误码 812903 并拒绝删除
- WHEN MDM-User 查询 MaterialCategory 列表 THEN THE SYSTEM SHALL 支持按 parentCode、status 过滤，并支持分页
- WHEN MDM-User 查询 MaterialCategory 树形结构 THEN THE SYSTEM SHALL 返回完整的树形层级数据
- IF MaterialCategory 的 effective_from > effective_to THEN THE SYSTEM SHALL 返回错误码 812904（MATERIAL_CATEGORY_EFFECTIVE_PERIOD_INVALID）并拒绝保存

#### US-057: MaterialCategory 删除保护

**As a** System, **I want** 在 MaterialCategory 存在子项或被 Part 引用时阻止删除, **so that** 避免悬空引用导致数据不一致。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 删除 MaterialCategory 且存在子项 MaterialCategory（parentCode=该记录 code）THEN THE SYSTEM SHALL 返回错误码 812903 并拒绝删除，错误响应中包含子项数量信息
- WHEN MDM-User 删除 MaterialCategory 且存在 Part 引用该 MaterialCategory（categoryCode=该记录 code）THEN THE SYSTEM SHALL 返回错误码 812903 并拒绝删除，错误响应中包含引用 Part 数量信息
- WHEN MDM-User 删除 MaterialCategory 且无子项且无 Part 引用 THEN THE SYSTEM SHALL 允许删除（DRAFT 状态物理删除）

#### US-058: MaterialCategory 树形层级维护

**As a** MDM-User, **I want** 维护 MaterialCategory 的树形层级, **so that** 建立物料品类的分类体系。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 MaterialCategory 且 parentCode 为空 THEN THE SYSTEM SHALL 创建顶级品类节点
- WHEN MDM-User 创建 MaterialCategory 且 parentCode 指向已存在且 status=ACTIVE 的 MaterialCategory THEN THE SYSTEM SHALL 创建子品类节点
- WHEN MDM-User 更新 MaterialCategory 的 parentCode 且新 parentCode 指向已存在且 status=ACTIVE 的 MaterialCategory THEN THE SYSTEM SHALL 更新层级关系
- WHEN MDM-User 更新 MaterialCategory 的 parentCode 且形成环路 THEN THE SYSTEM SHALL 返回错误码 812906 并拒绝更新
- WHEN MDM-User 查询 MaterialCategory 树形结构 THEN THE SYSTEM SHALL 返回从根节点到叶子节点的完整树形数据

### 域 15: Material MDM - 零件管理（CR-009）

#### US-059: CRUD Part

**As a** MDM-User, **I want** CRUD Part（创建、查询、更新、删除、列表），**so that** 管理零件 Golden Record。

> **Part 字段定义**：
>
> 身份属性
> - ✅ code: VARCHAR(64)，业务主键，全局唯一（=零件号）
> - ✅ name: VARCHAR(128)，零件名称
> - ⭕ nameLocal: VARCHAR(128)，本地化名称
> - ⭕ description: VARCHAR(512)，描述
>
> 分类与关联
> - ✅ categoryCode: VARCHAR(64)，物料品类编码（→ MaterialCategory.code）
> - ✅ partType: 枚举，零件类型（RAW_MATERIAL / STANDARD_PART / CUSTOM_PART / SOFTWARE / ASSEMBLY）
> - ⭕ vehicleNodeCode: VARCHAR(64)，车载节点编码（→ EEAD.VehicleNode.code，替代原 device_code）
> - ⭕ supplierCode: VARCHAR(64)，供应商编码（→ Party.Supplier.code）
>
> 能力声明
> - ✅ isSoftware: BOOLEAN，是否软件件
> - ✅ fotaUpgradeable: BOOLEAN，是否可 FOTA 升级（物料级声明，与 VehicleNode 的架构级能力解耦）
> - ⭕ isSafetyCritical: BOOLEAN，是否安全关键件
> - ⭕ isKeyPart: 枚举，关重特性（KEY / MAJOR / SIMPLE），标识零件的关键程度
> - ⭕ isRegulatoryPart: BOOLEAN，是否法规件（需满足法规要求的零件）
> - ⭕ isFramePart: BOOLEAN，是否架构件（E/E 架构层面的核心零件）
> - ⭕ isAccuratelyTraced: BOOLEAN，是否精准追溯（需全程追溯的零件）
>
> FFA 属性
> - ⭕ ffaCode: VARCHAR(64)，功能配置特征码（Function Feature Allocation Code）
> - ⭕ ffaDesc: VARCHAR(256)，功能配置特征描述
>
> 数字化属性
> - ⭕ isDigitate: BOOLEAN，是否有数模（数字化三维模型）
> - ⭕ initialModel: VARCHAR(64)，初始车型（零件首次应用的车型）
>
> 物料属性
> - ⭕ uom: VARCHAR(32)，计量单位
> - ⭕ drawingNo: VARCHAR(64)，图纸编号
> - ⭕ drawingVersion: VARCHAR(32)，图纸版本
> - ⭕ weight: DECIMAL(10,3)，重量
> - ⭕ weightUom: VARCHAR(16)，重量单位
> - ⭕ productionCode: VARCHAR(64)，对应生产件号（生产系统使用的零件编号）
>
> 生命周期
> - ✅ lifecycleStage: 枚举，生命周期阶段（PROTOTYPE / PRE_PRODUCTION / MASS_PRODUCTION / PHASE_OUT / OBSOLETE）
> - ⭕ substitutePartCode: VARCHAR(64)，替代件编码（→ Part.code）
> - ⭕ firstProductionDate: DATE，首次投产时间
>
> 设计归属
> - ⭕ designer: VARCHAR(64)，设计工程师
> - ⭕ designerDept: VARCHAR(128)，设计工程师部门
>
> 通用治理字段（与各子域对齐）：source_system / source_id / source_version / ingestion_channel / ingestion_time / source_payload_hash / version / status（DRAFT / ACTIVE / INACTIVE）/ effective_from / effective_to / create_by / create_time / modify_by / modify_time / row_version / row_valid
>
> ✅ = 必填，⭕ = 可选

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Part 且 code 唯一 THEN THE SYSTEM SHALL 持久化 Part 记录并设置 version=1，并自动填充 create_by（当前认证用户）、create_time（当前时间）、modify_by、modify_time
- WHEN MDM-User 创建 Part 且 code 已存在 THEN THE SYSTEM SHALL 返回错误码 812910（PART_CODE_EXIST）并拒绝创建
- WHEN MDM-User 创建 Part 且 name 为空 THEN THE SYSTEM SHALL 返回参数校验错误并拒绝创建
- WHEN MDM-User 创建 Part 且 categoryCode 指向不存在或 status≠ACTIVE 的 MaterialCategory THEN THE SYSTEM SHALL 返回错误码 812911（PART_CATEGORY_INVALID）并拒绝创建
- WHEN MDM-User 创建 Part 且 partType 取值不在枚举范围 THEN THE SYSTEM SHALL 返回参数校验错误并拒绝创建
- WHEN MDM-User 创建 Part 且 vehicleNodeCode 指向不存在于 EEAD.VehicleNode 的记录 THEN THE SYSTEM SHALL 返回错误码 812912（PART_VEHICLE_NODE_INVALID）并拒绝创建
- WHEN MDM-User 创建 Part 且 supplierCode 指向不存在于 Party.Supplier 的记录 THEN THE SYSTEM SHALL 返回错误码 812913（PART_SUPPLIER_INVALID）并拒绝创建
- WHEN MDM-User 创建 Part 且 substitutePartCode 指向不存在的 Part THEN THE SYSTEM SHALL 返回错误码 812914（PART_SUBSTITUTE_INVALID）并拒绝创建
- WHEN MDM-User 创建 Part 且 lifecycleStage 取值不在枚举范围 THEN THE SYSTEM SHALL 返回参数校验错误并拒绝创建
- WHEN MDM-User 更新 Part 且记录存在 THEN THE SYSTEM SHALL 自增 version 并更新记录，并自动填充 modify_by、modify_time；THE SYSTEM SHALL NOT 允许修改 code（业务主键不可变）
- WHEN MDM-User 更新 Part 且记录不存在 THEN THE SYSTEM SHALL 返回错误码 812910 并拒绝更新
- WHEN MDM-User 失效 Part 且 status=ACTIVE THEN THE SYSTEM SHALL 设置 status=INACTIVE 和 effective_to=now() 并自增 version，并自动填充 modify_by、modify_time
- WHEN MDM-User 删除 Part 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录
- WHEN MDM-User 删除 Part 且 status≠DRAFT THEN THE SYSTEM SHALL 按 US-070 定义的删除前置依赖检查规则处理
- WHEN MDM-User 查询 Part 列表 THEN THE SYSTEM SHALL 支持按 categoryCode、partType、vehicleNodeCode、supplierCode、lifecycleStage、isKeyPart、status 任意组合过滤，并支持分页
- IF Part 的 effective_from > effective_to THEN THE SYSTEM SHALL 返回错误码 812904 并拒绝保存

#### US-060: Part 引用完整性校验（categoryCode）

**As a** System, **I want** 校验 Part 的 categoryCode 指向有效且 ACTIVE 的 MaterialCategory, **so that** 保证零件与品类的关联关系有效。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Part 且 categoryCode 指向不存在的 MaterialCategory THEN THE SYSTEM SHALL 返回错误码 812911 并拒绝创建
- WHEN MDM-User 创建 Part 且 categoryCode 指向 status≠ACTIVE 的 MaterialCategory THEN THE SYSTEM SHALL 返回错误码 812911 并拒绝创建
- WHEN MDM-User 更新 Part 且 categoryCode 变更 THEN THE SYSTEM SHALL 校验新 categoryCode 指向有效且 ACTIVE 的 MaterialCategory

#### US-061: Part 引用完整性校验（vehicleNodeCode）

**As a** System, **I want** 校验 Part 的 vehicleNodeCode 指向有效的 EEAD.VehicleNode, **so that** 保证零件与车载节点的关联关系有效。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Part 且 vehicleNodeCode 不为空且指向不存在于 EEAD.VehicleNode 的记录 THEN THE SYSTEM SHALL 返回错误码 812912 并拒绝创建
- WHEN MDM-User 更新 Part 且 vehicleNodeCode 变更且新值不为空 THEN THE SYSTEM SHALL 校验新 vehicleNodeCode 指向有效的 EEAD.VehicleNode

#### US-062: Part 引用完整性校验（supplierCode）

**As a** System, **I want** 校验 Part 的 supplierCode 指向有效的 Party.Supplier, **so that** 保证零件与供应商的关联关系有效。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建 Part 且 supplierCode 不为空且指向不存在于 Party.Supplier 的记录 THEN THE SYSTEM SHALL 返回错误码 812913 并拒绝创建
- WHEN MDM-User 更新 Part 且 supplierCode 变更且新值不为空 THEN THE SYSTEM SHALL 校验新 supplierCode 指向有效的 Party.Supplier

#### US-063: Part 与 MaterialCategory 历史版本快照

**As a** System, **I want** 每次 Part 与 MaterialCategory 发生变更时生成历史版本快照, **so that** 支持回溯与审计。

**Acceptance Criteria** (EARS 语法):

- WHEN Part 发生创建、更新、失效操作 THEN THE SYSTEM SHALL 在 mdm_material_part_history 表插入一条完整快照记录（与 US-004 / US-032 / US-049 同模式）
- WHEN MaterialCategory 发生创建、更新、删除操作 THEN THE SYSTEM SHALL 在 mdm_material_category_history 表插入一条完整快照记录
- WHEN history 表插入快照 THEN THE SYSTEM SHALL 记录变更时间、操作人、变更前后的 version
- WHEN 查询历史版本 THEN THE SYSTEM SHALL 按 entityId 和 version 降序返回快照列表，响应包含来源字段（source_system / source_id / source_version / ingestion_channel / ingestion_time）
- WHEN MDM-User 通过后台查询 Part 历史版本 THEN THE SYSTEM SHALL 提供 GET /api/mpt/mdm/material/part/v1/{code}/history 接口，按 version 降序返回快照列表
- WHEN MDM-User 通过后台查询 MaterialCategory 历史版本 THEN THE SYSTEM SHALL 提供 GET /api/mpt/mdm/material/category/v1/{code}/history 接口，按 version 降序返回快照列表
- WHEN 调用历史版本查询接口且 code 不存在 THEN THE SYSTEM SHALL 返回错误码 812901 或 812910 并拒绝查询

#### US-064: Part 与 MaterialCategory 事件发布

**As a** System, **I want** 在 Part 与 MaterialCategory 发生变更时通过事务性发件箱写入事件，由后台 Relay 任务推送到 Kafka topic `mdm.material.part.event` / `mdm.material.category.event`，**so that** 下游可订阅同步且不丢消息。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 使用单一 Kafka topic：`mdm.material.part.event`（Part）与 `mdm.material.category.event`（MaterialCategory）；所有事件发布到同一 topic，由 payload 中的 eventType 字段区分事件子类型（与 EEAD / Org 子域同模式）
- THE SYSTEM SHALL 定义 Part 的 eventType 取值：PartCreated / PartUpdated / PartDeleted
- THE SYSTEM SHALL 定义 MaterialCategory 的 eventType 取值：MaterialCategoryCreated / MaterialCategoryUpdated / MaterialCategoryDeleted
- WHEN Part 被创建且 status=ACTIVE THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=PartCreated）
- WHEN Part 被更新 THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=PartUpdated）
- WHEN Part 被失效（status=ACTIVE → INACTIVE）THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=PartUpdated，并在 payload 中通过 status=INACTIVE 体现失效语义）
- WHEN Part 被删除 THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=PartDeleted）
- WHEN MaterialCategory 被创建且 status=ACTIVE THEN THE SYSTEM SHALL 写入 Outbox 事件（eventType=MaterialCategoryCreated）
- WHEN MaterialCategory 被更新或删除 THEN THE SYSTEM SHALL 写入对应 Outbox 事件
- WHEN 事件写入 Outbox THEN THE SYSTEM SHALL 与业务变更在同一本地事务内完成
- WHEN 后台 Relay 任务扫描 Outbox THEN THE SYSTEM SHALL 按 occurredAt 顺序投递 Kafka
- WHEN Kafka 投递成功 THEN THE SYSTEM SHALL 标记 Outbox 记录为已发送（sent=true, sent_at=now()）
- WHEN Kafka 投递失败 IF 超出重试次数（默认 3 次）THEN THE SYSTEM SHALL 投递死信队列并告警
- WHEN 事件 payload 构建 THEN THE SYSTEM SHALL 包含以下字段：eventId / eventType / occurredAt / entityId（即 code）/ version / payload 主体 / 来源字段（source_system / source_id / source_version / ingestion_channel / ingestion_time）
- THE SYSTEM SHALL 复用 Product MDM / Party MDM / EEAD / Org 已建立的 Outbox 基础设施，仅在 topic 命名空间上保持 Material 子域隔离

#### US-065: Part 与 MaterialCategory 全量快照与批量查询

**As a** Service-Caller, **I want** 通过 Feign 拉取 Part 与 MaterialCategory 的全量快照，并按 code / categoryCode / vehicleNodeCode / supplierCode 批量查询, **so that** 下游可执行 Bootstrap 与对账。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 提供 Feign 接口：GET /api/service/mdm/material/v1/part/snapshot
- THE SYSTEM SHALL 提供 Feign 接口：GET /api/service/mdm/material/v1/category/snapshot
- WHEN Service-Caller 调用 snapshot 接口且未传 includeInactive 参数 THEN THE SYSTEM SHALL 仅返回 status=ACTIVE 的记录
- WHEN Service-Caller 调用 snapshot 接口且 includeInactive=true THEN THE SYSTEM SHALL 返回所有状态的记录
- WHEN 返回结果 THEN THE SYSTEM SHALL 支持分页（page / size 参数）
- THE SYSTEM SHALL 提供 Feign 接口：GET /api/service/mdm/material/v1/part/{code}（按 code 单点查询）
- THE SYSTEM SHALL 提供 Feign 接口：GET /api/service/mdm/material/v1/part/listByCategory?categoryCode={categoryCode}（按 categoryCode 批量查询）
- THE SYSTEM SHALL 提供 Feign 接口：GET /api/service/mdm/material/v1/part/listByVehicleNode?vehicleNodeCode={vehicleNodeCode}（按 vehicleNodeCode 批量查询）
- THE SYSTEM SHALL 提供 Feign 接口：GET /api/service/mdm/material/v1/part/listBySupplier?supplierCode={supplierCode}（按 supplierCode 批量查询）
- WHEN Service-Caller 调用单点查询接口且记录存在且 status=ACTIVE THEN THE SYSTEM SHALL 返回 Part 详情
- WHEN Service-Caller 调用单点查询接口且记录不存在或 status≠ACTIVE THEN THE SYSTEM SHALL 返回 404 或空响应
- WHEN Feign 调用失败 THEN THE SYSTEM SHALL 通过 FallbackFactory 返回空列表或 null 并记录日志

#### US-066: 数据来源记录（治理元数据）

**As a** System, **I want** 所有写入必须落齐治理元数据, **so that** 支持审计、追溯、裁决与下游识别。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 复用 US-015 已定义的数据来源记录机制：Part 与 MaterialCategory 主表及 history 表包含 source_system / source_id / source_version / ingestion_channel / ingestion_time / source_payload_hash 字段，填充规则与 Product MDM 各实体一致
- WHEN MDM-User 在 MDM 后台创建 Part 或 MaterialCategory THEN THE SYSTEM SHALL 设置 source_system=LOCAL、ingestion_channel=LOCAL、ingestion_time=now()
- WHEN MDM-User 在 MDM 后台对任意 Part 或 MaterialCategory 记录执行更新、失效、删除操作 THEN THE SYSTEM SHALL 按 US-059 / US-056 的常规规则处理，不因 source_system 字段差异施加额外限制
- THE SYSTEM SHALL 在对外发布的 Kafka 事件 payload 与 Feign 接口响应中携带来源字段（source_system / source_id / source_version / ingestion_channel / ingestion_time），供下游识别与对账
- THE SYSTEM SHALL 在 MDM 后台 Part 与 MaterialCategory 详情页面 / 列表页面展示来源字段

#### US-067: Part 生命周期状态机

**As a** System, **I want** Part 的 lifecycleStage 按状态机推进，禁止逆向跳转，OBSOLETE 为终态, **so that** 保证零件生命周期的业务合规性。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 定义 Part 的 lifecycleStage 状态机：PROTOTYPE → PRE_PRODUCTION → MASS_PRODUCTION → PHASE_OUT → OBSOLETE
- WHEN MDM-User 更新 Part 的 lifecycleStage 且新状态为当前状态的合法后继 THEN THE SYSTEM SHALL 允许更新
- WHEN MDM-User 更新 Part 的 lifecycleStage 且新状态为逆向跳转（如 MASS_PRODUCTION → PROTOTYPE）THEN THE SYSTEM SHALL 返回错误码 812915（PART_LIFECYCLE_INVALID_TRANSITION）并拒绝更新
- WHEN MDM-User 更新 Part 的 lifecycleStage 且当前状态为 OBSOLETE THEN THE SYSTEM SHALL 返回错误码 812915 并拒绝更新（OBSOLETE 为终态，禁止任何变更）
- THE SYSTEM SHALL 允许 lifecycleStage 与 status（DRAFT / ACTIVE / INACTIVE）独立管理，lifecycleStage 的变更不影响 status

#### US-068: Part 替代件设置

**As a** MDM-User, **I want** 为 Part 设置 substitutePartCode, **so that** 标识零件间的替代关系。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 创建或更新 Part 且 substitutePartCode 不为空 THEN THE SYSTEM SHALL 校验目标 Part 存在
- WHEN MDM-User 创建或更新 Part 且 substitutePartCode 指向不存在的 Part THEN THE SYSTEM SHALL 返回错误码 812914（PART_SUBSTITUTE_INVALID）并拒绝操作
- WHEN MDM-User 创建或更新 Part 且 substitutePartCode 指向自身（code=substitutePartCode）THEN THE SYSTEM SHALL 返回错误码 812914 并拒绝操作
- WHEN MDM-User 清空 Part 的 substitutePartCode THEN THE SYSTEM SHALL 允许操作

#### US-069: Part 与 MaterialCategory code 全局唯一校验

**As a** System, **I want** 校验 Part.code 与 MaterialCategory.code 全局唯一, **so that** 避免重复写入导致数据歧义。

**Acceptance Criteria** (EARS 语法):

- THE SYSTEM SHALL 在 mdm_material_part 表上对 code 列设置全局唯一约束（UK），由数据库层兜底
- THE SYSTEM SHALL 在 mdm_material_category 表上对 code 列设置全局唯一约束（UK），由数据库层兜底
- WHEN MDM-User 创建 Part 且 code 与表内已存在记录冲突 THEN THE SYSTEM SHALL 返回错误码 812910（PART_CODE_EXIST）并拒绝创建
- WHEN MDM-User 创建 MaterialCategory 且 code 与表内已存在记录冲突 THEN THE SYSTEM SHALL 返回错误码 812902（MATERIAL_CATEGORY_CODE_EXIST）并拒绝创建
- THE SYSTEM SHALL 在错误响应体中明确指出冲突的 code 取值与已占用记录的当前 status，便于排查

#### US-070: Part 删除前置依赖检查

**As a** System, **I want** 在 Part 删除前检查无下游引用, **so that** 为后续 BOM / SubstituteRelation 预留钩子。

**Acceptance Criteria** (EARS 语法):

- WHEN MDM-User 删除 Part 且 status=DRAFT THEN THE SYSTEM SHALL 物理删除记录（DRAFT 状态下游尚未感知，无需做反查）
- WHEN MDM-User 删除 Part 且 status=ACTIVE 或 status=INACTIVE THEN THE SYSTEM SHALL 检查是否存在其他 Part 的 substitutePartCode 指向该记录
- WHEN 检查结果存在引用 THEN THE SYSTEM SHALL 返回错误码 812916（PART_HAS_DOWNSTREAM_REF）并拒绝删除；错误响应中包含引用记录数量信息
- WHEN 检查结果不存在引用 THEN THE SYSTEM SHALL 物理删除记录并写入 Outbox 事件（eventType=PartDeleted，按 US-064）
- THE SYSTEM SHALL 限制删除接口的权限点为 mdm:material:part:remove
- THE SYSTEM SHOULD 提供管理员旁路标志（仅供 MDM-Admin 通过额外权限点 mdm:material:part:remove:force 使用），允许在已确认下游已清理的情况下跳过反查直接删除；旁路使用须记录审计日志

## 5. Constraints & Assumptions

### 业务约束

- **横向治理层定位**：edd-mdm 是横向主数据治理层，不替代业务系统主权
- **单一权威源**：首期只支持单一权威源（由 edd-mdm 内部 CRUD 维护），不实施多源合并
- **VMD 对齐**：架构风格与 VMD 项目保持一致
- **产品树层级关系**：Brand → CarLine → Model → Variant → Configuration，上层失效/删除时须校验下层无引用
- **Model 双归属**：Model 同时归属于 CarLine 和 Platform，创建时两者均须为 ACTIVE 状态
- **Option Family 互斥**：同一 Variant 下，同一 Option Family 最多只能绑定一个 Option Code；同一 Configuration 下同理
- **Configuration 归属**：Configuration 必须归属于一个 Variant
- **Configuration code 生成规则（CR-005）**：Configuration 的 code 不接受调用方传入；LOCAL 路径下系统按 `{variantCode}` + 7 位零填充自增序号生成（同一 Variant 下严格单调递增）；上游 ingest 路径下按 US-030 两层规则决定（命中 (source_system, source_id) 走幂等更新保持原 code；未命中按 code 是否被占用决定直采上游 code 或本地兜底生成）；code 全局唯一、不可变、序号只增不复用（DRAFT 物理删除不回收）
- **Variant code 长度上限**：为给 Configuration 自动拼接 7 位序号留足空间，Variant code 长度上限为 57 字符（57 + 7 = 64）
- **Option Code 归属**：Option Code 必须归属于一个 Option Family
- **删除前置依赖**：任何实体在物理删除前，须校验不存在下层实体或绑定关系的引用
- **Party MDM 共享基础设施**：Party MDM 与 Product MDM 共享同一 edd-mdm 服务实例、同一数据库 schema 前缀（mdm_*）、同一 Outbox / Ingestion Log / 权威源配置基础设施；角色定义（MDM-User / MDM-Admin / Upstream-System / Service-Caller / System）全部复用，不新增角色
- **EEAD 子域共享基础设施**：EEAD 子域与 Product MDM / Party MDM 共享同一 edd-mdm 服务实例、同一数据库 schema、同一 Outbox / Ingestion Log / 权威源配置基础设施；角色定义全部复用，不新增角色；技术栈与 DDD 四层骨架共用一份
- **EEAD 子域硬隔离矩阵**：EEAD 子域与 Product MDM / Party MDM 子域在以下六个层面**严格隔离**：

  | 隔离层 | Product MDM | Party MDM | EEAD |
  |---|---|---|---|
  | Java 包路径 | 沿用扁平结构（`...service.{adapter,application,domain,infrastructure}.*`） | 同左 | 同左 |
  | Java 类名命名前缀 | Brand* / CarLine* / Platform* / Model* / Variant* / Configuration* / OptionFamily* / OptionCode* | Supplier* | **VehicleNode\* / NodeType / FunctionalDomain / OtaSupportType / HsmCapability / SecurityLevel / VmdVehiclePart\***（业务命名前缀作为子域归属标识，与现有项目扁平结构一致） |
  | DB 表前缀 | `mdm_*`（无子域中缀，历史已用） | `mdm_*`（无子域中缀，历史已用） | `mdm_eead_*`（强制带 eead 中缀） |
  | Kafka topic 命名 | `mdm.product.<entity>.<op>` | `mdm.party.<entity>.<op>` | `mdm.eead.<entity>.event`（单一 topic + eventType 区分） |
  | Feign 服务接口路径前缀 | `/api/service/<entity>/v1/**`（历史已用） | `/api/service/<entity>/v1/**`（历史已用） | `/api/service/mdm/eead/v1/**`（强制带 eead 路径段） |
  | 权限点前缀 | `mdm:product:*`（建议规划） | `mdm:party:*`（建议规划） | `mdm:eead:*`（本期落地） |
  | 审批治理流程 | 产品 / 营销负责 | 采购 / 商务负责 | E/E 架构工程师负责 |

  > 说明：Java 包路径**不引入子域中缀**——所有子域的 DDD 对象都平铺在 `service.{adapter,application,domain,infrastructure}.*` 下，子域归属通过**类名命名前缀（VehicleNode\* / NodeType\* 等）**体现，与现有 Product / Party 子域风格一致，避免单子域过度设计。物理隔离主要靠表前缀、topic、Feign 路径、权限点 4 个层面落地。是否未来对齐 Product / Party 命名空间至 EEAD 风格的表前缀 / Feign 路径，由 Q12 跟踪。

- **VehicleNode 业务主键不可变**：nodeCode 一经创建即不可修改；如需变更视作"删除旧节点 + 新建新节点"，遵循 US-045 删除前置依赖检查规则
- **VehicleNode 删除策略**：本期 CR-007 采用"反查下游引用 + 硬拒绝"策略（详见 US-045 决策说明）；DRAFT 状态记录可直接物理删除，ACTIVE / INACTIVE 状态须先反查下游 source=MDM 的本地副本引用，存在引用则返回 812303 拒绝；MDM-Admin 通过专用权限点 `mdm:eead:vehicleNode:remove:force` 提供旁路能力但须留审计
- **VehicleNode 不开启上游接入路径**：本期 EEAD VehicleNode 不订阅 `upstream.<sourceSystem>.eead.vehicleNode` topic、不暴露 `POST /api/upstream/mdm/eead/vehicleNode/v1/ingest` 接口；所有 MDM 内 VehicleNode 记录由 MDM-User 在后台直接维护；source=MDM 取值仅作为字段语义保留位，供后续承接 VMD Device 数据迁移或开启上游接入时使用（详见 OS-17 / Q14）
- **EEAD 子域 Flyway 命名空间**：EEAD 子域的 Flyway 迁移脚本采用 `V*_EEAD__<desc>.sql` 命名规则，与 Product MDM / Party MDM 的迁移脚本（沿用 Flyway 默认命名）保持文件级隔离，便于审计与按子域回滚

- **Org 子域共享基础设施**：Org 子域与 Product MDM / Party MDM / EEAD 子域共享同一 edd-mdm 服务实例、同一数据库 schema、同一 Outbox / Ingestion Log / 权威源配置基础设施；角色定义全部复用，不新增角色；技术栈与 DDD 四层骨架共用一份
- **Org 子域硬隔离矩阵**：Org 子域与 Product MDM / Party MDM / EEAD 子域在以下六个层面**严格隔离**：

  | 隔离层 | Org 子域取值 |
  |---|---|
  | Java 包路径 | 沿用扁平结构（`...service.{adapter,application,domain,infrastructure}.*`），不引入子域包中缀 |
  | Java 类名命名前缀 | `Plant*` / `PlantType*` / `ProductionCapability*` 等（业务命名前缀作为子域归属标识） |
  | DB 表前缀 | `mdm_org_*`（强制带 `org` 中缀，如 `mdm_org_plant` / `mdm_org_plant_history`） |
  | Kafka topic 命名 | `mdm.org.plant.event`（单一 topic + eventType 区分，对齐 EEAD 风格），eventType ∈ {PlantCreated / PlantUpdated / PlantDeleted} |
  | Feign 路径前缀 | `/api/service/mdm/org/v1/**`（强制带 `org` 路径段） |
  | 权限点前缀 | `mdm:org:*`（细分至 `mdm:org:plant:list / query / add / edit / remove / export` 六点） |
  | 审批治理流程 | 由"生产运营 / 工厂规划"角色负责（与 Product / Party / EEAD 物理隔离） |
  | Flyway 命名 | `V*_ORG__<desc>.sql` 命名规则，与其他子域文件级隔离 |

- **Plant 业务主键不可变**：code 一经创建即不可修改；如需变更视作"删除旧工厂 + 新建新工厂"，遵循 US-054 删除前置依赖检查规则
- **Plant 删除策略**：沿用 US-045（VehicleNode）的"反查下游引用 + 硬拒绝"策略（详见 US-054 决策说明）；DRAFT 状态记录可直接物理删除，ACTIVE / INACTIVE 状态须先反查下游 source=MDM 的本地副本引用，存在引用则返回 812503 拒绝；MDM-Admin 通过专用权限点 `mdm:org:plant:remove:force` 提供旁路能力但须留审计
- **Plant 不开启上游接入路径**：本期 Org Plant 不订阅 `upstream.<sourceSystem>.org.plant` topic、不暴露 `POST /api/upstream/mdm/org/plant/v1/ingest` 接口；所有 MDM 内 Plant 记录由 MDM-User 在后台直接维护；source_system 字段本期均为 LOCAL，保留扩展语义供后续 ERP 上线后开启上游接入时使用（届时上游推送的记录 source_system=ERP、ingestion_channel=KAFKA/FEIGN，详见 OS-23 / Q18）
- **Plant 本期 ERP 未上线**：Plant 的行业 SSOT 是 ERP（SAP S/4HANA Plant / WERKS），本期 ERP 尚未上线，MDM 暂代行 Plant SSOT 职责（source_system 默认 LOCAL）；未来 ERP 上线后，Plant 子域降级为 ERP 的本地副本 + 治理层，权威源切换流程由 Q18 跟踪
- **Plant 位置属性内嵌**：工厂同时具备组织属性与位置属性；本期归 Org 子域（组织维度优先），位置属性（country / province / city / address / longitude / latitude / timezone）以扁平字段形式内嵌 Plant 实体，不抽出独立 Location 子域
- **Plant 引用字段暂不做外键**：`legalEntityCode` / `costCenterCode` 本期作为字符串字段不做引用完整性校验，未来 Legal Entity / Cost Center 落地后升级为外键（见 Q16）
- **Org 子域 Flyway 命名空间**：Org 子域的 Flyway 迁移脚本采用 `V*_ORG__<desc>.sql` 命名规则，与 Product MDM / Party MDM / EEAD 的迁移脚本保持文件级隔离

- **Material 子域共享基础设施**：Material 子域与 Product MDM / Party MDM / EEAD / Org 子域共享同一 edd-mdm 服务实例、同一数据库 schema、同一 Outbox / Ingestion Log / 权威源配置基础设施；角色定义全部复用，不新增角色；技术栈与 DDD 四层骨架共用一份
- **Material 子域硬隔离矩阵**：Material 子域与 Product MDM / Party MDM / EEAD / Org 子域在以下六个层面**严格隔离**：

  | 隔离层 | Material 子域取值 |
  |---|---|
  | Java 包路径 | 沿用扁平结构（`...service.{adapter,application,domain,infrastructure}.*`），不引入子域包中缀 |
  | Java 类名命名前缀 | `Part*` / `MaterialCategory*` / `PartType*` / `LifecycleStage*` 等（业务命名前缀作为子域归属标识） |
  | DB 表前缀 | `mdm_material_*`（强制带 `material` 中缀，如 `mdm_material_part` / `mdm_material_part_history` / `mdm_material_category` / `mdm_material_category_history`） |
  | Kafka topic 命名 | `mdm.material.part.event` / `mdm.material.category.event`（单一 topic + eventType 区分，与 EEAD / Org 同模式） |
  | Feign 路径前缀 | `/api/service/mdm/material/v1/**`（强制带 `material` 路径段） |
  | 权限点前缀 | `mdm:material:*`（细分至 `mdm:material:part:list / query / add / edit / remove / export` 与 `mdm:material:category:list / query / add / edit / remove / export` 六点 × 2 实体） |
  | 审批治理流程 | 由"物料工程 / 采购工程"角色负责（与 Product / Party / EEAD / Org 物理隔离） |
  | Flyway 命名 | `V*_MATERIAL__<desc>.sql` 命名规则，与其他子域文件级隔离 |

- **Part 业务主键不可变**：code 一经创建即不可修改；如需变更视作"删除旧零件 + 新建新零件"，遵循 US-070 删除前置依赖检查规则
- **Part 引用完整性**：Part 的 categoryCode 必须指向有效且 ACTIVE 的 MaterialCategory；vehicleNodeCode 与 supplierCode 为可选字段，非空时必须指向有效记录
- **Part 生命周期状态机**：lifecycleStage 按 PROTOTYPE → PRE_PRODUCTION → MASS_PRODUCTION → PHASE_OUT → OBSOLETE 单向推进，禁止逆向跳转，OBSOLETE 为终态
- **Part 删除策略**：采用"反查下游引用 + 硬拒绝"策略（沿用 VehicleNode / Plant 同款）；DRAFT 状态记录可直接物理删除，ACTIVE / INACTIVE 状态须先反查其他 Part 的 substitutePartCode 引用，存在引用则返回 812916 拒绝；MDM-Admin 通过专用权限点 `mdm:material:part:remove:force` 提供旁路能力但须留审计
- **MaterialCategory 树形结构防环**：MaterialCategory 的 parentCode 自关联支持树形层级，系统须在创建/更新时检测环路并拒绝
- **MaterialCategory 删除保护**：存在子项 MaterialCategory 或被 Part 引用时，禁止删除
- **Material 子域本期不开启上游接入路径**：本期 Material 不订阅上游 Kafka topic、不暴露 `POST /api/upstream/mdm/material/*/ingest` 接口；所有 MDM 内 Material 记录由 MDM-User 在后台直接维护；source_system 字段保留扩展语义，供后续 PLM / ERP 上线后开启上游接入时使用
- **VMD 零件数据降级为本地投影**：VMD 中的零件数据降级为 MDM Material 的本地投影（VMD 不再作为零件主数据源），通过订阅 MDM Material 事件完成本地副本同步
- **Org 子域错误码段位**：Org 子域分配 **8125XX 段**（与 Product 的 8121XX、EEAD 的 8123XX、Party 的 8127XX、Material 的 8129XX 错开）：812501（PLANT_NOT_EXIST）/ 812502（PLANT_CODE_EXIST）/ 812503（PLANT_HAS_DOWNSTREAM_REF）/ 812504（PLANT_EFFECTIVE_PERIOD_INVALID）/ 812510 ~ 812513（预留给未来上游接入路径）

- **MDM 字段归属判断 checklist**（CR-010 引入，后续 CR 复用的治理准则）：判断一个字段是否应纳入 MDM Golden Record，须同时通过以下四问——
  - **Q1**：该字段在不同渠道 / 区域 / 时间窗下取值是否会不同？→ 会，则不属于 MDM
  - **Q2**：去掉该字段，下游能否自行计算或配置？→ 能，则不属于 MDM
  - **Q3**：该字段描述"实体本身是什么"还是"实体如何被使用 / 展示"？→ 后者不属于 MDM
  - **Q4**：多个下游引用该字段时取值一致性是否被天然保证？→ 不一致则不属于 MDM

  > **示例（CR-010 Option Family）**：`category` 四问全部命中"应该在 MDM"（全渠道一致、不可由下游计算、描述实体本身是什么、MDM 单源保证一致），故纳入；`is_customer_visible` / `selection_type` / `display_order` / `icon_url` 不满足 Q3（属于"如何被展示"），不入 MDM Golden Record，归商品中心 / 销售配置器管理（见 OS-31 / OS-32）。

### 前置条件

- VMD 项目已完成品牌 / 车系 / 平台主数据的本地投影副本改造（引用 MDM 数据）
- 下游系统（订单、销售配置器、BI）已具备消费 Kafka 事件或调用 Feign 接口的能力

### 角色定义

- **MDM-User**：MDM 后台运营 / 工程师，持有 `mdm:product:*` 权限点
- **MDM-User（Org）**：MDM 后台生产运营 / 工厂规划角色，持有 `mdm:org:*` 权限点
- **MDM-User（Material）**：MDM 后台物料工程 / 采购工程角色，持有 `mdm:material:*` 权限点
- **Service-Caller**：内部微服务（VMD / 订单 / 销售配置器等）通过 Feign 或 Kafka 订阅消费 MDM 数据
- **System**：edd-mdm 自身后台异步流程（事件发布、Outbox Relay、上游消息消费等）
- **Upstream-System**：业务源头权威的上游系统（如 PLM / DMS / 集团主数据平台），通过 Kafka 消息或 Feign / HTTP 接口推送 Brand / CarLine / Platform 主数据至 edd-mdm；每个上游系统对应唯一的 sourceSystem 编码
- **MDM-Admin**：MDM 管理员，持有 `mdm:admin:*` 权限点，负责权威源配置、上游来源注册、接入审计与监控查询等运维操作

## 6. Out of Scope

| 编号 | 内容 | 原因 |
|------|------|------|
| OS-1 | VMD 的 VIN / 零部件 / 生命周期 | 仍由 VMD 持有 |
| OS-2 | Customer / Material / Employee / Location MDM 子域 | 未来扩展。Customer 未来归属 Party MDM；Material 未来归属 Product MDM；Employee 未来归属 Org MDM；Location 未来归属 Location MDM |
| OS-3 | Manufacturer | 留待后续 CR |
| OS-4 | 字段级多源合并裁决（field-level merge） | 首期采用单一权威源策略，每条主数据由唯一权威源写入；支持多上游接入但不做字段级合并 |
| OS-5 | 数据质量打分引擎 | 未来扩展 |
| OS-6 | 跨系统 ID 映射表 | 留待后续 CR |
| OS-7 | 审批工作流 | 留待后续 CR；首期 CRUD 即发布 |
| OS-8 | 对账机制 | 留待后续 CR |
| OS-9 | 跨车型的选项码兼容矩阵（哪些选项码可用于哪些车型） | 业务复杂度高，留待后续 CR |
| OS-10 | 选项码间的依赖/排斥规则引擎（如选了 A 则必须选 B、不能选 C） | 属于销售配置器/CPQ 领域能力，不在 MDM 范围 |
| OS-11 | 选项码定价与商务属性 | 属于销售/财务域，MDM 仅管理主数据标识 |
| OS-12 | 配置与车辆实例（VIN）的关联 | 仍由 VMD 持有 |
| OS-13 | Party MDM 下的 Dealer / Customer / 其他业务伙伴实体 | 本期不做，留待后续 CR |
| OS-14 | Supplier 与 Part / Device / Manufacturer 的关联关系 | 本期不做，留待 Product MDM 纳入物料/设备时统一设计 |
| OS-15 | EEAD 子域中除车载节点外的其余四块（通讯矩阵 / 诊断架构 / 刷写 / OTA 拓扑 / 信息安全架构） | 本期不做，每块留待独立 CR 立项 |
| OS-16 | 车载节点 ↔ 平台 ↔ 车型 ↔ 架构代次 的多对多关联表 | 本期不做，归属 EEAD 子域内"架构代次子能力"，由后续 CR 立项 |
| OS-17 | VMD 侧 Device 历史数据迁移到 MDM mdm_eead_vehicle_node 表的数据迁移脚本 | 本期不做，由独立的数据迁移 CR 完成（视为生产数据搬迁工程） |
| OS-18 | VMD 侧本地副本接入（含 VMD 订阅 mdm.eead.vehicleNode.event topic、消费 Feign 全量快照、Device 实体降级为只读投影副本）| 本期不做，由 VMD 侧后续 CR（暂记 VMD-CR-013）承接，本 spec 仅承诺上游对外发布契约 |
| OS-19 | Legal Entity / BU / Department / Cost Center / Warehouse 等其余 Org 子域实体 | 本期不做，留待后续 CR（Org 子域仅落地 Plant） |
| OS-20 | Plant ↔ 产线 / 车间 / 设备的层级关联 | 本期不做，留待后续 CR |
| OS-21 | VMD 侧 Manufacturer 历史数据迁移到 mdm_org_plant 表的数据迁移脚本 | 本期不做，由独立的数据迁移 CR 完成（参考 OS-17 EEAD 同款） |
| OS-22 | VMD 侧本地副本接入（含 VMD 订阅 `mdm.org.plant.event` topic、Feign 全量快照消费、Manufacturer 实体降级为只读投影副本并改名 plantCode）| 本期不做，由 VMD-CR-014 承接，本 CR 仅承诺上游对外发布契约 |
| OS-23 | ERP 上游接入路径（订阅 `upstream.<sourceSystem>.org.plant` topic、暴露 `POST /api/upstream/mdm/org/plant/v1/ingest` 接口）| 本期不开启，留待 ERP 上线后独立 CR 立项 |
| OS-24 | 工厂的产能 / 排产 / 人员编制 / 班次 等运营业务功能 | 不在 MDM 范围（归生产运营 / HCM / MES 系统） |
| OS-25 | BOM（物料清单）管理 | Material 子域候选实体，本期不落地，留待后续 CR |
| OS-26 | SubstituteRelation（替代关系）独立实体管理 | 本期仅通过 Part.substitutePartCode 字段支持简单替代件标注，完整的替代关系管理（含生效条件、优先级、用量换算等）留待后续 CR |
| OS-27 | Material 子域上游接入路径（PLM / ERP / SRM 推送物料主数据）| 本期不开启，留待后续 CR |
| OS-28 | Part 与 VehicleNode 的多对多关联（一个 Part 可适配多个 VehicleNode）| 本期仅支持 Part 单点引用一个 vehicleNodeCode，多对多关联留待后续 CR |
| OS-29 | Part 的成本 / 价格 / 采购属性 | 属于采购 / 财务域，MDM 仅管理物料主数据标识 |
| OS-30 | Part 的质量属性（合格率、不良率、质检标准）| 属于质量管理域，不在 MDM 范围 |
| OS-31 | Option Family 的 `selection_type` 字段（multi-select 场景）| 本期不引入，沿用 US-021 / US-023 的隐式 SINGLE 约束（同一 Option Family 最多一个 Option Code）；未来如出现真实 MULTIPLE 需求（如组合优惠包）另开 CR 补字段 |
| OS-32 | Option Family 的 `is_customer_visible` / `display_order` / `icon_url` 字段 | 渠道可见性 / 展示顺序 / 图标归商品中心 / 销售配置器管理，不入 MDM Golden Record（不满足 MDM 字段归属判断 checklist Q3：属于"如何被展示"而非"实体本身是什么"） |

## 7. Open Questions

| 编号 | 问题 | 影响范围 | 建议（待业务确认） |
|------|------|----------|-------------------|
| Q1 | 上层失效（如 Model → INACTIVE）时，是否级联失效下层（Variant / Configuration）？还是仅校验阻止？ | US-019 / US-020 / US-022 | **已决议：采用方案 B（阻止失效 + 依赖检查）**。失效父级时检查是否存在 ACTIVE 状态的子级，存在则拒绝失效并返回错误码（812116~812122）。详见 Changelog CR-010 |
| Q2 | Configuration 的**物理删除**应限定在何种状态？MDM 内无法感知车辆实例的引用情况。 | US-022 | **已决议：采用方案 A（仅 DRAFT 可物理删除）**。ACTIVE / INACTIVE 状态仅支持失效（软删除），物理删除仅限 DRAFT 状态。理由：MDM 内无法感知下游车辆实例（VIN）对 Configuration 的引用，为避免悬空引用，采用保守策略 |
| Q3 | 下层引用上层时，是否要求上层 status = ACTIVE？还是允许引用 INACTIVE 状态的上层？ | US-019 ~ US-026 | **已决议：采用方案 A（新数据要求上层 ACTIVE，历史数据保留原引用）**。创建新记录时必须校验上层为 ACTIVE 状态；更新记录时若引用的上层未变更则保留历史引用不重新校验，若引用的上层发生变更则校验新上层为 ACTIVE 状态。理由：新业务数据不应再关联已失效的业务实体，但历史数据的引用关系应保持稳定 |
| Q4 | Option Code 是否需要跨车型/版本/配置的全局唯一性？还是允许在不同上下文中复用同一 Option Code？ | US-026 | **已决议：采用方案 A（全局唯一）**。Option Code 在整个 mdm_option_code 表中全局唯一，不允许不同 Option Family 下复用同一 code。理由：(1) 行业标准对齐（SAP Characteristic Value、GM RPO Code、VW PR-Nummer 均全局唯一）；(2) 新势力车企实践（产品线简单、数字化原生、系统集成需清晰标识）；(3) 下游简单（订单/销售配置器/生产系统直接用 code 定义，无需带上下文维度）。code 命名建议带前缀区分语义（如 CLR_RED、ROOF_PANORAMIC） |
| Q5 | Variant 与 Option Code 绑定的"同一 Option Family 互斥"约束，是否在所有 Variant 上都强制？是否存在例外场景？ | US-021 | **已决议：采用方案 A（全局强制，无例外）**。Variant 和 Configuration 均遵守同一 Option Family 互斥约束，不允许同一 Option Family 绑定多个 Option Code。理由：(1) Variant 标识版本特征，一个版本在同一维度应只有一个标识；(2) Configuration 是具体可生产规格，一个配置在同一维度只能有一个取值；(3) "多选"场景通过不绑定该 Option Family + 多个 Configuration 实现（如 Variant 不绑定颜色族，每个 Configuration 各绑定一个颜色） |
| Q6 | Configuration 绑定的 Option Code 集合，是否要求"覆盖所有必选 Option Family"？还是允许部分缺失？ | US-023 | **已决议：采用方案 A（不要求全覆盖）**。Configuration 可以只绑定部分 Option Family，不要求覆盖所有 Option Family。理由：(1) 新势力车企采用渐进式配置管理，不要求一次配齐；(2) "必选"的定义在不同车型/版本下可能不同，实现复杂；(3) 下游系统（订单/销售配置器）可自行判断哪些是必选项 |
| Q7 | 按 Option Code 组合反查 Configuration 的匹配语义，是"精确匹配"还是"包含匹配"？是否支持模糊/通配？ | US-024 | 待定 |
| Q8 | Supplier 的核心字段中，哪些属于 Golden Record 必备字段、哪些属于业务系统私有字段？（影响 schema 设计与上游接入 payload 契约） | US-031 | **已决议：首期全量纳入 US-031 定义的字段集，后续按业务反馈裁剪**。理由：(1) 新势力车企供应商数据量不大，全量管理成本可控；(2) 先跑通流程，后续根据实际使用情况裁剪；(3) 避免因字段遗漏导致下游系统抱怨 |
| Q9 | 同一公司在不同业务场景下（如零部件供应商 vs 服务供应商）是否需要拆成多条 Supplier 记录？还是用 supplier_type 字段在一条记录上区分？ | US-031 | **已决议：采用方案 A（一条记录 + supplier_type 多选）**。同一公司只维护一条 Supplier 记录，supplier_type 改为支持多选（VARCHAR(256) 逗号分隔或 JSON 数组），可同时标记为 MATERIAL、SERVICE 等多种类型。理由：(1) 避免数据冗余（公司基础信息只需维护一次）；(2) 符合"主数据单一 Golden Record"理念；(3) supplier_type 已定义枚举（MATERIAL / COMPONENT / SERVICE / LOGISTICS / OTHER） |
| Q10 | Supplier 的状态机除 ACTIVE / INACTIVE / DRAFT 外，是否需要 BLACKLISTED（黑名单/冻结）等业务状态？ | US-031 | **已决议：采用方案 A（首期三态，BLACKLISTED 留待后续 CR）**。首期仅 DRAFT / ACTIVE / INACTIVE 三态，与 Product MDM 各实体状态机保持一致。"黑名单"语义可通过 INACTIVE + description 字段标注实现，后续根据实际业务需求再决定是否扩展 BLACKLISTED 状态 |
| Q11 | 失效（deactivate）一个 Supplier 时，是否需要校验下层引用（如 Part / 采购订单等）？由于这些实体不在 MDM 内，建议失效时仅发布事件，由下游各自决定是否拒绝引用 | US-031 | **已决议：采用方案 A（不校验，仅发布 deactivated 事件）**。Supplier 失效时不校验下层引用，仅发布 mdm.party.supplier.deactivated 事件，由下游系统（采购、SRM 等）自行决定是否拒绝引用。理由：(1) MDM 无法感知采购订单、SRM 等外部系统的引用；(2) 与 US-031 AC 中已采纳的方向一致；(3) 下游系统应自行处理 Supplier 失效事件 |
| Q12 | 错误码段位已统一：各子域错误码段位规划为 Product=8121XX、EEAD=8123XX、Org=8125XX、Party=8127XX、Material=8129XX。已完成迁移：(1) Product/Party 原 807xxx 错误码已迁移至 8121XX/8127XX 段；(2) EEAD 原 812xxx 错误码已迁移至 8123XX 段；(3) Org 原 813xxx 错误码已迁移至 8125XX 段；(4) Material 原 814xxx 错误码已迁移至 8129XX 段 | §5 / 全部 US 错误码引用 / design §5.5 错误码表 | **已决议：采用方案 B（统一迁移至 812xxx 段）**。各子域错误码段位规划：Product=8121XX、EEAD=8123XX、Org=8125XX、Party=8127XX、Material=8129XX。已完成迁移 |
| Q13 | VehicleNode 删除策略本期采用"反查下游引用 + 硬拒绝"（US-045 方案 A），未来是否需要切换为"软删除标记 + 下游事件自决"策略？ | US-045 | **已决议：维持方案 A（反查 + 硬拒绝）**。本期采用保守策略，运行 3-6 个月后复盘：若反查 VMD 的 Feign 调用失败率 > 5% 或下游事件处理流程已成熟，再立 CR 切换为软删除策略 |
| Q14 | VehicleNode 是否需要开启上游接入路径（订阅 `upstream.<sourceSystem>.eead.vehicleNode` topic 与暴露 `POST /api/upstream/mdm/eead/vehicleNode/v1/ingest` 接口）？本期默认仅 LOCAL 维护，source 字段保留 MDM 取值供未来扩展使用 | US-046 / OS-17 | **已决议：本期不开启**。所有 VehicleNode 由 MDM-User 后台直接维护（source=MANUAL）；source=MDM 字段保留语义，供后续承接 VMD Device 数据迁移或开启上游接入时使用 |
| Q15 | Plant 的 `code` 是否需要符合预设命名规范（如 `PLT_<国家>_<城市>_<序号>`）？是否在系统层做正则校验，还是仅在用户文档约定？ | US-047 | **已决议：仅文档约定**。建议命名规范 `PLT_<国家代码>_<城市代码>_<序号>`（如 `PLT_CN_CD_01`），系统层不做正则校验，由用户自律遵守 |
| Q16 | `legalEntityCode` / `costCenterCode` 本期作为字符串字段不做引用完整性校验，未来 Legal Entity 子域落地时是否回填外键约束 + 数据回扫？ | US-047 / §5 约束 | **已决议：接受建议**。本期作为字符串字段存储，未来 Legal Entity / Cost Center 实体落地时（见 OS-19）回填外键约束并执行数据回扫脚本 |
| Q17 | Plant 失效（INACTIVE）是否需要进一步校验下层引用？参考 Supplier（Q11 决议为不校验，仅发事件）vs VehicleNode（US-045 反查 + 硬拒绝）两种范式选择 | US-054 | **已决议：采用反查 + 硬拒绝策略**。沿用 VehicleNode（US-045）的删除策略，Plant 失效时同样执行反查校验，因为 Manufacturer / Plant 在 VMD 中是 Vehicle 的强引用字段，悬空风险更高 |
| Q18 | 未来 ERP 上线后，Plant 的权威源切换（LOCAL → ERP）流程是否需要独立的灰度方案？ | US-055 / OS-23 / §5 约束 | **已决议：接受建议**。未来 ERP 上线时立独立 CR 设计权威源切换灰度方案，含双写期、数据校验期、切换期三阶段，参考 US-017 权威源配置机制扩展 |
| Q19 | Manufacturer（制造商，语义更宽，可对应多个 Plant）与 Plant 是否需要拆为两个独立实体？ | US-047 / §1 Overview | **已决议：本期合并**。一条 Plant 记录即代表一个独立物理工厂，由 legalEntityCode 间接表达制造商归属；如未来出现"一家制造商有多个 Plant 需要在 MDM 内显式建模"的诉求，再立独立 CR 拆分 |
| Q20 | Part 的 partType 枚举是否需要更细化的分类？如区分原材料 / 标准件 / 定制件 / 软件件 / 总成件 / 电子件 / 机械件等 | US-059 | **已决议：首期 5 类**。采用 RAW_MATERIAL / STANDARD_PART / CUSTOM_PART / SOFTWARE / ASSEMBLY，未来按业务反馈扩展 |
| Q21 | Part 的 vehicleNodeCode 是否允许引用 status≠ACTIVE 的 VehicleNode？ | US-061 | **已决议：仅允许 ACTIVE**。与 MaterialCategory 引用策略一致，创建/更新时校验 VehicleNode 为 ACTIVE 状态 |
| Q22 | MaterialCategory 的树形层级深度是否需要限制？ | US-058 | **已决议：不做系统限制**。在用户文档约定合理深度（如不超过 5 层），系统层不做硬编码限制 |
| Q23 | Part 的 substitutePartCode 是否需要支持一对多替代关系（一个零件有多个可选替代件）？ | US-068 | **已决议：首期一对一**。仅通过 substitutePartCode 字段支持一对一替代，一对多替代关系留待 OS-26 SubstituteRelation 独立实体管理 |
| Q24 | Part 的 fotaUpgradeable 字段与 VehicleNode 的 otaSupportType 字段如何协同？当物料级声明 fotaUpgradeable=true 但节点级声明 otaSupportType=NOT_SUPPORTED 时如何处理？ | US-059 | **已决议：独立管理，不做校验**。两个字段分别表达物料级和节点级的 OTA 能力声明，不做强制一致性校验，由下游 OTA 服务自行决策 |
| Q25 | Material 子域的错误码段位已统一为 8129XX 段，与其他子域对齐（Product=8121XX、EEAD=8123XX、Org=8125XX、Party=8127XX） | §5 | **已决议：采用 8129XX 段**，与其他子域统一规范 |
| Q26 | 现有 Option Family 数据迁移时 `category` 字段如何回填？ | US-071 / 数据迁移 | **建议方向：建表时设 `DEFAULT 'OTHER'`，由 MDM-User 后台批量修订**。理由：(1) 存量 Option Family 数量有限，人工修订成本可控；(2) 自动映射规则难以准确（需理解每个 Option Family 的业务语义）；(3) DEFAULT 'OTHER' 为安全兜底，不影响下游消费 |

## 8. Impact Analysis（业务层影响）

### VMD 项目

- VMD 侧的车型 / 版本 / 配置 / 选项族 / 选项码后台维护能力需降级为**只读**（数据来源切换为 MDM 本地投影副本）
- VMD 侧的车型 / 版本 / 配置查询能力需切换为消费 MDM 事件 + 本地副本模式（沿用 CR-010 已落地的本地投影副本模式）
- VMD 侧对应的下游迁移由 VMD-CR-011 承接
- VMD-CR-012 承接 Supplier 从 VMD 迁移至 MDM 的下游改造：VMD 侧 Supplier 数据切换为消费 MDM Party 事件 + 本地投影副本模式（参考 VMD-CR-010 的 source / external_ref_id / external_version / last_sync_time 投影副本模式）

### 下游消费方

- VSO（车辆销售订单）、订单域、生产域、销售域需要扩展 Kafka 订阅范围，新增 Model / Variant / Configuration / Option Family / Option Code 5 类事件的消费
- 下游需扩展 Feign 全量快照对账范围，覆盖 5 类新实体
- 按选项码组合反查配置（US-024）为订单/销售域核心高频能力，下游需对接该查询接口
- 采购系统 / SRM / 财务系统需订阅 Party MDM Supplier 事件（`mdm.party.supplier.*`），完成供应商主数据的本地同步或引用切换

### 上游系统

- 需评估是否新增产品主数据的上游推送通道（如 PLM、CPQ、产品定义系统）
- 若存在上游系统直接维护车型/版本/配置/选项族/选项码，需完成来源注册与权威源配置
- 需评估是否新增 ERP / SRM / 集团供应商主数据平台作为 Supplier 的上游推送方，完成来源注册与权威源配置

### EEAD 子域 - 车载节点上线影响

#### 对 VMD 项目的影响（VMD-CR-013 承接）

- VMD 侧的 `Device` 实体降级为本地投影副本（沿用 VMD-CR-010 的 `source / external_ref_id / external_version / last_sync_time` 投影副本模式：source=MDM 的记录在 VMD 后台只读）；MDM-User 在 MDM 后台维护的 VehicleNode 通过 `mdm.eead.vehicleNode.event` topic 同步到 VMD 本地副本
- VMD 侧 Device 后台 CRUD 能力在 VMD-CR-013 落地后降级为：source=MANUAL 的本地记录可正常 CRUD；source=MDM 的本地记录只读，仅由订阅 MDM 事件刷新
- VMD 侧 Device 历史数据迁移到 MDM `mdm_eead_vehicle_node` 由独立的数据迁移 CR 承接（详见 OS-17），VMD-CR-013 不展开数据搬迁工程
- VMD 侧 VehiclePart 等引用 Device 的实体须在 VMD-CR-013 完成 Feign 反向查询接口的暴露（供 MDM US-045 反查使用）

#### 对 OTA 服务的影响

- OTA 服务可基于 US-044 提供的"按 OTA 类型批量查询"接口圈选可推送的目标节点集合（FOTA / SOTA / BOTH / NOT_SUPPORTED）
- OTA 服务可基于 US-043 单点查询节点的 `hsmCapability` / `securityLevel` 字段，做安全策略判定（如 NONE 节点禁止下发签名包）
- OTA 服务需通过 Feign 改为消费 MDM 的 VehicleNode 节点能力声明（取代原直接读 VMD Device 表的旧方案）

#### 对诊断服务的影响

- 诊断服务后续若需要按节点类型、功能域、是否核心节点等维度做诊断策略圈选，可消费 MDM VehicleNode 主数据
- 诊断架构本身（DID / DTC / 诊断会话等）归属 EEAD 子域内"诊断架构子能力"，由后续 CR 立项（详见 OS-15），与本期 VehicleNode 解耦

#### 对刷写 / OTA 拓扑的影响

- 刷写顺序、刷写协议、节点间刷写依赖等归属 EEAD 子域内"刷写 / OTA 拓扑子能力"，由后续 CR 立项（详见 OS-15）
- 本期 VehicleNode 仅承载 `otaSupportType` 这一节点能力声明字段，不涉及刷写顺序与拓扑

#### 对配置 / 销售配置器的影响

- 销售配置器若已基于 VMD Device 做"车型可选硬件节点"列表，需切换为消费 MDM 的 VehicleNode 全量快照（US-042）；source=MDM 的本地副本由 VMD 同步刷新
- 节点 ↔ 平台 ↔ 车型 ↔ 架构代次的关联（多对多）归属"架构代次子能力"，由后续 CR 立项（详见 OS-16），本期不可基于 VehicleNode 做车型可选节点的精确圈选

#### 对权限治理的影响

- 需在权限服务（IAM / Casbin / RBAC 后台）注册新的权限点：`mdm:eead:vehicleNode:list / query / add / edit / remove / export`，以及管理员旁路点 `mdm:eead:vehicleNode:remove:force`
- E/E 架构工程师角色须通过 MDM-Admin 在权限后台分配 EEAD 权限点，与 Product / Party MDM 的运营角色权限点物理隔离（不复用同一角色组）

### Org 子域 - 工厂上线影响

#### 对 VMD 项目的影响（VMD-CR-014 承接）

- VMD 侧的 `Manufacturer` 实体降级为本地投影副本（沿用 VMD-CR-010 的 `source / external_ref_id / external_version / last_sync_time` 投影副本模式：source=MDM 的记录在 VMD 后台只读）；MDM-User 在 MDM 后台维护的 Plant 通过 `mdm.org.plant.event` topic 同步到 VMD 本地副本
- VMD 侧 `Vehicle.manufacturerCode` 字段改名为 `plantCode` 并切换为消费 MDM Plant 同步数据（VMD-CR-014 承接）
- VMD 侧 Manufacturer 历史数据迁移到 MDM `mdm_org_plant` 由独立的数据迁移 CR 承接（详见 OS-21），VMD-CR-014 不展开数据搬迁工程
- VMD 侧须暴露反向查询接口供 MDM US-054 删除前置依赖检查使用（VMD-CR-014 承接）

#### 对 VSO 的影响

- VSO 订单中的"交付工厂"字段切换为消费 MDM Plant 事件（`mdm.org.plant.event`）

#### 对数仓的影响

- DWS 车辆静态主数据宽表中的 `manufacturer_code` 维度更名 `plant_code` 并切换为关联 MDM Plant

#### 对未来 MES / WMS 的影响

- MES / WMS 作为 Plant SSOT 的下游消费方接入，通过 Feign 全量快照（US-051）或 Kafka 事件（US-050）获取工厂主数据

#### 对权限治理的影响

- 需在权限服务（IAM / Casbin / RBAC 后台）注册新的权限点：`mdm:org:plant:list / query / add / edit / remove / export`，以及管理员旁路点 `mdm:org:plant:remove:force`
- 生产运营 / 工厂规划角色须通过 MDM-Admin 在权限后台分配 Org 权限点，与 Product / Party / EEAD MDM 的运营角色权限点物理隔离（不复用同一角色组）

### Material 子域 - 物料上线影响

#### 对 VMD 项目的影响（VMD-CR-015 承接）

- VMD 侧的零件数据降级为本地投影副本（沿用 VMD-CR-010 的 `source / external_ref_id / external_version / last_sync_time` 投影副本模式：source=MDM 的记录在 VMD 后台只读）；MDM-User 在 MDM 后台维护的 Part 通过 `mdm.material.part.event` topic 同步到 VMD 本地副本
- VMD 侧零件后台 CRUD 能力在 VMD-CR-015 落地后降级为：source=MANUAL 的本地记录可正常 CRUD；source=MDM 的本地记录只读，仅由订阅 MDM 事件刷新
- VMD 侧零件历史数据迁移到 MDM `mdm_material_part` 由独立的数据迁移 CR 承接，VMD-CR-015 不展开数据搬迁工程

#### 对采购系统 / SRM 的影响

- 采购系统可订阅 MDM Material 事件（`mdm.material.part.event`），获取零件主数据用于采购订单、供应商协同等场景
- SRM 可通过 Feign 全量快照（US-065）获取 Part 列表，按 supplierCode 过滤获取特定供应商的零件清单

#### 对 OTA 服务的影响

- OTA 服务可基于 Part 的 `fotaUpgradeable` 字段圈选可 OTA 升级的物料级目标
- OTA 服务可结合 VehicleNode 的 `otaSupportType`（架构级）与 Part 的 `fotaUpgradeable`（物料级）做联合决策

#### 对 BOM / 制造域的影响

- BOM 系统（未来）可消费 MDM Part 主数据作为 BOM 构建的零件字典
- 制造域可基于 Part 的 lifecycleStage 信息做生产排程决策（如 PROTOTYPE 零件仅用于试制车间）

#### 对权限治理的影响

- 需在权限服务（IAM / Casbin / RBAC 后台）注册新的权限点：`mdm:material:part:list / query / add / edit / remove / export` 与 `mdm:material:category:list / query / add / edit / remove / export`，以及管理员旁路点 `mdm:material:part:remove:force`
- 物料工程 / 采购工程角色须通过 MDM-Admin 在权限后台分配 Material 权限点，与其他子域的运营角色权限点物理隔离

## 9. Changelog

| Date | Change ID | Type | Description |
|------|-----------|------|-------------|
| 2026-05-26 | CR-001 | Added | 首版产出：建立 Product MDM 子域（Brand / CarLine / Platform）需求文档 |
| 2026-05-26 | CR-002 | Added | 新增"域 5: 上游系统数据接入"（US-013 ~ US-018）：支持通过 Kafka / Feign 接收上游推送，新增数据来源字段（source_system / source_id / source_version / ingestion_channel / ingestion_time / source_payload_hash），新增幂等处理、权威源配置与冲突裁决（REJECT / AUDIT_ONLY）、接入审计表 mdm_ingestion_log 与监控指标；新增错误码 812110（消息 schema 非法）/ 812111（来源鉴权失败）/ 812112（同版本冲突）/ 812113（非权威源写入被拒绝）；新增角色 Upstream-System、MDM-Admin |
| 2026-05-26 | CR-003 | Modified | 补充 US-004 历史版本快照需求：新增 MPT 后台查询接口定义（GET /api/mpt/mdm/{entity}/v1/{code}/history），支持 Brand / CarLine / Platform 历史版本按 version 降序查询，响应包含来源字段 |
| 2026-05-27 | CR-004 | Added | 纳入产品树底层 5 类主数据（Model / Variant / Configuration / Option Family / Option Code）：新增术语表（§3）；新增域 6~10 共 12 条 US（US-019 ~ US-030）覆盖 CRUD、引用校验、Option Code 绑定/互斥、按选项码反查配置、事件发布、全量快照、历史追溯、上游接入扩展；修改 NG1 和 OS-1 移除已纳入实体的非目标声明；新增 G9/G10 业务目标；补充业务约束与 Out of Scope（OS-9 ~ OS-12）；新增 Open Questions（Q1 ~ Q7）与 Impact Analysis。VMD 侧对应的下游迁移由 VMD-CR-011 承接 |
| 2026-05-27 | CR-005 | Modified | 细化 Configuration code 自动生成规则：(1) US-022 重写 CRUD AC：Configuration code 改为系统按 `{variantCode}` + 7 位零填充自增序号自动生成、code 不可变、DRAFT 物理删除不回收序号、序号溢出返回 812114；(2) US-020 新增 Variant code 长度上限 57 字符的约束（超限返回 812115），保证 Configuration code 拼接后不超过 64 字符；(3) US-030 新增上游 ingest Configuration 时的 code 决策规则（两层判定：先按 (source_system, source_id) 幂等更新保持原 code；未命中再按 code 是否被占用决定直采上游 code 或本地兜底生成 + 告警）；(4) §5 业务约束补充 Configuration code 生成规则与 Variant code 长度上限；(5) 新增错误码 812114（Configuration 序号溢出）/ 812115（Variant code 长度超限） |
| 2026-05-27 | CR-006 | Added | 引入 Party MDM 子域，纳入 Supplier 作为首个实体：(1) §1 Overview 改写为多子域 MDM 平台定位；(2) §2 新增 G11/G12 目标，NG7 移除 Supplier；(3) §3 Glossary 新增 Party / Supplier 术语；(4) §4 新增"域 11: Party MDM - 供应商管理"含 US-031 ~ US-038，覆盖 CRUD / 历史快照 / 事件发布 / 全量快照 / 上游接入（Kafka + Feign）/ 来源记录与权威源裁决（复用既有机制）/ 接入审计与监控；(5) §5 业务约束补充 Party 与 Product 共享同一服务实例；(6) §6 OS-3 移除 Supplier，新增 OS-13/OS-14；(7) §7 新增 Q8 ~ Q11；(8) §8 补充对采购/SRM/财务系统及 VMD-CR-012 的影响；(9) 错误码 812701（Supplier code 重复）按需新增 |
| 2026-05-28 | CR-007 | Added | 引入 EEAD（Electrical/Electronic Architecture Data）子域，纳入车载节点（Vehicle Node）作为首个实体；同步进行目录重命名 specs/product-mdm → specs/mdm 以消除"目录名 product-mdm 但内容含多子域"的命名漂移：(1) §1 Overview 子域总表追加 EEAD MDM 行 + 新增"EEAD 子域"段含五大块外延规划与 VMD 上移背景；(2) §2 新增 G13（EEAD 子域硬隔离）/ G14（VehicleNode 首版能力定位为"节点身份+节点能力声明"字典）/ G15（基础设施共享 + 命名空间隔离并存）；(3) §3 Glossary 新增 EEAD 术语 8 条（EEAD / Vehicle Node / Node Type / Functional Domain / Device Category / OTA Support Type / HSM Capability / Security Level）；(4) §4 新增"域 12: EEAD - 车载节点字典管理"含 US-039 ~ US-046，覆盖 CRUD（含字段定义引用块）/ nodeCode 全局唯一 / Kafka 事件发布（单一 topic mdm.eead.vehicleNode.event）/ Feign 全量快照（/api/service/mdm/eead/v1/vehicleNode/snapshot）/ 节点能力查询（按 nodeCode 单点）/ 按 OTA 类型批量查询 / 删除前置依赖检查（采用方案 A：反查下游引用 + 硬拒绝，含 MDM-Admin force 旁路）/ source=MDM/MANUAL 治理（MDM 即权威源）；(5) §5 业务约束新增 EEAD 子域共享基础设施声明、三子域硬隔离矩阵（包路径/表前缀/topic/Feign路径/权限点/审批人 6 行 × 4 列）、VehicleNode 业务主键不可变、删除策略说明、本期不开启上游接入路径、Flyway 命名空间；(6) §6 新增 OS-15 ~ OS-18；(7) §7 新增 Q12（错误码段位 807 vs 812 历史漂移建议独立 CR 对齐）/ Q13（VehicleNode 删除策略未来是否切换为软删除）/ Q14（是否开启 VehicleNode 上游接入路径）；(8) §8 新增 EEAD 子域对 VMD（VMD-CR-013 承接）/ OTA / 诊断 / 刷写拓扑 / 配置销售配置器 / 权限治理 6 类下游的影响；(9) 错误码 812301（VEHICLE_NODE_NOT_EXIST）/ 812302（VEHICLE_NODE_CODE_EXIST）/ 812303（VEHICLE_NODE_HAS_DOWNSTREAM_REF）按需新增；(10) 同步动作：git mv specs/product-mdm → specs/mdm，AGENTS.md 中对 specs/product-mdm/* 的引用同步改为 specs/mdm/*，design.md 顶部追加 banner 说明本 design 暂不覆盖 EEAD 子域待后续 CR 补充 |
| 2026-05-28 | CR-008 | Added | 引入 Org（Organization）子域，纳入工厂（Plant）作为首个实体：(1) §1 Overview 子域总表追加 Org MDM 行 + 新增"Org MDM 子域"段含行业语境与术语锁定说明（Plant 为标准词，Factory 仅术语表注明别名）；(2) §2 新增 G16（Org 子域硬隔离）/ G17（本期 ERP 未上线 MDM 暂代 SSOT + 命名空间隔离并存）/ NG9（排除非 Plant 实体）；(3) §3 Glossary 新增 Org 术语 8 条（Org / Plant / Plant Type / Legal Entity / Business Unit / Department / Cost Center / Warehouse）；(4) §4 新增"域 13: Org MDM - 工厂管理"含 US-047 ~ US-055，覆盖 CRUD（含完整字段定义引用块：身份/分类/位置/运营/MDM 同步治理/通用治理 5 层）/ plantCode 全局唯一 / 历史版本快照 / Kafka 事件发布（单一 topic mdm.org.plant.event + eventType PlantCreated/PlantUpdated/PlantDeleted）/ Feign 全量快照（/api/service/mdm/org/v1/plant/snapshot）/ 按 plantCode 单点查询 / 按 plantType 批量查询（供 VMD 按整车总装工厂圈选）/ 删除前置依赖检查（沿用 VehicleNode 方案 A：反查下游引用 + 硬拒绝 + force 旁路）/ 数据来源记录（复用 US-015 source_system/source_id/source_version/ingestion_channel/ingestion_time 机制）；(5) §5 业务约束新增 Org 子域共享基础设施声明、四子域硬隔离矩阵（8 行 × 2 列）、Plant 业务主键不可变、删除策略说明、本期不开启上游接入路径、ERP 未上线约束、位置属性内嵌、引用字段暂不做外键、Flyway 命名空间、错误码段位 8125XX；(6) §6 新增 OS-19 ~ OS-24；(7) §7 新增 Q15（code 命名规范校验）/ Q16（引用字段未来外键化）/ Q17（Plant 失效是否反查引用）/ Q18（ERP 上线权威源切换灰度）/ Q19（Manufacturer 与 Plant 是否拆分）；(8) §8 新增 Org 子域对 VMD（VMD-CR-014 承接，含 Manufacturer 降级为投影副本 + manufacturerCode 改名 plantCode）/ VSO / 数仓 / 未来 MES+WMS / 权限治理 5 类下游的影响；(9) 错误码 812501（PLANT_NOT_EXIST）/ 812502（PLANT_CODE_EXIST）/ 812503（PLANT_HAS_DOWNSTREAM_REF）/ 812504（PLANT_EFFECTIVE_PERIOD_INVALID）/ 812510~812513（预留给未来上游接入）按需新增 |
| 2026-05-29 | CR-009 | Added | 引入 Material（物料）子域，纳入 Part（零件）与 MaterialCategory（物料品类）作为首批实体，VMD 零件数据降级为本地投影：(1) §1 Overview 子域总表追加 Material MDM 行 + 新增"Material MDM 子域"段含行业语境与术语锁定说明；(2) §2 新增 G18（Material 子域 SSOT）/ G19（命名空间隔离并存）/ G20（VMD 零件降级为本地投影）；(3) §3 Glossary 新增 Material 术语 9 条（Material / Part / MaterialCategory / Part Type / Lifecycle Stage / Substitute Part / BOM / Drawing / Local Projection）；(4) §4 新增"域 14: Material MDM - 物料品类管理"含 US-056 ~ US-058（MaterialCategory CRUD / 删除保护 / 树形层级维护）与"域 15: Material MDM - 零件管理"含 US-059 ~ US-070（Part CRUD / categoryCode 引用校验 / vehicleNodeCode 引用校验 / supplierCode 引用校验 / 历史版本快照 / 事件发布 / 全量快照与批量查询 / 数据来源记录 / 生命周期状态机 / 替代件设置 / code 全局唯一 / 删除前置依赖检查）；(5) §5 业务约束新增 Material 子域共享基础设施声明、五子域硬隔离矩阵（8 行 × 2 列）、Part 业务主键不可变、引用完整性、生命周期状态机、删除策略、MaterialCategory 防环与删除保护、本期不开启上游接入路径、VMD 零件降级；(6) §6 新增 OS-25 ~ OS-30；(7) §7 新增 Q20（partType 枚举细化）/ Q21（vehicleNodeCode 引用状态约束）/ Q22（树形层级深度限制）/ Q23（一对多替代关系）/ Q24（fotaUpgradeable 与 otaSupportType 协同）/ Q25（错误码段位确认）；(8) §8 新增 Material 子域对 VMD（VMD-CR-015 承接）/ 采购系统+SRM / OTA 服务 / BOM+制造域 / 权限治理 5 类下游的影响；(9) 错误码 812901（MATERIAL_CATEGORY_NOT_EXIST）/ 812902（MATERIAL_CATEGORY_CODE_EXIST）/ 812903（MATERIAL_CATEGORY_HAS_CHILDREN）/ 812904（EFFECTIVE_PERIOD_INVALID）/ 812905（MATERIAL_CATEGORY_PARENT_NOT_EXIST）/ 812906（MATERIAL_CATEGORY_LOOP_DETECTED）/ 812910（PART_CODE_EXIST）/ 812911（PART_CATEGORY_INVALID）/ 812912（PART_VEHICLE_NODE_INVALID）/ 812913（PART_SUPPLIER_INVALID）/ 812914（PART_SUBSTITUTE_INVALID）/ 812915（PART_LIFECYCLE_INVALID_TRANSITION）/ 812916（PART_HAS_DOWNSTREAM_REF）按需新增 |
| 2026-05-29 | CR-010 | Modified | 决议 Q1：Product MDM 上层失效时采用方案 B（阻止失效 + 依赖检查）。(1) §7 Q1 状态更新为"已决议"；(2) 为 Brand / CarLine / Platform / Model / Variant / OptionFamily 6 个实体的 deactivate 流程新增子级 ACTIVE 状态依赖检查——存在 ACTIVE 子级则拒绝失效；(3) 新增错误码 812116（BrandHasActiveChildren）/ 812117（CarLineHasActiveChildren）/ 812118（PlatformHasActiveChildren）/ 812119（ModelHasActiveChildren）/ 812121（VariantHasActiveChildren）/ 812122（OptionFamilyHasActiveChildren）；(4) 实现层：Repository 接口新增 existsByXxxCodeAndStatusActive 方法，ProductDomainService 各 deactivate 方法新增依赖检查逻辑 |
| 2026-05-29 | CR-011 | Modified | 决议 Q2：Configuration 物理删除采用方案 A（仅 DRAFT 可物理删除）。(1) §7 Q2 状态更新为"已决议"；(2) 策略说明：ACTIVE / INACTIVE 状态仅支持失效（软删除），物理删除仅限 DRAFT 状态；(3) 理由：MDM 内无法感知下游车辆实例（VIN）对 Configuration 的引用，为避免悬空引用，采用保守策略 |
| 2026-05-29 | CR-012 | Modified | 决议 Q3：下层引用上层时采用方案 A（新数据要求上层 ACTIVE，历史数据保留原引用）。(1) §7 Q3 状态更新为"已决议"；(2) 规则说明：创建新记录时必须校验上层为 ACTIVE 状态；更新记录时若引用的上层未变更则保留历史引用不重新校验，若引用的上层发生变更则校验新上层为 ACTIVE 状态；(3) 理由：新业务数据不应再关联已失效的业务实体，但历史数据的引用关系应保持稳定 |
| 2026-05-29 | CR-013 | Modified | 决议 Q4：Option Code 采用方案 A（全局唯一）。(1) §7 Q4 状态更新为"已决议"；(2) 策略说明：Option Code 在整个 mdm_option_code 表中全局唯一，不允许不同 Option Family 下复用同一 code；(3) 理由：行业标准对齐（SAP/GM/VW 均全局唯一）、新势力车企实践、下游系统集成简单；(4) 命名建议：code 带前缀区分语义（如 CLR_RED、ROOF_PANORAMIC） |
| 2026-05-29 | CR-014 | Modified | 决议 Q5：Variant/Configuration 绑定 Option Code 采用方案 A（全局强制，无例外）。(1) §7 Q5 状态更新为"已决议"；(2) 策略说明：Variant 和 Configuration 均遵守同一 Option Family 互斥约束，不允许同一 Option Family 绑定多个 Option Code；(3) 理由：Variant 标识版本特征、Configuration 是具体可生产规格，同一维度应只有一个取值；(4) "多选"场景实现方式：Variant 不绑定该 Option Family + 多个 Configuration 各绑定一个 Option Code |
| 2026-05-29 | CR-015 | Modified | 决议 Q6：Configuration 绑定 Option Code 采用方案 A（不要求全覆盖）。(1) §7 Q6 状态更新为"已决议"；(2) 策略说明：Configuration 可以只绑定部分 Option Family，不要求覆盖所有 Option Family；(3) 理由：渐进式配置管理、"必选"定义因车型而异、下游系统可自行判断必选项 |
| 2026-05-29 | CR-016 | Modified | 决议 Q8：Supplier Golden Record 字段采用"首期全量纳入，后续按反馈裁剪"策略。(1) §7 Q8 状态更新为"已决议"；(2) 策略说明：首期全量纳入 US-031 定义的字段集（含 code/name/supplier_type/country/contact_name/bank_name 等），后续根据实际使用反馈裁剪业务系统私有字段；(3) 理由：新势力车企供应商数据量不大、先跑通流程、避免字段遗漏 |
| 2026-05-29 | CR-017 | Modified | 决议 Q9：Supplier 数据建模采用方案 A（一条记录 + supplier_type 多选）。(1) §7 Q9 状态更新为"已决议"；(2) 策略说明：同一公司只维护一条 Supplier 记录，supplier_type 改为支持多选（VARCHAR(256) 逗号分隔或 JSON 数组）；(3) 理由：避免数据冗余、符合主数据单一 Golden Record 理念 |
| 2026-05-29 | CR-018 | Modified | 决议 Q10：Supplier 状态机采用方案 A（首期三态）。(1) §7 Q10 状态更新为"已决议"；(2) 策略说明：首期仅 DRAFT / ACTIVE / INACTIVE 三态，与 Product MDM 各实体状态机保持一致；(3) 理由：避免状态机过度设计、"黑名单"语义可通过 INACTIVE + description 标注实现、后续根据实际业务需求再扩展 |
| 2026-05-29 | CR-019 | Modified | 决议 Q11：Supplier 失效采用方案 A（不校验下层引用，仅发布事件）。(1) §7 Q11 状态更新为"已决议"；(2) 策略说明：Supplier 失效时不校验下层引用，仅发布 mdm.party.supplier.deactivated 事件，由下游系统自行决定是否拒绝引用；(3) 理由：MDM 无法感知外部系统引用、与 US-031 AC 一致、下游系统应自行处理失效事件 |
| 2026-05-29 | CR-020 | Modified | 决议 Q12：错误码段位采用方案 B（统一迁移至 812xxx 段）。(1) §7 Q12 状态更新为"已决议"；(2) 各子域段位规划：Product=8121XX、EEAD=8123XX、Org=8125XX、Party=8127XX、Material=8129XX；(3) 迁移已完成：Product/Party 原 807xxx 已迁移至 8121XX/8127XX，EEAD 原 812xxx 已迁移至 8123XX，Org 原 813xxx 已迁移至 8125XX，Material 原 814xxx 已迁移至 8129XX；(4) 理由：统一规范、便于管理和扩展 |
| 2026-05-31 | CR-021 | Added | 为 Option Family 实体新增 `category` 字段（商品分类枚举），将商品分类维度纳入 Golden Record 治理：(1) §1 Overview Product MDM 子域段落追加 CR-010 范围说明；(2) §2 新增 G21 目标；(3) §3 Glossary 新增 Option Family Category 术语；(4) §4 域 9 新增 US-071（Option Family 商品分类管理，含完整字段定义引用块与 10 条 EARS AC）；(5) 更新 US-025 追加 category 必填与枚举校验 AC、列表过滤 AC；(6) 更新 US-027 事件 payload 包含 category；(7) 更新 US-028 Feign 响应包含 category；(8) 更新 US-029 history 快照保留 category；(9) 更新 US-030 上游接入校验 category 必填与枚举合法性；(10) §5 业务约束新增 MDM 字段归属判断 checklist（四问治理准则，后续 CR 复用）；(11) §6 新增 OS-31（不引入 selection_type）/ OS-32（不引入 is_customer_visible / display_order / icon_url）；(12) §7 新增 Q26（存量数据 category 回填策略）；(13) 新增错误码 812123（OPTION_FAMILY_CATEGORY_INVALID） |
| 2026-06-03 | CR-022 | Added | 新增按 Variant 和 Option Code 组合反查 Configuration Code 接口需求（US-072），支撑 VSO 创建心愿单时根据版本和选项码组合定位对应的配置代码：(1) §4 域 8 新增 US-072（按 Variant 和 Option Code 组合反查 Configuration Code，含 5 条 EARS AC）；(2) 该接口与现有 US-024（按 Option Code 组合反查 Configuration）的区别在于增加 variantCode 参数约束，返回单个 Configuration Code 而非列表 |
