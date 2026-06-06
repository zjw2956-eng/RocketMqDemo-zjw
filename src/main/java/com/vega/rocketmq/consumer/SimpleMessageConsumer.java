package com.vega.rocketmq.consumer;

import com.vega.rocketmq.common.RocketMqConstant;

import lombok.extern.slf4j.Slf4j;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 普通消息消费者 —— 集群消费模式（默认）
 *
 * <h3>集群消费 vs 广播消费</h3>
 * <table>
 *   <tr><th>集群消费（CLUSTERING，默认）</th><th>广播消费（BROADCASTING）</th></tr>
 *   <tr><td>同组消费者平均分摊消息</td><td>同组消费者每人收到全量消息</td></tr>
 *   <tr><td>一条消息只被消费一次</td><td>一条消息被每个消费者消费一次</td></tr>
 *   <tr><td>适合业务处理（订单、支付）</td><td>适合配置刷新、缓存更新</td></tr>
 * </table>
 *
 * <h3>关键注解参数：</h3>
 * <ul>
 *   <li>{@code topic}：监听的主题</li>
 *   <li>{@code consumerGroup}：消费组名，同组共享消费进度</li>
 *   <li>{@code selectorExpression}：过滤 Tag，默认 "*" 表示所有</li>
 *   <li>{@code consumeThreadMax}：最大消费线程数（默认 64）</li>
 * </ul>
 *
 * @author Vega.z
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = RocketMqConstant.TOPIC_SIMPLE,
        consumerGroup = RocketMqConstant.GROUP_CLUSTER,
        selectorExpression = "*"
)
public class SimpleMessageConsumer implements RocketMQListener<String> {

    /**
     * 消费消息
     *
     * <p>注意：</p>
     * <ul>
     *   <li>此方法由 RocketMQ 线程池调用，不要在里面写阻塞代码</li>
     *   <li>抛出异常会触发重试，默认最多重试 16 次</li>
     *   <li>重试 16 次后仍失败，消息进入死信队列（DLQ）</li>
     *   <li>返回 null 或正常返回代表消费成功，会提交 offset</li>
     * </ul>
     *
     * @param message 消息体字符串
     */
    @Override
    public void onMessage(String message) {
        // 实现消息消费逻辑
        // 提示：RocketMQ 自动提交 offset，无需手动 ack
        // 提示：处理失败直接抛异常即可触发重试
        log.info("收到普通消息：{}", message);
    }
}
