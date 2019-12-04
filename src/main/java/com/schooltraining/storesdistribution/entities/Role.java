package com.schooltraining.storesdistribution.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name ="ams_role")
public class Role {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(generator = "JDBC")
    private Integer id;
    private String roleName;
    private String description;

    @Transient
    private List<Authority> authorities;
}
