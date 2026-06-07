package com.vega.rocketmq.controller;

import com.vega.rocketmq.common.RocketMqConstant;
import com.vega.rocketmq.producer.DelayMessageProducer;
import com.vega.rocketmq.producer.OrderlyMessageProducer;
import com.vega.rocketmq.producer.SimpleMessageProducer;
import com.vega.rocketmq.producer.TransactionMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 消息发送接口（方便用 Postman / curl 测试）
 *
 * <h3>测试建议：</h3>
 * <ol>
 * <li>先启动 WSL 中的 NameServer 和 Broker</li>
 * <li>启动这个 Spring Boot 应用</li>
 * <li>用 Postman 逐个调用这些接口</li>
 * <li>观察控制台日志，理解每种消息的行为差异</li>
 * </ol>
 *
 * @author Vega.z
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private SimpleMessageProducer simpleMessageProducer;

    @Autowired
    private OrderlyMessageProducer orderlyMessageProducer;

    @Autowired
    private TransactionMessageProducer transactionMessageProducer;

    @Autowired
    private DelayMessageProducer delayMessageProducer;

    // ==================== 普通消息 ====================

    /**
     * 发送普通消息（同步方式）
     *
     * <p>
     * 测试方式：{@code POST http://localhost:8080/api/message/simple}
     * Body: {@code "测试消息内容"}
     * </p>
     */
    @PostMapping("/simple")
    public String sendSimpleMessage(@RequestBody String message) {
        // 调用 simpleMessageProducer.syncSend() 发送消息
        simpleMessageProducer.syncSend(
                RocketMqConstant.TOPIC_SIMPLE,
                RocketMqConstant.TAG_DEFAULT,
                message);
        return "同步消息发送成功：" + message;
    }

    @PostMapping("/simple/async")
    public String sendSimpleMessageAsync(@RequestBody String message) {
        // 调用 simpleMessageProducer.asyncSend() 发送异步消息
        // 提示：异步发送立即返回，结果通过回调打印到日志
        simpleMessageProducer.asyncSend(RocketMqConstant.TOPIC_SIMPLE, RocketMqConstant.TAG_DEFAULT, message);
        return "异步消息已提交" + message;
    }

    @PostMapping("/simple/oneway")
    public String sendSimpleMessageOneWay(@RequestBody String message) {
        // 调用 simpleMessageProducer.sendOneWay() 单向发送
        simpleMessageProducer.sendOneWay(
                RocketMqConstant.TOPIC_SIMPLE,
                RocketMqConstant.TAG_DEFAULT,
                message);
        return "单向消息已发送（不保证送达）：" + message;
    }

    // ==================== 顺序消息 ====================

    /**
     * 发送顺序消息
     *
     * <p>
     * 测试方式：{@code POST http://localhost:8080/api/message/orderly?bizKey=order-123}
     * Body: {@code "订单消息内容"}
     * </p>
     *
     * <p>
     * 观察点：同一个 bizKey 的消息，消费者端是按发送顺序接收的
     * </p>
     */
    @PostMapping("/orderly")
    public String sendOrderlyMessage(@RequestBody String message,
            @RequestParam String bizKey) {
        // 调用 orderlyMessageProducer.sendOrderlyMessage() 发送顺序消息
        orderlyMessageProducer.sendOrderlyMessage(
                RocketMqConstant.TOPIC_ORDERLY,
                RocketMqConstant.TAG_ORDER_CREATE,
                message,
                bizKey);
        return "顺序消息发送成功，bizKey=" + bizKey;
    }

    // ==================== 事务消息 ====================

    /**
     * 发送事务消息
     *
     * <p>
     * 测试方式：{@code POST http://localhost:8080/api/message/transaction}
     * Body: {@code "事务消息内容"}
     * </p>
     *
     * <p>
     * 观察点：
     * </p>
     * <ul>
     * <li>本地事务成功 → 消费者收到消息</li>
     * <li>本地事务失败 → 消费者收不到消息</li>
     * <li>模拟超时 → Broker 回查，观察回查逻辑</li>
     * </ul>
     */
    @PostMapping("/transaction")
    public String sendTransactionMessage(@RequestBody String message) {
        // 调用 transactionMessageProducer.sendTransactionMessage() 发送事务消息
        transactionMessageProducer.sendTransactionMessage(
                RocketMqConstant.TOPIC_TRANSACTION,
                RocketMqConstant.TAG_DEFAULT,
                message,
                message);
        return "事务消息已发送：" + message;
    }

    // ==================== 延时消息 ====================

    /**
     * 发送延时消息
     *
     * <p>
     * 测试方式：{@code POST http://localhost:8080/api/message/delay?delayLevel=3}
     * Body: {@code "延时消息内容"}
     * </p>
     *
     * <p>
     * delayLevel 对照表（只能用 1-18）：
     * </p>
     * 
     * <pre>
     * 1=1s   2=5s   3=10s   4=30s   5=1m   6=2m   7=3m   8=4m   9=5m
     * 10=6m  11=7m  12=8m  13=9m  14=10m 15=20m 16=30m 17=1h 18=2h
     * </pre>
     *
     * <p>
     * 观察点：消费者不会立即收到消息，而是在指定时间后才收到
     * </p>
     */
    @PostMapping("/delay")
    public String sendDelayMessage(@RequestBody String message,
            @RequestParam(defaultValue = "3") int delayLevel) {
        // 调用 delayMessageProducer.sendDelayMessage() 发送延时消息
        delayMessageProducer.sendDelayMessage(
                RocketMqConstant.TOPIC_DELAY,
                RocketMqConstant.TAG_DEFAULT,
                message,
                delayLevel);
        return "延时消息发送成功，delayLevel=" + delayLevel + "（" + getDelayDesc(delayLevel) + "）";
    }

    // 可选：把延时级别翻译成中文，返回提示更友好
    private String getDelayDesc(int level) {
        String[] desc = { "", "1s", "5s", "10s", "30s", "1m", "2m", "3m", "4m", "5m",
                "6m", "7m", "8m", "9m", "10m", "20m", "30m", "1h", "2h" };
        return level >= 1 && level <= 18 ? desc[level] : "未知";
    }
}
