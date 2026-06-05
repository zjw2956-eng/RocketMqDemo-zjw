package com.vega.rocketmq.common;

/**
 * RocketMQ 公共常量
 *
 * <h3>命名规范提示：</h3>
 * <ul>
 *   <li>Topic 通常按业务领域划分，如 {@code order-topic}、{@code user-topic}</li>
 *   <li>ConsumerGroup 命名建议：{@code <业务名>-<功能>-consumer-group}</li>
 *   <li>Tag 是消息二级分类，同一个 Topic 下可以用 Tag 区分不同业务场景</li>
 * </ul>
 *
 * @author Vega.z
 */
public final class RocketMqConstant {

    private RocketMqConstant() {
        // 工具类，禁止实例化
    }

    // ==================== Topic ====================

    /** 普通消息测试 Topic */
    public static final String TOPIC_SIMPLE = "simple-message-topic";

    /** 顺序消息测试 Topic */
    public static final String TOPIC_ORDERLY = "orderly-message-topic";

    /** 事务消息测试 Topic */
    public static final String TOPIC_TRANSACTION = "transaction-message-topic";

    /** 延时消息测试 Topic */
    public static final String TOPIC_DELAY = "delay-message-topic";

    // ==================== Tag ====================

    /** 普通消息 Tag */
    public static final String TAG_DEFAULT = "default";

    /** 顺序消息 Tag（按业务场景细分） */
    public static final String TAG_ORDER_CREATE = "order-create";
    public static final String TAG_ORDER_PAY = "order-pay";

    // ==================== ConsumerGroup ====================

    /** 集群消费组 */
    public static final String GROUP_CLUSTER = "demo-cluster-consumer-group";

    /** 广播消费组 */
    public static final String GROUP_BROADCAST = "demo-broadcast-consumer-group";

    /** 顺序消费组 */
    public static final String GROUP_ORDERLY = "demo-orderly-consumer-group";

    /** 事务消息消费组 */
    public static final String GROUP_TRANSACTION = "demo-transaction-consumer-group";
}
