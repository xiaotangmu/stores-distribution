package com.schooltraining.storesdistribution.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name ="sms_store")
public class Store implements Serializable{

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;
    private String storeAddress;
    private String storeName;
    private String phone;
    private String email;
    private String description;

    @Transient
    private List<User> managers;
    @Transient
    private List<User> members;
}
