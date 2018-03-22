package org.jmatrix.core.jdbc.service;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.jmatrix.core.jdbc.common.MybatisMapperInterface;
import org.jmatrix.core.jdbc.domain.Order;

import java.util.List;

/**
 * Created by matrix on 2017/9/20.
 */
public interface OrderMapper extends MybatisMapperInterface {
    @Select("select * from order where order_no = #{0}")
    @Results(id = "order", value = {
            @Result(property = "id", column = "id", javaType = Long.class),
            @Result(property = "orderNo", column = "order_no")
    })
    List<Order> findByOrderNo(String orderNo);
}
