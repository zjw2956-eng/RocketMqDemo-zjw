package com.vega.rocketmq.producer;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 普通消息生产者 —— 三种发送方式
 *
 * <h3>核心概念（对比 RabbitMQ）：</h3>
 * <table>
 *   <tr><th>发送方式</th><th>特点</th><th>适用场景</th></tr>
 *   <tr><td>同步发送</td><td>等待 Broker 确认后返回 SendResult</td><td>重要通知、订单状态变更</td></tr>
 *   <tr><td>异步发送</td><td>不阻塞，结果通过回调返回</td><td>高吞吐场景、日志上报</td></tr>
 *   <tr><td>单向发送</td><td>发完就走，不关心结果</td><td>心跳包、链路追踪日志</td></tr>
 * </table>
 *
 * <h3>发送到 MessageQueue 的原理：</h3>
 * <p>Producer 发送消息时，默认通过轮询算法选择 Topic 下的某个 MessageQueue。
 * 一个 Topic 可以有多个 MessageQueue，分布在不同的 Broker 上。</p>
 *
 * @author Vega.z
 */
@Component
@Slf4j
public class SimpleMessageProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 【同步发送】—— 阻塞等待 Broker 确认
     *
     * <p>常用参数：</p>
     * <ul>
     *   <li>{@code destination}：格式为 {@code "topic:tag"}，如 {@code "order-topic:order-paid"}</li>
     *   <li>{@code message}：消息体，可以是字符串或对象（会被序列化为 JSON）</li>
     *   <li>{@code timeout}：超时时间，默认 3000ms</li>
     * </ul>
     *
     * @param topic   消息主题
     * @param tag     消息标签（二级分类）
     * @param message 消息内容
     * @return 发送结果，包含 msgId、queue 等信息
     */
    public void syncSend(String topic, String tag, String message) {
        //同步发送
        // 提示：destination 格式为 topic + ":" + tag
        // 返回值 SendResult 包含 msgId、sendStatus、messageQueue 等信息
        String destination = topic + ":" + tag;
        SendResult result = rocketMQTemplate.syncSend(destination, message);
        log.info("同步发送成功，msgId={}, topic={}, message={}", result.getMsgId(), topic, message);
    }

    /**
     * 【异步发送】—— 不阻塞，结果通过回调异步返回
     *
     * <p>需要实现 {@link SendCallback} 接口的两个方法：</p>
     * <ul>
     *   <li>{@code onSuccess(SendResult)}：发送成功时回调</li>
     *   <li>{@code onException(Throwable)}：发送失败时回调</li>
     * </ul>
     *
     * @param topic   消息主题
     * @param tag     消息标签
     * @param message 消息内容
     */
    public void asyncSend(String topic, String tag, String message) {
        // 使用 rocketMQTemplate.asyncSend() 实现异步发送
        // 提示：需要实现 SendCallback 接口
        String destination = topic + ":" + tag;
        rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步发送成功,msg={},queue={}", sendResult.getMsgId(), sendResult.getMessageQueue());
            }

            @Override
            public void onException(Throwable e) {
                log.error("异步发送失败,message={}", message, e);
            }
        });
        log.info("异步发送已提交，不等待结果");
    }

    /**
     * 【单向发送】—— 发完就走，不关心结果
     *
     * <p>性能最高，但不保证消息一定送达。适合日志采集、心跳等场景。</p>
     *
     * @param topic   消息主题
     * @param tag     消息标签
     * @param message 消息内容
     */
    public void sendOneWay(String topic, String tag, String message) {
        // 使用 rocketMQTemplate.sendOneWay() 实现单向发送
        // 提示：这个方法没有返回值
        String destination = topic + ":" + tag;
        rocketMQTemplate.sendOneWay(destination, message);
        log.info("单向发送完成（不保证送达），topic={}, message={}", topic, message);
    }

    /**
     * 【同步发送对象】—— 发送 POJO 对象，RocketMQ 自动 JSON 序列化
     *
     * <p>可以直接发 Java 对象，消费者收到的也是同一个对象（需要类路径一致）。</p>
     *
     * @param topic   消息主题
     * @param tag     消息标签
     * @param payload 任意 Java 对象
     * @param <T>     对象类型
     */
    public <T> void syncSendObject(String topic, String tag, T payload) {
        // 使用 rocketMQTemplate.syncSend() + MessageBuilder 发送对象
        // 提示：使用 MessageBuilder.withPayload(payload).setHeader(...).build()
        String destination = topic + ":" + tag;
        Message<T> msg = MessageBuilder.withPayload(payload).build();
        rocketMQTemplate.syncSend(destination, msg);
        log.info("对象发送成功，type={}, payload={}",
                payload.getClass().getSimpleName(), payload);
    }
}
