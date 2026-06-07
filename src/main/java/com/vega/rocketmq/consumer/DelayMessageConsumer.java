package com.vega.rocketmq.consumer;

import com.vega.rocketmq.common.RocketMqConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 延时消息消费者
 *
 * 监听 delay-message-topic，验证延时投递效果。
 * 观察点：消息发送时间和消费时间的差值 = 延时时间。
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = RocketMqConstant.TOPIC_DELAY, consumerGroup = RocketMqConstant.GROUP_CLUSTER, selectorExpression = "*")
public class DelayMessageConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        log.info("延时消息已投递，当前时间={}, 消息内容={}", System.currentTimeMillis(), message);
    }

}
