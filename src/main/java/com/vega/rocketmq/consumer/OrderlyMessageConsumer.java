package com.vega.rocketmq.consumer;

import com.vega.rocketmq.common.RocketMqConstant;

import lombok.extern.slf4j.Slf4j;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 顺序消息消费者
 *
 * <h3>关键配置：{@code consumeMode = ConsumeMode.ORDERLY}</h3>
 *
 * <p>顺序消费的原理：</p>
 * <ol>
 *   <li>生产者将同一个 bizKey 的消息路由到<b>同一个 MessageQueue</b></li>
 *   <li>消费者端加锁，确保同一个 MessageQueue<b>同一时刻只被一个线程消费</b></li>
 *   <li>消费成功后，才拉取下一条消息</li>
 * </ol>
 *
 * <h3>⚠️ 顺序消费 vs 并发消费（CONCURRENTLY，默认）</h3>
 * <table>
 *   <tr><th>顺序消费</th><th>并发消费</th></tr>
 *   <tr><td>单线程顺序处理</td><td>多线程并行处理</td></tr>
 *   <tr><td>吞吐量较低</td><td>吞吐量高</td></tr>
 *   <tr><td>保证 FIFO</td><td>不保证顺序</td></tr>
 * </table>
 *
 * <h3>注意：</h3>
 * <p>顺序消费虽然保证 FIFO，但会牺牲吞吐量。只在真正需要顺序的场景使用。</p>
 *
 * @author Vega.z
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = RocketMqConstant.TOPIC_ORDERLY,
        consumerGroup = RocketMqConstant.GROUP_ORDERLY,
        selectorExpression = "*",
        consumeMode = ConsumeMode.ORDERLY
)
public class OrderlyMessageConsumer implements RocketMQListener<String> {

    /**
     * 顺序消费消息
     *
     * <p>保证同一个 MessageQueue 内的消息按发送顺序被消费。
     * 注意：只有同一个 MessageQueue 内才保证顺序！</p>
     *
     * @param message 消息体字符串
     */
    @Override
    public void onMessage(String message) {
        // 实现顺序消费逻辑
        // 提示：同一个 MessageQueue 的消息是单线程消费，不用担心线程安全问题
        // 提示：处理失败抛异常会触发重试，重试期间该队列后续消息会被阻塞
        log.info("顺序消费收到消息：{}", message);
    }
}
