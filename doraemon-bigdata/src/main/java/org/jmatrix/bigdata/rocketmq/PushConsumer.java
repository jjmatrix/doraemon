package org.jmatrix.bigdata.rocketmq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * Created by matrix on 2017/9/5.
 */
public class PushConsumer {

    public static void main(String[] args) {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("cg_push_test_1");
        consumer.setNamesrvAddr("127.0.0.1:9876");
        try {

            consumer.subscribe("TopicTest", "TagA");

            // 程序第一次启动从消息队列头获取数据
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            //可以修改每次消费消息的数量，默认设置是每次消费一条
            consumer.setConsumeMessageBatchMaxSize(1);

            //注册消费的监听
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                //在此监听中消费信息，并返回消费的状态信息
                public ConsumeConcurrentlyStatus consumeMessage(
                        List<MessageExt> msgs,
                        ConsumeConcurrentlyContext context) {

                    // msgs中只收集同一个topic，同一个tag，并且key相同的message会把不同的消息分别放置到不同的队列中
                    for (MessageExt msg : msgs) {
                        System.out.println("ext:" + msg + ",msg:" + new String(msg.getBody()));
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

            consumer.start();
            Thread.sleep(5000);
            //5秒后挂载消费端消费
            consumer.suspend();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
