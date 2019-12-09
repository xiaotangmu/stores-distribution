package com.schooltraining.storesdistribution.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name ="ums_user")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String userName;
    private String password;
    private String phone;
    private String email;
    private String gender;//1 为男，0 为女
    private Integer roleId;
    private Integer storeId;//所属分店
    private Long integration;//积分
    
    @Transient
    private Role role;
//    @Transient
//    private List<Role> roles;

}
