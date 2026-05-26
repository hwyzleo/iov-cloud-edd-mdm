# Product MDM 子域 - Design

## 1. Architecture Overview

### 系统上下文图

```mermaid
graph TB
    subgraph "上游系统"
        MDM-User[MDM-User<br/>后台运营/工程师]
    end
    
    subgraph "edd-mdm 服务"
        API[API Layer<br/>Controller]
        APP[Application Layer<br/>Service]
        DOMAIN[Domain Layer<br/>Entity/Repository]
        INFRA[Infrastructure Layer<br/>Mapper/Kafka/Outbox]
        RELAY[Outbox Relay<br/>后台任务]
    end
    
    subgraph "下游消费方"
        VMD[VMD<br/>车辆主数据]
        ORDER[订单服务]
        CONFIG[销售配置器]
        BI[BI 系统]
    end
    
    subgraph "基础设施"
        DB[(MySQL)]
        KAFKA[Kafka]
        NACOS[Nacos]
    end
    
    MDM-User -->|HTTP| API
    API --> APP
    APP --> DOMAIN
    DOMAIN --> INFRA
    INFRA --> DB
    INFRA -->|写入 Outbox| DB
    RELAY -->|扫描 Outbox| DB
    RELAY -->|投递事件| KAFKA
    
    VMD -->|Feign| API
    ORDER -->|Feign| API
    CONFIG -->|Feign| API
    BI -->|Feign| API
    
    VMD -->|订阅| KAFKA
    ORDER -->|订阅| KAFKA
    CONFIG -->|订阅| KAFKA
    BI -->|订阅| KAFKA
```

### 模块依赖

#### Parent POM

| 模块 | 继承 | 说明 |
|------|------|------|
| edd-mdm-api | `net.hwyz.iov.cloud.parent:api:0.0.1-SNAPSHOT` | API 模块 |
| edd-mdm-service | `net.hwyz.iov.cloud.parent:service:0.0.1-SNAPSHOT` | Service 模块 |

#### Framework Starter 依赖

| Starter | GroupId | ArtifactId | 职责 |
|---------|---------|-----------|------|
| 常用 | `net.hwyz.iov.cloud.framework` | `framework-common` | 常用对象、常量、枚举、工具类 |
| MySQL | `net.hwyz.iov.cloud.framework` | `framework-mysql-starter` | 数据源配置、MyBatis-Plus 集成、分页插件 |
| Kafka | `net.hwyz.iov.cloud.framework` | `framework-kafka-starter` | Kafka 生产者/消费者配置 |
| Web | `net.hwyz.iov.cloud.framework` | `framework-web-starter` | WEB 服务相关（Service 模块默认依赖） |

**使用约定**：
- Service 模块按需引入所需的 Framework Starter，不直接依赖底层中间件原生 starter
- Service 模块的 Parent 已默认依赖 framework-web-starter，且 framework-web-starter 已默认依赖 framework-common
- Framework Starter 已包含对应中间件的 Spring Boot Starter 传递依赖，业务模块无需重复声明
- 版本由 Parent POM 统一管理，业务模块不指定 Framework Starter 版本号

#### 包结构

**API 模块** (`net.hwyz.iov.cloud.edd.mdm.api`)

```
net.hwyz.iov.cloud.edd.mdm.api
├── service                          // 服务API接口，命名规则：MdmXxxService
│   ├── BrandService.java
│   ├── SeriesService.java
│   └── PlatformService.java
├── fallback                         // 服务API接口fallback类，命名规则：MdmXxxServiceFallbackFactory
│   ├── BrandServiceFallbackFactory.java
│   ├── SeriesServiceFallbackFactory.java
│   └── PlatformServiceFallbackFactory.java
└── vo
    ├── request                      // 入参 VO，命名规则：XxxRequest
    │   ├── BrandCreateRequest.java
    │   ├── BrandUpdateRequest.java
    │   └── ...
    └── response                     // 出参 VO，命名规则：XxxResponse
        ├── BrandResponse.java
        ├── BrandPageResponse.java
        └── ...
```

**Service 模块** (`net.hwyz.iov.cloud.edd.mdm.service`)

```
net.hwyz.iov.cloud.edd.mdm.service
├── adapter                          【接入层 / Interface Adapter】
│   ├── web
│   │   ├── controller
│   │   │   ├── service              // 服务端，命名规则：ServiceXxxController
│   │   │   │   ├── ServiceBrandController.java
│   │   │   │   ├── ServiceSeriesController.java
│   │   │   │   └── ServicePlatformController.java
│   │   │   └── mpt                  // 管理后台，命名规则：MptXxxController
│   │   │       ├── MptBrandController.java
│   │   │       ├── MptSeriesController.java
│   │   │       └── MptPlatformController.java
│   │   ├── vo
│   │   │   ├── request              // 入参 VO，命名规则：XxxRequest
│   │   │   └── response             // 出参 VO，命名规则：XxxResponse
│   │   └── assembler                // VO ⇄ DTO 转换器
│   │       ├── BrandAssembler.java
│   │       ├── SeriesAssembler.java
│   │       └── PlatformAssembler.java
│   ├── mq
│   │   └── consumer                 // 消息消费入口(驱动 Application)
│   └── task
│       └── scheduler                // 定时任务入口(驱动 Application)
│           └── OutboxRelayScheduler.java
│
├── application                      【应用层 / Use Case Orchestration】
│   ├── service                      // 用例编排、事务边界，命名规则：XxxAppService
│   │   ├── BrandAppService.java
│   │   ├── SeriesAppService.java
│   │   └── PlatformAppService.java
│   ├── dto
│   │   ├── cmd                      // 写入类入参，命名规则：XxxCmd
│   │   │   ├── BrandCreateCmd.java
│   │   │   ├── BrandUpdateCmd.java
│   │   │   └── ...
│   │   ├── query                    // 查询类入参，命名规则：XxxQuery
│   │   │   ├── BrandQuery.java
│   │   │   ├── SeriesQuery.java
│   │   │   └── ...
│   │   └── result                   // 出参，命名规则：XxxResult / XxxDto
│   │       ├── BrandDto.java
│   │       ├── SeriesDto.java
│   │       └── ...
│   ├── assembler                    // DTO ⇄ Domain Model 转换器，命名规则：XxxAssembler
│   │   ├── BrandDomainAssembler.java
│   │   ├── SeriesDomainAssembler.java
│   │   └── PlatformDomainAssembler.java
│   └── port                         // ★ Application 定义的出站端口
│       ├── gateway                  // 外部系统 Port(跨上下文、三方 API)
│       │   └── KafkaEventGateway.java
│       └── service                  // 技术能力 Port(日志、幂等、锁、ID)
│           └── OutboxService.java
│
├── domain                           【领域层 / Domain Core】
│   ├── model
│   │   ├── aggregate                // 聚合根
│   │   │   ├── Brand.java
│   │   │   ├── Series.java
│   │   │   └── Platform.java
│   │   ├── entity                   // 实体(聚合内)
│   │   ├── valueobject              // 值对象
│   │   │   ├── BrandStatus.java
│   │   │   ├── SeriesType.java
│   │   │   ├── PlatformType.java
│   │   │   └── ...
│   │   └── event                    // 领域事件
│   │       ├── BrandCreatedEvent.java
│   │       ├── BrandUpdatedEvent.java
│   │       ├── BrandDeactivatedEvent.java
│   │       └── ...
│   ├── service                      // 领域服务(跨聚合业务逻辑)
│   │   └── ProductDomainService.java
│   ├── repository                   // ★ 聚合持久化接口(仅接口)，命名规则：XxxRepository
│   │   ├── BrandRepository.java
│   │   ├── SeriesRepository.java
│   │   ├── PlatformRepository.java
│   │   └── OutboxRepository.java
│   ├── gateway                      // ★ (可选)领域级外部依赖接口
│   ├── policy                       // 业务策略 / 规则引擎
│   ├── factory                      // 聚合工厂，命名规则：XxxFactory
│   └── exception                    // 领域异常
│       ├── BrandNotFoundException.java
│       ├── DuplicateCodeException.java
│       └── ...
│
├── infrastructure                   【基础设施层 / Implementation】
│   ├── persistence
│   │   ├── po                       // 数据库对象,不得外泄，命名规则：XxxPo
│   │   │   ├── BrandPo.java
│   │   │   ├── SeriesPo.java
│   │   │   ├── PlatformPo.java
│   │   │   ├── BrandHistoryPo.java
│   │   │   ├── SeriesHistoryPo.java
│   │   │   ├── PlatformHistoryPo.java
│   │   │   └── OutboxPo.java
│   │   ├── mapper                   // MyBatis / JPA Mapper，命名规则：XxxMapper
│   │   │   ├── BrandMapper.java
│   │   │   ├── SeriesMapper.java
│   │   │   ├── PlatformMapper.java
│   │   │   ├── BrandHistoryMapper.java
│   │   │   ├── SeriesHistoryMapper.java
│   │   │   ├── PlatformHistoryMapper.java
│   │   │   └── OutboxMapper.java
│   │   ├── repository               // Domain Repository 接口实现，命名规则：XxxRepositoryImpl
│   │   │   ├── BrandRepositoryImpl.java
│   │   │   ├── SeriesRepositoryImpl.java
│   │   │   ├── PlatformRepositoryImpl.java
│   │   │   └── OutboxRepositoryImpl.java
│   │   └── converter                // DO ⇄ Domain Model 转换器，命名规则：XxxConverter
│   │       ├── BrandConverter.java
│   │       ├── SeriesConverter.java
│   │       └── PlatformConverter.java
│   ├── cache
│   │   └── redis
│   ├── gateway                      // ★ Application/Domain 定义的 Gateway 实现
│   │   └── mq                       // 消息生产者(对外发消息)
│   │       └── KafkaEventGatewayImpl.java
│   ├── service                      // ★ Application 定义的 Service Port 实现
│   │   └── OutboxServiceImpl.java
│   ├── config                       // Spring 配置、数据源、Bean 装配
│   └── common                       // Infra 内部工具
│
└── common / shared                  【跨层通用】
    ├── constant
    │   └── MdmConstants.java
    ├── enums                        // 与协议/存储无关的通用枚举
    │   ├── EntityStatus.java
    │   └── EventType.java
    ├── exception                    // 基础异常类(ServiceException 等)
    │   └── MdmBusinessException.java
    └── util                         // 纯工具类(日期、字符串)
```

### DDD 四层架构

| 层 | 英文 | 职责 | 对应对象 | 主要组件 |
|---|------|------|----------|----------|
| **接入层** | Controller / Adapter | 协议适配、参数校验、鉴权、序列化 | VO | Controller、Assembler |
| **应用层** | Application | 用例编排、事务边界、跨聚合协调，**不含业务规则** | DTO | AppService、DTO、Assembler、Port |
| **领域层** | Domain | 业务规则、实体、值对象、领域服务、领域事件 | Domain Model | Aggregate、Entity、VO、Event、Repository 接口 |
| **基础设施层** | Infrastructure | 持久化、消息、缓存、外部 RPC | DO | Mapper、Repository 实现、Converter、Gateway 实现 |

**依赖方向**：Controller → Application → Domain ← Infrastructure

Domain 是核心，不得依赖任何其他层。Infrastructure 通过依赖倒置（DIP）实现 Domain 定义的接口（Repository、Gateway 等）。

#### 对象约束

| 对象 | 定义位置 | 命名规范 | 说明 |
|------|----------|----------|------|
| **PO** | infrastructure.persistence.po | XxxPo | 与数据库表结构一一对应，包含审计字段 |
| **Domain Model** | domain.model | 业务名（如 Brand） | 纯 POJO，包含业务逻辑方法，不含框架注解 |
| **DTO** | application.dto | XxxDto / XxxCmd / XxxQuery | 应用层输入/输出契约，纯数据载体 |
| **VO** | adapter.web.vo 或 api.vo | XxxRequest / XxxResponse | 对外暴露的数据对象，字段对前端友好 |

#### 转换规则

| 转换 | 归属层 | 推荐组件 |
|------|--------|----------|
| VO ⇄ DTO | Controller 层 | Assembler |
| DTO ⇄ Domain Model | Application 层 | Assembler |
| Domain Model ⇄ DO | Infrastructure 层 | Converter |

**规范**：
- 禁止跨层直接转换（例如 VO → Domain Model、DO → VO）
- 转换器为无状态 @Component 或静态工具，禁止在转换器中写业务逻辑
- 所有层间对象转换必须使用 MapStruct，禁止手动写 setXxx() 转换代码

## 2. Tech Stack & Decisions

### 平台统一 Parent 与 Framework

本项目继承 OpenIOV 平台统一的 Parent POM 和 Framework Starter，不自行管理基础中间件版本与配置。

| 决策 | 选择 | 说明 |
|------|------|------|
| Parent POM (API) | `net.hwyz.iov.cloud.parent:api:0.0.1-SNAPSHOT` | API 模块继承 |
| Parent POM (Service) | `net.hwyz.iov.cloud.parent:service:0.0.1-SNAPSHOT` | Service 模块继承 |
| Framework Common | `net.hwyz.iov.cloud.framework:framework-common` | 常用对象、常量、枚举、工具类 |
| Framework MySQL | `net.hwyz.iov.cloud.framework:framework-mysql-starter` | 数据源配置、MyBatis-Plus 集成、分页插件 |
| Framework Kafka | `net.hwyz.iov.cloud.framework:framework-kafka-starter` | Kafka 生产者/消费者配置 |
| Framework Web | `net.hwyz.iov.cloud.framework:framework-web-starter` | WEB 服务相关（Service 模块默认依赖） |

**使用约定**：
- Service 模块按需引入所需的 Framework Starter，不直接依赖底层中间件原生 starter
- Service 模块的 Parent 已默认依赖 framework-web-starter，且 framework-web-starter 已默认依赖 framework-common
- Framework Starter 已包含对应中间件的 Spring Boot Starter 传递依赖，业务模块无需重复声明
- 版本由 Parent POM 统一管理，业务模块不指定 Framework Starter 版本号
- 设计 MySQL 数据库的 Service，引入 framework-mysql-starter 后，相关 Dao 或 Mapper 需要继承 `net.hwyz.iov.cloud.framework.mysql.dao.BaseDao`

### 技术选型

| Decision | Choice | Alternatives | Rationale |
|----------|--------|--------------|-----------|
| JDK 版本 | JDK 17 | JDK 8, JDK 11 | 与 VMD 对齐，LTS 版本 |
| Web 框架 | Spring Boot 2.7.x + Spring Cloud | Quarkus, Micronaut | 与 VMD 对齐，生态成熟 |
| 注册中心 | Nacos | Eureka, Consul | 与 VMD 对齐，支持配置管理 |
| ORM | MyBatis-Plus | JPA, MyBatis | 与 VMD 对齐，简化 CRUD |
| 数据库迁移 | Flyway | Liquibase | 与 VMD 对齐，版本化管理 |
| 消息队列 | Apache Kafka | RabbitMQ, RocketMQ | 与 VMD 对齐，高吞吐 |
| 事件分发模式 | Outbox Pattern + Relay | 直接 Kafka, CDC | 事务一致性、不丢消息 |
| 历史版本存储 | 独立 history 表 | 同表 version 字段, Event Sourcing | 查询简单、主表性能好 |
| Brand-Series 关联 | 逻辑引用 (brandCode) | 物理外键 | 解耦、灵活 |
| API 风格 | RESTful | gRPC, GraphQL | 与 VMD 对齐，通用性好 |
| 分页支持 | PageHelper + 分页 VO | 游标分页 | 与 VMD 对齐，简单通用 |

### 新增技术决策

| 决策 | 选择 | 说明 |
|------|------|------|
| Outbox 实现 | 本地表 + 后台 Relay 任务 | 不依赖 Debezium，运维简单 |
| Kafka Topic 命名 | `mdm.product.<entity>.<eventType>` | 语义清晰，便于订阅 |
| Feign 契约策略 | edd-mdm-api 模块定义接口 + VO | 契约 SSOT，下游依赖 api jar |
| FallbackFactory | 返回空对象 + 日志告警 | 避免 NPE，便于排查 |
| 错误码段位 | 807XXX | 与企业数字底座领域其他服务对齐 |

## 3. Data Model

### 3.1 主表

#### mdm_brand（品牌表）

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | Y | 主键，自增 |
| code | VARCHAR(64) | Y | 业务主键，跨系统稳定 |
| name | VARCHAR(128) | Y | 官方名称（如 BMW） |
| name_local | VARCHAR(128) | N | 本地化名称（如 宝马） |
| description | VARCHAR(512) | N | 品牌描述 |
| logo | VARCHAR(256) | N | Logo URL |
| country | VARCHAR(64) | N | 国家 |
| founded_year | INT | N | 创立年份 |
| version | INT | Y | 业务版本号，每次变更 +1 |
| effective_from | DATETIME | N | 生效开始时间 |
| effective_to | DATETIME | N | 生效结束时间 |
| status | VARCHAR(16) | Y | ACTIVE / INACTIVE / DEPRECATED / DRAFT |
| create_by | VARCHAR(64) | Y | 创建人 |
| create_time | DATETIME | Y | 创建时间 |
| modify_by | VARCHAR(64) | Y | 修改人 |
| modify_time | DATETIME | Y | 修改时间 |
| row_version | INT | Y | 乐观锁版本号，默认 0 |
| row_valid | TINYINT | Y | 行有效标记，1=有效，0=无效 |

**唯一约束**：UK(code)

#### mdm_series（车系表）

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | Y | 主键，自增 |
| code | VARCHAR(64) | Y | 业务主键 |
| name | VARCHAR(128) | Y | 官方名称（如 Model 3） |
| name_local | VARCHAR(128) | N | 本地化名称（如 汉） |
| brand_code | VARCHAR(64) | Y | 逻辑引用 Brand.code |
| series_type | VARCHAR(16) | N | 轿车/SUV/MPV/皮卡/商用 |
| lifecycle_status | VARCHAR(16) | N | 在研/在售/停售 |
| target_market | VARCHAR(16) | N | 国内/海外/全球 |
| version | INT | Y | 业务版本号，每次变更 +1 |
| effective_from | DATETIME | N | 生效开始时间 |
| effective_to | DATETIME | N | 生效结束时间 |
| status | VARCHAR(16) | Y | ACTIVE / INACTIVE / DEPRECATED / DRAFT |
| create_by | VARCHAR(64) | Y | 创建人 |
| create_time | DATETIME | Y | 创建时间 |
| modify_by | VARCHAR(64) | Y | 修改人 |
| modify_time | DATETIME | Y | 修改时间 |
| row_version | INT | Y | 乐观锁版本号，默认 0 |
| row_valid | TINYINT | Y | 行有效标记，1=有效，0=无效 |

**唯一约束**：UK(code)  
**业务约束**：brand_code 必须指向已存在且 status=ACTIVE 的 Brand

#### mdm_platform（平台表）

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | Y | 主键，自增 |
| code | VARCHAR(64) | Y | 业务主键 |
| name | VARCHAR(128) | Y | 官方名称（如 MEB） |
| name_local | VARCHAR(128) | N | 本地化名称（如有） |
| platform_type | VARCHAR(16) | N | 油车/纯电/插混/增程 |
| architecture | VARCHAR(64) | N | EE 架构代号 |
| version | INT | Y | 业务版本号，每次变更 +1 |
| effective_from | DATETIME | N | 生效开始时间 |
| effective_to | DATETIME | N | 生效结束时间 |
| status | VARCHAR(16) | Y | ACTIVE / INACTIVE / DEPRECATED / DRAFT |
| create_by | VARCHAR(64) | Y | 创建人 |
| create_time | DATETIME | Y | 创建时间 |
| modify_by | VARCHAR(64) | Y | 修改人 |
| modify_time | DATETIME | Y | 修改时间 |
| row_version | INT | Y | 乐观锁版本号，默认 0 |
| row_valid | TINYINT | Y | 行有效标记，1=有效，0=无效 |

**唯一约束**：UK(code)

### 3.2 历史快照表

历史快照表结构与主表一致（包含所有业务字段和审计字段），额外增加以下字段：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| snapshot_id | BIGINT | Y | 主键，自增 |
| entity_id | BIGINT | Y | 关联主表 id |
| operation_type | VARCHAR(16) | Y | CREATE / UPDATE / DEACTIVATE / DELETE |
| snapshot_time | DATETIME | Y | 快照时间 |
| operator | VARCHAR(64) | Y | 操作人 |

**表名**：
- mdm_brand_history
- mdm_series_history
- mdm_platform_history

### 3.3 事务性发件箱表

#### mdm_outbox

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | Y | 主键，自增 |
| aggregate_type | VARCHAR(32) | Y | 聚合类型（BRAND / SERIES / PLATFORM） |
| aggregate_id | VARCHAR(64) | Y | 聚合根 ID（code） |
| event_type | VARCHAR(64) | Y | 事件类型 |
| payload | TEXT | Y | JSON 格式事件体 |
| occurred_at | DATETIME | Y | 事件发生时间 |
| sent | BOOLEAN | Y | 是否已发送，默认 false |
| sent_at | DATETIME | N | 发送时间 |
| retry_count | INT | Y | 重试次数，默认 0 |
| create_by | VARCHAR(64) | Y | 创建人 |
| create_time | DATETIME | Y | 创建时间 |
| modify_by | VARCHAR(64) | Y | 修改人 |
| modify_time | DATETIME | Y | 修改时间 |
| row_version | INT | Y | 乐观锁版本号，默认 0 |
| row_valid | TINYINT | Y | 行有效标记，1=有效，0=无效 |

**索引**：
- IDX_OUTBOX_SENT_OCCURRED (sent, occurred_at)：用于 Relay 扫描
- IDX_OUTBOX_AGGREGATE (aggregate_type, aggregate_id)：用于聚合查询

## 4. Core Flows

### F1 - MDM-User 维护品牌（CRUD）

```mermaid
sequenceDiagram
    participant User as MDM-User
    participant API as Controller
    participant App as Service
    participant Domain as Domain Layer
    participant Infra as Infrastructure
    participant DB as MySQL
    participant Outbox as Outbox Table
    
    User->>API: POST /api/mpt/mdm/brand/v1/create
    API->>App: createBrand(dto)
    App->>Domain: Brand.create(dto)
    Domain->>Domain: 校验 code 唯一性
    Domain->>Domain: 校验 effectiveFrom <= effectiveTo
    Domain->>Infra: repository.save(brand)
    Infra->>DB: INSERT mdm_brand
    Infra->>DB: INSERT mdm_brand_history
    Infra->>Outbox: INSERT mdm_outbox (event_type=brand.created)
    Infra-->>App: 返回 Brand
    App-->>API: 返回 BrandVO
    API-->>User: 200 OK
```

### F2 - Outbox 写入 + 后台 Relay 任务推 Kafka

```mermaid
sequenceDiagram
    participant Service as Service
    participant Outbox as Outbox Table
    participant Relay as Relay Task
    participant Kafka as Kafka
    participant DLQ as Dead Letter Queue
    
    Service->>Outbox: INSERT 事件（与业务同一事务）
    
    loop 定时扫描（每 5 秒）
        Relay->>Outbox: SELECT WHERE sent=false ORDER BY occurred_at
        Outbox-->>Relay: 返回待发送事件列表
        
        loop 逐条发送
            Relay->>Kafka: 发送事件
            alt 发送成功
                Kafka-->>Relay: ACK
                Relay->>Outbox: UPDATE sent=true, sent_at=now()
            else 发送失败
                Kafka-->>Relay: NACK
                Relay->>Relay: retry_count++
                alt 超出重试次数
                    Relay->>DLQ: 投递死信队列
                    Relay->>Relay: 告警通知
                end
            end
        end
    end
```

### F3 - 下游 Bootstrap 拉全量快照流程

```mermaid
sequenceDiagram
    participant Caller as Service-Caller
    participant API as Controller
    participant App as Service
    participant DB as MySQL
    
    Caller->>API: GET /api/service/brand/v1/listAll?page=1&size=100
    API->>App: listAllBrands(page, size, includeInactive)
    App->>DB: SELECT FROM mdm_brand WHERE status='ACTIVE' LIMIT 100
    DB-->>App: 返回 Brand 列表
    App-->>API: 返回 PageResult<BrandVO>
    API-->>Caller: 200 OK
    
    Note over Caller: 循环分页拉取直至完成
```

### F4 - 失效（Deactivate）的事件传播

```mermaid
sequenceDiagram
    participant User as MDM-User
    participant Service as Service
    participant DB as MySQL
    participant Outbox as Outbox Table
    participant Relay as Relay Task
    participant Kafka as Kafka
    participant Downstream as 下游系统
    
    User->>Service: 失效 Brand（status=INACTIVE）
    Service->>DB: UPDATE mdm_brand SET status='INACTIVE', effective_to=now(), version=version+1
    Service->>DB: INSERT mdm_brand_history
    Service->>Outbox: INSERT event (eventType=brand.deactivated)
    
    Relay->>Outbox: 扫描并发送
    Relay->>Kafka: 发送 deactivated 事件
    Kafka->>Downstream: 消费 deactivated 事件
    Downstream->>Downstream: 更新本地副本 status=INACTIVE
```

## 5. API Contracts

### 5.1 MPT 端接口（后台管理）

#### Brand 接口

| Method | Path | 说明 |
|--------|------|------|
| POST | /api/mpt/mdm/brand/v1/create | 创建品牌 |
| PUT | /api/mpt/mdm/brand/v1/{code} | 更新品牌 |
| DELETE | /api/mpt/mdm/brand/v1/{code} | 删除品牌（仅 DRAFT 状态） |
| POST | /api/mpt/mdm/brand/v1/{code}/deactivate | 失效品牌 |
| GET | /api/mpt/mdm/brand/v1/{code} | 查询品牌详情 |
| GET | /api/mpt/mdm/brand/v1/list | 分页查询品牌列表 |
| GET | /api/mpt/mdm/brand/v1/{code}/history | 查询品牌历史版本 |

#### Series 接口

| Method | Path | 说明 |
|--------|------|------|
| POST | /api/mpt/mdm/series/v1/create | 创建车系 |
| PUT | /api/mpt/mdm/series/v1/{code} | 更新车系 |
| DELETE | /api/mpt/mdm/series/v1/{code} | 删除车系（仅 DRAFT 状态） |
| POST | /api/mpt/mdm/series/v1/{code}/deactivate | 失效车系 |
| GET | /api/mpt/mdm/series/v1/{code} | 查询车系详情 |
| GET | /api/mpt/mdm/series/v1/list | 分页查询车系列表（支持 brandCode、status 过滤） |
| GET | /api/mpt/mdm/series/v1/{code}/history | 查询车系历史版本 |

#### Platform 接口

| Method | Path | 说明 |
|--------|------|------|
| POST | /api/mpt/mdm/platform/v1/create | 创建平台 |
| PUT | /api/mpt/mdm/platform/v1/{code} | 更新平台 |
| DELETE | /api/mpt/mdm/platform/v1/{code} | 删除平台（仅 DRAFT 状态） |
| POST | /api/mpt/mdm/platform/v1/{code}/deactivate | 失效平台 |
| GET | /api/mpt/mdm/platform/v1/{code} | 查询平台详情 |
| GET | /api/mpt/mdm/platform/v1/list | 分页查询平台列表 |
| GET | /api/mpt/mdm/platform/v1/{code}/history | 查询平台历史版本 |

### 5.2 Service 端接口（下游消费）

#### Brand 接口

| Method | Path | 说明 |
|--------|------|------|
| GET | /api/service/brand/v1/listAll | 全量快照（支持分页、includeInactive） |
| GET | /api/service/brand/v1/{code} | 按 code 单点查询 |

#### Series 接口

| Method | Path | 说明 |
|--------|------|------|
| GET | /api/service/series/v1/listAll | 全量快照（支持分页、brandCode 过滤） |
| GET | /api/service/series/v1/{code} | 按 code 单点查询 |

#### Platform 接口

| Method | Path | 说明 |
|--------|------|------|
| GET | /api/service/platform/v1/listAll | 全量快照（支持分页） |
| GET | /api/service/platform/v1/{code} | 按 code 单点查询 |

### 5.3 错误码表（段位 807XXX）

| 错误码 | 说明 | HTTP 状态码 |
|--------|------|------------|
| 807001 | 业务主键（code）已存在 | 409 Conflict |
| 807002 | 记录不存在 | 404 Not Found |
| 807003 | 状态不允许删除（非 DRAFT） | 400 Bad Request |
| 807004 | 生效期无效（effectiveFrom > effectiveTo） | 400 Bad Request |
| 807005 | 引用的 Brand 不存在或状态无效 | 400 Bad Request |
| 807006 | 状态不允许失效（非 ACTIVE） | 400 Bad Request |
| 807099 | 系统内部错误 | 500 Internal Server Error |

## 6. Coverage Mapping

| US-ID | Design Section | Note |
|-------|----------------|------|
| US-001 | §3.1 (mdm_brand), §4 (F1), §5.1 (Brand API) | Brand CRUD |
| US-002 | §3.1 (mdm_series), §5.1 (Series API) | Series CRUD + 过滤 |
| US-003 | §3.1 (mdm_platform), §5.1 (Platform API) | Platform CRUD |
| US-004 | §3.2 (history 表) | 历史版本快照 |
| US-005 | §4 (F1 中的校验逻辑) | 生效期校验 |
| US-006 | §3.3 (mdm_outbox), §4 (F2, F4) | Brand 事件发布 |
| US-007 | §3.3, §4 (F2, F4) | Series 事件发布 |
| US-008 | §3.3, §4 (F2, F4) | Platform 事件发布 |
| US-009 | §5.2 (Brand Service API) | Brand 全量快照 |
| US-010 | §5.2 (Series Service API) | Series 全量快照 |
| US-011 | §5.2 (Platform Service API) | Platform 全量快照 |
| US-012 | §5.2 (单点查询 API) | 按 code 单点查询 |

## 7. Impact Analysis

### 对 VMD 项目的影响

- VMD 需要新增本地投影副本表（external_ref_id, external_version, source='MDM', last_sync_time）
- VMD 需要实现 Kafka 消费者订阅 `mdm.product.*` 事件
- VMD 需要实现 Feign 客户端调用 edd-mdm Service 端接口
- VMD 需要改造 Brand / Series / Platform 的查询逻辑，优先读本地副本，Fallback 到 MDM

### 对下游系统的影响

- 订单服务、销售配置器、BI 系统需要订阅 Kafka 事件或调用 Feign 接口
- 下游系统需要实现 upsert 逻辑：`IF event.version > local.external_version THEN upsert ELSE ignore`

## 8. Open Questions

| 编号 | 问题 | 答案 | 状态 |
|------|------|------|------|
| OQ-1 | Outbox Relay 任务的扫描频率和批量大小如何配置？ | 扫描频率：5 秒，批量大小：100 条，支持 Nacos 动态配置 | 已确认 |
| OQ-2 | Kafka 消息的 Key 如何选择？ | 使用 entity code（如 BRAND_001），保证同一实体事件在同一 Partition 内有序 | 已确认 |
| OQ-3 | Feign 接口是否需要支持增量拉取（基于 modify_time）？ | 本期不实现，留待后续 CR。首期用 Kafka 事件实现增量同步，Feign 接口聚焦 Bootstrap 和对账 | 已确认 |

## 9. Changelog

| Date | Change ID | Type | Description |
|------|-----------|------|-------------|
| 2026-05-26 | CR-001 | Added | 首版产出：建立 Product MDM 子域（Brand / Series / Platform）设计文档 |
