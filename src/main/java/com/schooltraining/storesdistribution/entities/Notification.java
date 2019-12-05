package com.schooltraining.storesdistribution.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name ="wms_notification")
public class Notification {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;
    private String title;
    private String content;
    private String createTime;
    private Integer userId;//推送消息的人--自己也能收到
    private Integer shopId;//所属分店
    @Transient
    private List<User> users;
}
