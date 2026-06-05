package com.vega.rocketmq.producer;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 顺序消息生产者
 *
 * <h3>RocketMQ 如何保证顺序？</h3>
 * <ol>
 *   <li>生产者通过 {@code MessageQueueSelector} 将相同业务ID的消息发送到同一个 MessageQueue</li>
 *   <li>消费者端使用 {@code MessageListenerOrderly} 单线程消费同一个 MessageQueue</li>
 *   <li>这样同一个业务ID（如订单号）的消息就能保证 "先进先出"</li>
 * </ol>
 *
 * <h3>典型场景：</h3>
 * <ul>
 *   <li>订单状态流转：创建 → 支付 → 发货 → 完成（不能乱序）</li>
 *   <li>数据库 binlog 同步（必须按时间顺序）</li>
 * </ul>
 *
 * <h3>对比 RabbitMQ：</h3>
 * <p>RabbitMQ 只能在单个 Queue + 单个 Consumer 下勉强保证顺序，
 * RocketMQ 的顺序保障更灵活——只要指定相同的 bizKey 就行。</p>
 *
 * @author Vega.z
 */
@Component
public class OrderlyMessageProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送顺序消息
     *
     * <p>关键点：第三个参数 {@code hashKey} 决定了消息路由到哪个 MessageQueue。
     * 同一个 hashKey 的消息总是进入同一个队列，从而保证顺序。</p>
     *
     * <p>实现思路：</p>
     * <ol>
     *   <li>选取业务唯一标识作为 hashKey（如订单ID、用户ID）</li>
     *   <li>调用 {@code syncSendOrderly(destination, message, hashKey)}</li>
     *   <li>底层 {@code SelectMessageQueueByHash} 通过 hash(key) % queueSize 选择队列</li>
     * </ol>
     *
     * @param topic   消息主题
     * @param tag     消息标签
     * @param message 消息内容
     * @param hashKey 业务键（相同 key 进同一队列，保证顺序）
     */
    public void sendOrderlyMessage(String topic, String tag, String message, String hashKey) {
        // TODO: 使用 rocketMQTemplate.syncSendOrderly() 实现顺序消息发送
        // 提示：destination = topic + ":" + tag
        // 提示：hashKey 是保证顺序的核心——相同 hashKey → 相同 MessageQueue
        throw new UnsupportedOperationException("TODO: 实现顺序消息发送逻辑");
    }
}
