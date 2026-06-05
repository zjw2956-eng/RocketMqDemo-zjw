package com.vega.rocketmq.consumer;

import com.vega.rocketmq.common.RocketMqConstant;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * 事务消息 —— 本地事务执行器 + 回查监听器
 *
 * <h3>⚠️ 这个类有双重身份：</h3>
 * <ol>
 *   <li>实现了 {@link RocketMQLocalTransactionListener} — 负责事务消息的"执行"和"回查"</li>
 *   <li>实现了 {@link RocketMQListener} — 负责消费已提交的事务消息（业务消费者）</li>
 * </ol>
 *
 * <h3>两个核心方法：</h3>
 * <table>
 *   <tr><th>方法</th><th>调用时机</th><th>返回</th></tr>
 *   <tr>
 *       <td>{@code executeLocalTransaction}</td>
 *       <td>半消息发送成功后，Broker 回调</td>
 *       <td>COMMIT（消息对消费者可见）或 ROLLBACK（消息删除）</td>
 *   </tr>
 *   <tr>
 *       <td>{@code checkLocalTransaction}</td>
 *       <td>Broker 长时间未收到 COMMIT/ROLLBACK 时主动回查</td>
 *       <td>COMMIT / ROLLBACK / UNKNOWN（稍后再问）</td>
 *   </tr>
 * </table>
 *
 * <h3>关键点：</h3>
 * <p>{@code checkLocalTransaction} 需要查询数据库确认本地事务的最终状态，
 * 绝对不能返回 UNKNOWN 死循环！</p>
 *
 * @author Vega.z
 */
@Component
@RocketMQMessageListener(
        topic = RocketMqConstant.TOPIC_TRANSACTION,
        consumerGroup = RocketMqConstant.GROUP_TRANSACTION,
        selectorExpression = "*"
)
public class TransactionMessageConsumer
        implements RocketMQListener<String>, RocketMQLocalTransactionListener {

    // ==================== 业务消费（RocketMQListener） ====================

    /**
     * 消费已提交的事务消息
     *
     * <p>只有本地事务执行成功（COMMIT）的消息才会被投递到这里。</p>
     *
     * @param message 消息体字符串
     */
    @Override
    public void onMessage(String message) {
        // TODO: 实现事务消息的业务消费逻辑
        // 提示：能走到这里的消息，说明本地事务已经提交成功了
    }

    // ==================== 本地事务执行（RocketMQLocalTransactionListener） ====================

    /**
     * 执行本地事务
     *
     * <p>典型流程：</p>
     * <ol>
     *   <li>从 {@code msg} 中取出业务参数</li>
     *   <li>执行本地数据库操作（如扣减库存）</li>
     *   <li>根据数据库操作结果返回 COMMIT 或 ROLLBACK</li>
     * </ol>
     *
     * @param msg  包含业务参数的消息
     * @param arg  发送时传入的额外参数（见 {@link com.vega.rocketmq.producer.TransactionMessageProducer}）
     * @return COMMIT（提交）或 ROLLBACK（回滚）
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        // TODO: 实现本地事务逻辑
        // 提示1：从 msg.getPayload() 或 msg.getHeaders() 获取业务数据
        // 提示2：执行数据库操作（增删改）
        // 提示3：成功返回 RocketMQLocalTransactionState.COMMIT
        // 提示4：失败返回 RocketMQLocalTransactionState.ROLLBACK
        // 提示5：不确定就返回 RocketMQLocalTransactionState.UNKNOWN，等 Broker 回查
        throw new UnsupportedOperationException("TODO: 实现本地事务执行逻辑");
    }

    /**
     * 回查本地事务状态（Broker 主动调用）
     *
     * <p>何时触发：</p>
     * <ul>
     *   <li>{@code executeLocalTransaction} 返回了 UNKNOWN</li>
     *   <li>{@code executeLocalTransaction} 执行超时</li>
     *   <li>Broker 重启后重新检查</li>
     * </ul>
     *
     * <p>实现要点：</p>
     * <ol>
     *   <li>从消息中取出业务唯一标识（如订单号）</li>
     *   <li>查数据库确认该业务是否已处理</li>
     *   <li>已处理返回 COMMIT，未处理返回 ROLLBACK</li>
     *   <li>⚠️ 没把握时返回 UNKNOWN，但不要无限返回 UNKNOWN</li>
     * </ol>
     *
     * @param msg 需要回查的消息
     * @return 事务最终状态
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        // TODO: 实现本地事务回查逻辑
        // 提示1：从 msg 中提取业务ID
        // 提示2：查询数据库确认该业务是否已处理
        // 提示3：查到了返回 COMMIT，没查到返回 ROLLBACK
        throw new UnsupportedOperationException("TODO: 实现事务回查逻辑");
    }
}
