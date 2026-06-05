# RocketMQ Demo 学习笔记

> 作者：Vega.z  
> 项目目标：从 RabbitMQ 迁移思维到 RocketMQ，掌握 RocketMQ 核心特性

---

## 一、环境信息

| 项目 | 信息 |
|---|---|
| RocketMQ 版本 | （你安装后填这里） |
| Spring Boot 版本 | 3.2.0 |
| Java 版本 | 17 |
| 部署方式 | WSL（Ubuntu） |

## 二、WSL 部署 RocketMQ 步骤

> 请把你的部署过程记录在这里（命令、遇到的问题、解决方案）

### 2.1 下载 RocketMQ

```bash
# 你的下载命令...
```

### 2.2 启动 NameServer

```bash
# 启动命令 & 日志位置...
```

### 2.3 启动 Broker

```bash
# 启动命令 & 参数说明...
```

### 2.4 验证部署

```bash
# 验证命令...
```

---

## 三、RocketMQ 核心概念理解

> 用自己的话总结（不要照抄文档）

### 3.1 NameServer vs Broker

（写你的理解）

### 3.2 Topic vs Tag

（写你的理解）

### 3.3 MessageQueue

（写你的理解）

### 3.4 消费组

（写你的理解）

---

## 四、核心特性学习记录

### 4.1 普通消息（三种发送方式）

- [ ] 同步发送 —— 理解要点：
- [ ] 异步发送 —— 理解要点：
- [ ] 单向发送 —— 理解要点：

### 4.2 顺序消息

- [ ] 生产者如何保证路由到同一队列？
- [ ] 消费者如何保证顺序消费？
- [ ] 顺序消息的代价是什么？

### 4.3 事务消息 ⭐（最重要）

- [ ] 半消息是什么？
- [ ] 本地事务执行器的职责？
- [ ] 回查机制的触发条件？
- [ ] 和 RabbitMQ 的区别？

### 4.4 延时消息

- [ ] 18 个延迟级别分别是什么？
- [ ] 如果我要延迟 15 分钟，用哪个级别？
- [ ] 局限性是什么？

### 4.5 集群消费 vs 广播消费

- [ ] 集群消费的使用场景？
- [ ] 广播消费的使用场景？
- [ ] offset 存储位置的区别？

---

## 五、RocketMQ vs RabbitMQ 对比总结

| 维度 | RabbitMQ | RocketMQ | 我的判断 |
|---|---|---|---|
| 消息模型 | Exchange → Queue | Topic → MessageQueue | |
| 顺序消息 | 难保证 | 原生支持 | |
| 事务消息 | 自己实现 | 原生支持 | |
| 延时消息 | 死信 TTL 模拟 | 18 级别 | |
| 吞吐量 | 中 | 高（万级 TPS） | |
| 运维复杂度 | 低 | 中 | |
| 适合场景 | | | |

---

## 六、踩坑记录

| 日期 | 问题描述 | 原因 | 解决方案 |
|---|---|---|---|
| | | | |

---

## 七、Demo 运行备忘

### 启动顺序

```bash
# 1. WSL 中启动 NameServer


# 2. WSL 中启动 Broker


# 3. 启动 Spring Boot 应用
cd /mnt/d/GitHubRepository/RocketMqDemo
mvn spring-boot:run
```

### 测试 curl 命令

```bash
# 普通消息
curl -X POST http://localhost:8080/api/message/simple \
  -H "Content-Type: application/json" \
  -d "Hello RocketMQ"

# 顺序消息
curl -X POST "http://localhost:8080/api/message/orderly?bizKey=order-001" \
  -H "Content-Type: application/json" \
  -d "订单创建"

# 事务消息
curl -X POST http://localhost:8080/api/message/transaction \
  -H "Content-Type: application/json" \
  -d "事务消息测试"

# 延时消息（10 秒后消费）
curl -X POST "http://localhost:8080/api/message/delay?delayLevel=3" \
  -H "Content-Type: application/json" \
  -d "10秒后你会看到我"
```
