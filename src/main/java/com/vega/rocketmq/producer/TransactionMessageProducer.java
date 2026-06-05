package com.vega.rocketmq.producer;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 事务消息生产者
 *
 * <h3>最精华的设计 —— 分布式事务最终一致性方案</h3>
 *
 * <p>RocketMQ 的事务消息流程分三步：</p>
 * <ol>
 *   <li><b>发送半消息</b>（half message）：消息先发给 Broker，但对消费者不可见</li>
 *   <li><b>执行本地事务</b>：你的业务代码（如扣库存、写订单表）</li>
 *   <li><b>提交/回滚</b>：本地事务成功 → 提交（消息对消费者可见）；失败 → 回滚（消息删除）</li>
 * </ol>
 *
 * <p>如果第3步因为网络问题没执行，Broker 会定期<b>回查</b>（check）生产者，
 * 询问本地事务的最终状态。这就是 {@code RocketMQLocalTransactionListener} 的职责。</p>
 *
 * <pre>
 * Producer                          Broker                          Consumer
 *    |                                 |                                |
 *    |---(1) 发送半消息 ---------------->| (消息暂存，不可见)              |
 *    |                                 |                                |
 *    |---(2) 执行本地事务（扣库存）       |                                |
 *    |        |                        |                                |
 *    |    ┌─成功→(3) 提交消息----------->| (消息变为可见) ──推送──> |   消费
 *    |    └─失败→(3) 回滚消息----------->| (消息删除)                    |
 *    |                                 |                                |
 *    |  网络超时...                     |                                |
 *    |       ←──(4) 回查本地事务状态────|                                |
 *    |─────返回 COMMIT/ROLLBACK────────→|                                |
 * </pre>
 *
 * <h3>对比 RabbitMQ：</h3>
 * <p>RabbitMQ 没有原生事务消息支持，需要自己实现本地消息表 + 定时任务补偿。</p>
 *
 * @author Vega.z
 */
@Component
public class TransactionMessageProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送事务消息
     *
     * <p>你需要两个东西：</p>
     * <ol>
     *   <li>调用 {@code sendMessageInTransaction()} 发送事务消息</li>
     *   <li>实现 {@code RocketMQLocalTransactionListener} 来处理本地事务执行和回查
     *       （见 {@link com.vega.rocketmq.consumer.TransactionMessageConsumer}）</li>
     * </ol>
     *
     * @param topic   消息主题
     * @param tag     消息标签
     * @param message 消息内容
     * @param arg     传给本地事务执行器的参数（如业务ID）
     */
    public void sendTransactionMessage(String topic, String tag, String message, Object arg) {
        // TODO: 使用 rocketMQTemplate.sendMessageInTransaction() 发送事务消息
        // 提示：需要先注册一个 RocketMQLocalTransactionListener（见 TransactionMessageConsumer）
        // 提示：arg 会传给 executeLocalTransaction() 和 checkLocalTransaction()
        throw new UnsupportedOperationException("TODO: 实现事务消息发送逻辑");
    }
}
