package com.schooltraining.storesdistribution.service.impl;

import com.schooltraining.storesdistribution.entities.Notification;
import com.schooltraining.storesdistribution.mapper.NotificationMapper;
import com.schooltraining.storesdistribution.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    NotificationMapper notificationMapper;

    @Override
    public int keepWithMemberRelation(List<Integer> userIds, int notificationId){
        return notificationMapper.keepWithMemberRelation(userIds, notificationId);
    }

    @Override
    public int updateStatusWithUser(List<Integer> userIds, int notificationId) {
        return notificationMapper.updateStatusWithUser(userIds, notificationId);
    }

    @Override
    public List<Notification> getNotificationsByUserId(Integer userId) {
        if(userId != null){
            List<Notification> notifications = notificationMapper.getNotificationsByUserId(userId);
            if (notifications != null && notifications.size() > 0) {
                return notifications;
            }
        }
        return null;
    }

    @Override
    public Notification add(Notification notification) {
    	int insert = notificationMapper.insert(notification);
        if(insert != 0){
            return notification;
        }
        return null;
    }
}
