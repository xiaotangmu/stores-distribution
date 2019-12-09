package com.schooltraining.storesdistribution.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name ="oms_order_item")
public class OrderItem implements Serializable {//订单详情

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;
    private String orderSn;//对外的订单号 serial number
    private String createTime;
    private Integer userId;
    private String userName;
    private String phone;//联系电话
    private Integer itemId;//商品 id
    private String itemSn;//商品编号
    private String description;//订单描述
    private BigDecimal itemPrice;//商品单价
    private String itemNum;//购买数量
    private BigDecimal totalPrice;//总价
    private Long integration;//积分
    private String flag;//用作特殊标记
    private String remark;//备注
    private Integer storeId;//分店id
    private String storeName;//分店名
    private String storePhone;//分店电话
    //...


}
