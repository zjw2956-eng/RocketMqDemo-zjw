package com.vega.rocketmq.consumer;

import com.vega.rocketmq.common.RocketMqConstant;

import lombok.extern.slf4j.Slf4j;

import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 广播消息消费者
 *
 * <h3>关键区别：{@code messageModel = MessageModel.BROADCASTING}</h3>
 *
 * <p>广播模式下：</p>
 * <ul>
 *   <li>同消费组的<b>每个消费者实例</b>都会收到<b>全部消息</b></li>
 *   <li>消费进度（offset）保存在消费者本地，不在 Broker 上</li>
 *   <li>适合场景：缓存刷新、配置同步、通知推送</li>
 * </ul>
 *
 * <p>举个例子：你启动了 3 个实例，发一条消息 → 3 个实例都会收到。</p>
 *
 * <h3>注意：</h3>
 * <p>广播模式下不支持顺序消费，也不支持消费进度服务端存储。</p>
 *
 * @author Vega.z
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = RocketMqConstant.TOPIC_SIMPLE,
        consumerGroup = RocketMqConstant.GROUP_BROADCAST,
        selectorExpression = "*",
        messageModel = MessageModel.BROADCASTING
)
public class BroadcastConsumer implements RocketMQListener<String> {

    /**
     * 消费消息（广播模式）
     *
     * <p>每个实例独立消费全量消息，互不影响。</p>
     *
     * @param message 消息体字符串
     */
    @Override
    public void onMessage(String message) {
        // 实现广播消费逻辑
        // 提示：所有接入这个消费组的实例都会收到同一条消息
        // 提示：适合刷新本地缓存、更新内存中的配置等场景
        log.info("广播消费收到消息：{}", message);
    }
}
