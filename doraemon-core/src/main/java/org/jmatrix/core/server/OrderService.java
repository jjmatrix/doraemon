package org.jmatrix.core.server;

import io.grpc.order.service.OrderQueryReply;
import io.grpc.order.service.OrderQueryRequest;
import io.grpc.order.service.OrderServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.jmatrix.core.jdbc.domain.Order;
import org.jmatrix.core.jdbc.service.OrderMapper;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by matrix on 2017/9/20.
 */
@GRpcService
public class OrderService extends OrderServiceGrpc.OrderServiceImplBase {

    private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private OrderMapper orderMapper;

    @Autowired
    public OrderService(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public void query(OrderQueryRequest request, StreamObserver<OrderQueryReply> responseObserver) {
        List<Order> orderList = orderMapper.findByOrderNo(request.getOrderNo());
        if (orderList == null || orderList.isEmpty()) {
            responseObserver.onNext(OrderQueryReply.newBuilder().build());
        } else {
            orderList.forEach(order -> {
                logger.info("order, info:{}", order);
            });
            Order order = orderList.get(0);
            responseObserver.onNext(OrderQueryReply.newBuilder()
                    .setOrderNo(order.getOrderNo())
                    .setName(order.getName())
                    .setPrice(order.getPrice().longValue())
                    .setDesc(order.getDesc())
                    .build());
        }

        responseObserver.onCompleted();
    }
}
