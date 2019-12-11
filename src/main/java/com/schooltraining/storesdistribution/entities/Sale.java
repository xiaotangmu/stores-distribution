package com.schooltraining.storesdistribution.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Sale implements Serializable{

    private Integer storeId;//店铺id
    private String storeName;//店铺名称
    private BigDecimal turnover;//营业额
    private String year;//年
    private String mon;//月份
    private int orderNum;//订单量
}
