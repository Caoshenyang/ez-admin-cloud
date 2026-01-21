# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在此代码仓库中工作时提供指导。

## 核心行为准则 (重要：Token 节省模式，强制执行)

为了提高响应速度并减少 Token 消耗，请遵循以下原则：

1. **禁止自动校验**: 严禁主动运行 `mvn compile`、`mvn test` 或任何构建命令。
2. **禁止循环修复**: 若代码报错（尤其是 MapStruct 缺失实现类或依赖未找到），请立即停止并向用户报告，严禁自行通过重复运行 Maven 命令尝试修复。
3. **只管生成，用户校验**: 你的职责是输出高质量 Java 代码。调试、环境纠错与单元测试由用户在本地控制。
4. **包管理与依赖许可 (Strict)**: 严禁在未经过用户明确同意的情况下修改 `pom.xml` 或引入任何新依赖。
5. **禁止私自提交 (Strict)**: 严禁主动执行 `git commit` 操作。只有在用户明确要求时才执行提交。
6. **同步更新进度 (Mandatory)**: 每一项子任务完成后，**必须立即修改并保存 `CLAUDE.md` 文件**。将对应任务标记为 `[x]`，并在末尾标注完成时间。


## 项目概述

这是一个基于 Java 的 Spring Boot 父项目 (ez-admin-cloud)，使用 Maven 作为构建工具。采用 Spring Boot + MyBatis-Plus 技术栈，遵循"分治主义"设计哲学。


## 技术栈规范

### 1. 命名规范 (Strict)

- **严禁使用模糊命名**（如 `BaseReq`, `Handle.java`）
- **必须使用明确的语义化命名**：
  - DTO: `UserQueryDTO`, `RoleCreateDTO`
  - VO: `UserResponseVO`, `MenuTreeVO`
  - Service: `UserService`, `RoleService`
  - Mapper: `UserMapper`, `RoleMapper`
  - Entity: 严格对应数据库表名，使用 Pascal Case

### 2. MyBatis-Plus 使用规范 (核心架构原则)

#### 2.1 分治主义设计哲学

本规范采用"分治主义"策略，在**开发效率**、**代码质量**与**后期可维护性**之间取得最佳平衡：

- **单表与简单逻辑**：拥抱编程式（MyBatis-Plus Wrapper），利用其类型安全和极致效率
- **多表与复杂逻辑**：回归 XML，利用其结构化能力、ResultMap 映射能力和 SQL 优化空间
- **注解 SQL**：**全面禁用**。注解既缺乏编程的可维护性，又缺乏 XML 的结构化美感

#### 2.2 职责分层规范

**实体层 (Entity / VO)**
- **Entity**：严格对应数据库表，仅使用 MyBatis-Plus 注解（`@TableName`, `@TableId`, `@TableField`）
- **VO (View Object)**：用于复杂查询的结果接收，必须在 XML 中定义对应的 `ResultMap`

**Mapper 接口层**
Mapper 是抹平"编程式"与"XML"差异的关键。Service 层不应感知底层实现细节：
- **简单 CRUD**：直接继承 `BaseMapper<T>`
- **单表复杂查询**：在接口中使用 `default` 关键字编写 LambdaWrapper 逻辑
- **多表联查/原生 SQL**：仅声明方法，具体实现在 XML 中

#### 2.3 技术决策准则（什么时候用什么？）

| 场景类型 | 推荐方案 | 实施方式 |
| :--- | :--- | :--- |
| **基础 CRUD** | **MP 内置方法** | `insert`, `updateById`, `deleteById` 等 |
| **单表动态筛选** | **编程式 (Wrapper)** | Mapper 中的 `default` 方法 + `LambdaQueryWrapper` |
| **2-3 表简单联查** | **编程式 / XML** | 逻辑简单可选用 `default` 封装；涉及多字段映射选 XML |
| **复杂关联/报表** | **XML** | 编写自定义 SQL，配置嵌套 `ResultMap` |
| **高性能/极致优化** | **XML** | 需精确控制 SQL 执行计划或使用特定数据库函数 |

#### 2.4 XML 编写规范

**ResultMap 嵌套映射**
```xml
<mapper namespace="com.example.mapper.UserMapper">
    <sql id="Base_Column_List">
        u.id, u.username, u.age, u.dept_id, u.status
    </sql>

    <resultMap id="UserDetailMap" type="com.example.vo.UserVO">
        <id property="id" column="id"/>
        <result property="username" column="username"/>
        <association property="dept" javaType="com.example.entity.Dept">
            <id property="id" column="d_id"/>
            <result property="name" column="d_name"/>
        </association>
        <collection property="roles" ofType="com.example.entity.Role">
            <id property="id" column="r_id"/>
            <result property="roleName" column="r_name"/>
        </collection>
    </resultMap>

    <select id="selectUserDetail" resultMap="UserDetailMap">
        SELECT
            <include refid="Base_Column_List"/>,
            d.id AS d_id, d.name AS d_name,
            r.id AS r_id, r.role_name AS r_name
        FROM sys_user u
        LEFT JOIN sys_dept d ON u.dept_id = d.id
        LEFT JOIN sys_user_role ur ON u.id = ur.user_id
        LEFT JOIN sys_role r ON ur.role_id = r.id
        WHERE u.id = #{id}
    </select>
</mapper>
```

**Mapper 层封装示例**
```java
@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 【规范】单表复杂查询 - 使用 default 封装，对 Service 屏蔽 Wrapper
    default List<User> selectActiveUsers(String keyword) {
        return this.selectList(new LambdaQueryWrapper<User>()
                .eq(User::getStatus, 1)
                .like(StringUtils.hasText(keyword), User::getUsername, keyword));
    }

    // 【规范】多表联查 - XML 实现
    UserVO selectUserDetail(@Param("id") Long id);
}
```

#### 2.5 团队开发红线（强制执行）

- **【禁止】** 在 Service 层拼装超过 3 个条件的 QueryWrapper。此类逻辑必须下沉至 Mapper 层封装为方法
- **【禁止】** 在项目中使用 `@Select`、`@Update` 等注解编写 SQL
- **【强制】** 必须使用 `LambdaQueryWrapper`，严禁在代码中出现硬编码的数据库字段名
- **【强制】** 多表联查 SQL 必须显式定义 ResultMap，严禁使用 Map 或 JSONObject 接收结果
- **【建议】** SQL 关键字保持大写，提高 XML 代码的视觉可读性

### 3. 代码注释规范 (Mandatory)

- **Controller 接口**：必须编写清晰注释，说明接口用途、参数含义、返回值结构
- **Service 复杂逻辑**：必须在关键业务逻辑处编写注释，解释"为什么这么做"
- **Mapper 方法**：多表联查或复杂查询必须注释查询目的和关联关系
- **注释风格**：优先使用 JavaDoc 注释格式

### 4. 对象转换规范

- **强制使用 MapStruct**：实体与 DTO/VO 之间的转换必须使用 MapStruct
- **转换器位置**：若转换逻辑复杂，请在 `model/mapstruct/` 下定义转换器接口
- **禁止手动 set/get**：严禁在代码中手动编写大量的 set/get 方法进行对象转换

### 5. 分层调用规范

```
Controller → Service → Mapper → Entity/VO
    ↓         ↓         ↓
   DTO    Entity    XML/Wrapper
```

- **Controller 层**：负责接收请求参数（DTO）、调用 Service、返回响应（VO）
- **Service 层**：负责业务逻辑处理，不直接操作 QueryWrapper
- **Mapper 层**：负责数据访问，封装查询逻辑（Wrapper 或 XML）


## 任务清单 (Todo List)

---
*注：每次执行完代码修改后，请确认已勾选上述清单，并告知用户下一项任务是什么。*
