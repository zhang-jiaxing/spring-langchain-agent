# SpringBoot LangChain Agent 🤖

基于 **Spring Boot 3** 和 **LangChain4j** 构建的大模型 Agent 项目，集成了**阿里通义千问 (DashScope)** 模型，并演示了从基础流式对话到复杂 RAG（检索增强生成）系统的实现。

## 📦 项目结构

本项目是一个 Maven 多模块工程，包含以下核心模块：

- **`agent-hello`**：基础流式对话 Agent。演示了如何快速集成大模型，并通过 Spring WebFlux 提供 Server-Sent Events (SSE) 流式接口。
- **`agent-faq`**：智能问答 / RAG Agent。演示了如何结合向量数据库（Milvus）和文档解析（Apache Tika），构建基于本地知识库（如学生手册）的问答系统，并利用 MongoDB 持久化聊天上下文（Chat Memory）。


## 🛠️ 技术栈

- **核心框架**: Java 17 + Spring Boot 3.2.3
- **大模型生态**: LangChain4j (1.0.0-beta)
- **大语言模型**: 阿里通义千问 DashScope (qwen-turbo / text-embedding-v3)
- **向量数据库**: Milvus (用于知识库文档的向量化存储与检索)
- **数据持久化**: MongoDB (用于存储历史对话记录 Chat Memory)
- **响应式编程**: Spring WebFlux / Reactor (提供流畅的流式打字机效果)
- **文档解析**: Apache Tika

## 🚀 快速开始

### 1. 环境准备

确保您的本地环境已经安装并运行以下组件：
- **JDK 17** 或更高版本
- **Maven 3.8+**
- **MongoDB** (默认连接: `localhost:27017`，用于 `agent-faq` 的记忆存储)
- **Milvus** (默认连接: `localhost:19530`，用于 `agent-faq` 的向量检索)

### 2. 配置环境变量

项目使用阿里云的通义千问大模型，您需要获取 DashScope 的 API Key。
建议将其配置为系统环境变量：

**Windows (CMD/PowerShell):**
```powershell
setx DASHSCOPE_API_KEY "您的_API_KEY"
```

**Linux/macOS:**
```bash
export DASHSCOPE_API_KEY="您的_API_KEY"
```

*(或者您可以直接在对应模块的 `application.yml` 中将 `${DASHSCOPE_API_KEY}` 替换为实际的 Key)*

### 3. 启动项目

#### 启动 agent-hello (基础流式对话)
```bash
cd agent-hello
mvn spring-boot:run
```
服务将在 `8081` 端口启动。

#### 启动 agent-faq (RAG 本地知识库问答)
*启动前请确保 MongoDB 和 Milvus 服务已就绪。*
```bash
cd agent-faq
mvn spring-boot:run
```
服务将在 `8082` 端口启动。

#### 单元测试 agent-faq (RAG 本地知识库问答)
*启动前请确保 MongoDB 和 Milvus 服务已就绪。*
```
@Test
    void testStreamingAssistant() {
        Flux<String> flux = assistant.chat(1L,"你好，我是大连交通大学的大一新生,依然。你是谁？");
        flux
                .doOnNext(System.out::print)
                .doOnComplete(() -> System.out.println("\nflux：对话结束"))
                .doOnError(e -> System.out.println("对话出错："+e))
                .blockLast(Duration.ofSeconds(30));
        Flux<String> chat = assistant.chat(1L, "我刚才的问题是什么？");
        chat
                .doOnNext(System.out::print)
                .doOnComplete(() -> System.out.println("\nchat：对话结束"))
                .doOnError(e -> System.out.println("对话出错："+e))
                .blockLast(Duration.ofSeconds(30));
        Flux<String> chat2 = assistant.chat(2L, "我刚才的问题是什么？");
        chat2
                .doOnNext(System.out::print)
                .doOnComplete(() -> System.out.println("\nchat2：对话结束"))
                .doOnError(e -> System.out.println("对话出错："+e))
                .blockLast(Duration.ofSeconds(30));
    }
```

## 🔗 API 接口说明

### 1. agent-hello 模块
- **接口地址**: `GET /hello/ask-stream`
- **参数**: `input` (String) - 您的提问内容
- **返回类型**: `text/event-stream` (SSE)
- **测试示例**:
  ```bash
  curl -N "http://localhost:8081/hello/ask-stream?input=你好，请介绍一下你自己"
  ```

### 2. agent-faq 模块
- **接口地址**: `GET /chat/stream`
- **参数**: `message` (String) - 您的提问内容
- **返回类型**: `text/event-stream` (SSE)
- **功能描述**: 结合本地知识库（如 `documents/学生手册.txt`）以及 MongoDB 历史对话记录为您进行专业解答。
- **测试示例**:
  ```bash
  curl -N "http://localhost:8082/chat/stream?message=请根据学生手册总结一下考勤规定？"
  ```

## 📝 注意事项

- 本项目使用了 `@Slf4j` 等 Lombok 注解，如果在 IDE 中运行，请确保已安装 Lombok 插件并开启了 Annotation Processing。
- 首次运行 `agent-faq` 时，可能会触发文本向量化（Embedding）操作，请耐心等待数据写入 Milvus 库中。
