package com.vega.rocketmq.producer;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 延时消息生产者
 *
 * <h3>核心概念：</h3>
 * <p>消息发送后不会立即被消费，而是等待指定时间后才投递给消费者。</p>
 *
 * <h3>⚠️ 重要限制：</h3>
 * <p>RocketMQ 不支持任意时间的延时！只支持 18 个预设级别（开源版）：</p>
 * <pre>
 * 级别 1: 1s      级别 2: 5s      级别 3: 10s
 * 级别 4: 30s     级别 5: 1m      级别 6: 2m
 * 级别 7: 3m      级别 8: 4m      级别 9: 5m
 * 级别10: 6m      级别11: 7m      级别12: 8m
 * 级别13: 9m      级别14: 10m     级别15: 20m
 * 级别16: 30m     级别17: 1h      级别18: 2h
 * </pre>
 * <p>设置延时：{@code message.setDelayTimeLevel(3)} 表示延迟 10 秒</p>
 *
 * <h3>典型场景：</h3>
 * <ul>
 *   <li>下单 30 分钟未支付自动取消</li>
 *   <li>延时发送短信/邮件通知</li>
 *   <li>定时任务（相比 cron 更轻量）</li>
 * </ul>
 *
 * <h3>对比 RabbitMQ：</h3>
 * <p>RabbitMQ 通过死信队列 + TTL 模拟延时，配置复杂。
 * RocketMQ 原生支持，但弹性差（只能选 18 个级别）。</p>
 *
 * @author Vega.z
 */
@Slf4j
@Component
public class DelayMessageProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送延时消息
     *
     * <p>实现思路：</p>
     * <ol>
     *   <li>构造消息并设置 delayTimeLevel</li>
     *   <li>调用同步发送方法</li>
     *   <li>消息在 Broker 端等待指定时间后才投递给消费者</li>
     * </ol>
     *
     * <p>注意：需要把 delayTimeLevel 设置到消息对象上，而不是用普通 String 发送。
     * 提示：使用 {@code org.apache.rocketmq.common.message.Message} 或者
     * {@code org.springframework.messaging.Message} + header 设置延时级别。</p>
     *
     * @param topic          消息主题
     * @param tag            消息标签
     * @param message        消息内容
     * @param delayTimeLevel 延时级别（1-18），见类注释中的映射表
     */
    public void sendDelayMessage(String topic, String tag, String message, int delayTimeLevel) {
        // 使用 RocketMQ 延时消息功能发送
        // 提示1：不能直接 syncSend(topic:tag, msg)，需要构造 Message 对象设置延迟级别
        // 提示2：rocketMQTemplate.syncSend() 的重载版本接受 org.apache.rocketmq.common.message.Message
        // 提示3：Message.setDelayTimeLevel(delayTimeLevel) 设置延时级别
        String destination = topic + ":" + tag;
        //构造RocketMQ原生Message对象
        Message msg=new Message(topic, tag, message.getBytes());
        msg.setDelayTimeLevel(delayTimeLevel);
        //注意：syncSend参数是topic:tag和Message,不是String
        SendResult result=rocketMQTemplate.syncSend(destination, msg);
        log.info("延时消息发送成功，delayLevel={}, msgId={}", delayTimeLevel, result.getMsgId());
    }
}
