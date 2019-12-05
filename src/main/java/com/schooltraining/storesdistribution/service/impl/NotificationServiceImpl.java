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
    public int keepWithMemberRelation(List<Integer> userIds, int shopId){
        return notificationMapper.keepWithMemberRelation(userIds, shopId);
    }

    @Override
    public int updateStatusWithUser(List<Integer> userIds, int shopId) {
        return notificationMapper.updateStatusWithUser(userIds, shopId);
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
