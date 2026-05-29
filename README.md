# Jenkins CI/CD 演示项目

一个功能完整的 Spring Boot REST API 应用，用于学习 Jenkins 流水线和 CI/CD 实践。

## 技术栈

- Java 17
- Spring Boot 3.2
- Spring Data JPA + H2 (内存数据库)
- Maven + JaCoCo (代码覆盖率)
- Docker / Docker Compose
- Jenkins Pipeline

## 项目结构

```
jenkins项目/
├── pom.xml
├── Jenkinsfile                 # Jenkins Pipeline (7阶段)
├── Dockerfile                  # 多阶段构建
├── docker-compose.yml
├── src/main/java/com/example/
│   ├── JenkinsDemoApplication.java
│   ├── config/DataInitializer.java    # 初始化种子数据
│   ├── model/User.java                # 用户实体
│   ├── repository/UserRepository.java # JPA 仓库
│   ├── service/UserService.java       # 业务逻辑
│   └── controller/
│       ├── HelloController.java       # GET /hello
│       └── UserController.java        # REST CRUD /api/users
├── src/main/resources/
│   ├── application.yml
│   └── application-test.yml
├── src/test/java/com/example/
│   ├── JenkinsDemoApplicationTests.java
│   ├── service/UserServiceTest.java       # 单元测试 (Mock)
│   └── controller/UserControllerTest.java # 集成测试 (MockMvc)
└── README.md
```

## API 接口

| 方法   | 路径                  | 说明         |
|--------|-----------------------|-------------|
| GET    | /hello                | 欢迎消息     |
| GET    | /api/users            | 获取所有用户 |
| GET    | /api/users/{id}       | 按ID查询     |
| GET    | /api/users/search?name=xxx | 按名字搜索 |
| POST   | /api/users            | 创建用户     |
| PUT    | /api/users/{id}       | 更新用户     |
| DELETE | /api/users/{id}       | 删除用户     |
| GET    | /actuator/health      | 健康检查     |
| GET    | /h2-console           | H2 数据库控制台 |

## 本地运行

```bash
mvn spring-boot:run
# 访问 http://localhost:8080/hello
```

## 运行测试

```bash
mvn test                    # 单元测试
mvn verify                  # 含覆盖率报告
```

## Docker 运行

```bash
docker-compose up -d
# 访问 http://localhost:8080
```

## Jenkins 使用步骤

1. 创建 GitHub 仓库，推送本代码
2. Jenkins → 新建 Item → Pipeline
3. 配置 Git 仓库地址和凭据
4. Pipeline 定义选 "Pipeline script from SCM"
5. 指定 Jenkinsfile 路径
6. 运行流水线，观察各阶段执行情况

### Pipeline 阶段说明

| 阶段             | 说明                            |
|------------------|--------------------------------|
| Checkout         | 从 Git 拉取代码                 |
| Code Analysis    | 并行：代码验证 + 依赖检查       |
| Build            | Maven 编译                     |
| Test             | 并行运行单元测试，生成覆盖率    |
| Package          | 打包 JAR，归档构建产物          |
| Quality Gate     | 代码质量门禁 (Jacoco 覆盖率)    |
| Build Docker     | main分支才执行，构建镜像        |
| Deploy           | Docker Compose 部署            |
