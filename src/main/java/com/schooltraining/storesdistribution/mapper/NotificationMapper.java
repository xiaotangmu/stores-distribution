package com.schooltraining.storesdistribution.mapper;

import com.schooltraining.storesdistribution.entities.Notification;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface NotificationMapper extends Mapper<Notification>{

    public int keepWithMemberRelation(@Param("userIds") List<Integer> userIds, @Param("notificationId") int notificationId);

    public int updateStatusWithUser(@Param("userIds") List<Integer> userIds, @Param("notificationId") int notificationId);

    List<Notification> getNotificationsByUserId(Integer userId);
}
