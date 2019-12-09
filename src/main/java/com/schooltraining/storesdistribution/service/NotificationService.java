package com.schooltraining.storesdistribution.service;

import com.schooltraining.storesdistribution.entities.Notification;

import java.util.List;

public interface NotificationService {

    public Notification add(Notification notification);

    public int keepWithMemberRelation(List<Integer> userIds, int notificationId);

    public int updateStatusWithUser(List<Integer> userIds, int notificationId);

    List<Notification> getNotificationsByUserId(Integer userId);
}
