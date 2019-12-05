package com.schooltraining.storesdistribution.service;

import com.schooltraining.storesdistribution.entities.Notification;

import java.util.List;

public interface NotificationService {

    public Notification add(Notification notification);

    public int keepWithMemberRelation(List<Integer> userIds, int shopId);

    public int updateStatusWithUser(List<Integer> userIds, int shopId);

}
