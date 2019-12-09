package com.schooltraining.storesdistribution.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class NotificatinItem {

    private Integer id;//消息编号
    private String title;
    private String content;
    private String createTime;
    private Integer managerId;//推送消息的会员id
    private Integer managerName;//推送消息的人
    private Integer storeId;//所属分店
    private Integer memberId;//所有者 -- 谁的消息
    private String status;//消息状态

}
