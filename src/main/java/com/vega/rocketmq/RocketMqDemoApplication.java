package com.vega.rocketmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RocketMQ Demo 启动类
 *
 * <h3>你需要了解：</h3>
 * <ul>
 *   <li>RocketMQ 采用 NameServer（注册中心）+ Broker（消息存储）+ 生产者/消费者 架构</li>
 *   <li>NameServer 是轻量级注册中心，不参与消息路由，只保存 Broker 元数据</li>
 *   <li>和 RabbitMQ 不同，RocketMQ 没有 Exchange 的概念，直接往 Topic 发消息</li>
 * </ul>
 *
 * <h3>本地 RocketMQ 启动顺序（WSL 中）：</h3>
 * <ol>
 *   <li>启动 NameServer：{@code nohup sh bin/mqnamesrv &}</li>
 *   <li>启动 Broker：{@code nohup sh bin/mqbroker -n localhost:9876 &}</li>
 *   <li>启动这个 Spring Boot 应用</li>
 * </ol>
 *
 * @author Vega.z
 */
@SpringBootApplication
public class RocketMqDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RocketMqDemoApplication.class, args);
    }
}
