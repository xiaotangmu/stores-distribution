package com.schooltraining.storesdistribution.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name ="sms_shop")
public class Shop {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;
    private String shopAddress;
    private String phone;
    private String email;

    @Transient
    private List<User> managers;
    @Transient
    private List<User> members;
}
