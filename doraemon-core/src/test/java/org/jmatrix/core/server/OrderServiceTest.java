package org.jmatrix.core.server;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.order.service.OrderQueryReply;
import io.grpc.order.service.OrderQueryRequest;
import io.grpc.order.service.OrderServiceGrpc;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by matrix on 2017/9/20.
 */
public class OrderServiceTest {

    private ManagedChannel channel;

    @Before
    public void setUp() throws Exception {
        channel = ManagedChannelBuilder.forAddress("127.0.0.1", 6565)
                .usePlaintext(true)
                .build();
    }

    @Test
    public void testQuery() throws Exception {
        OrderServiceGrpc.OrderServiceBlockingStub orderServiceBlockingStub = OrderServiceGrpc.newBlockingStub(channel);
        OrderQueryRequest queryRequest = OrderQueryRequest.newBuilder().setOrderNo("10000").build();
        OrderQueryReply queryReply = orderServiceBlockingStub.query(queryRequest);
        System.out.println("reply:" + queryReply);
    }

}