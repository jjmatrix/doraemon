package org.jmatrix.bigdata.rocketmq;

import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.common.message.Message;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by matrix on 2017/9/5.
 */
public class TransactionExecuterImpl implements LocalTransactionExecuter {

    private AtomicInteger transactionIndex = new AtomicInteger(1);

    @Override
    public LocalTransactionState executeLocalTransactionBranch(Message msg, Object arg) {
        int value = transactionIndex.getAndIncrement();

        if (value == 0) {
            throw new RuntimeException("Could not find db");
        } else if ((value % 5) == 0) {
            return LocalTransactionState.ROLLBACK_MESSAGE;
        } else if ((value % 4) == 0) {
            return LocalTransactionState.COMMIT_MESSAGE;
        }

        return LocalTransactionState.UNKNOW;
    }

}
