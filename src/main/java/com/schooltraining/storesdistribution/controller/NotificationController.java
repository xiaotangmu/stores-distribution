package com.schooltraining.storesdistribution.controller;

import com.schooltraining.storesdistribution.annotations.LoginRequired;
import com.schooltraining.storesdistribution.entities.Msg;
import com.schooltraining.storesdistribution.entities.Notification;
import com.schooltraining.storesdistribution.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins =  "*", maxAge = 3600)
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    Map<String, Object> returnMap = null;

    //获取消息
    @GetMapping("getNotification")
    @LoginRequired
    public Object getNotifications(HttpServletRequest request){
        returnMap = new HashMap<>();
        try {
            String userId = (String)request.getAttribute("userId");
            System.out.println(userId);
            List<Notification> notifications = notificationService.getNotificationsByUserId(Integer.parseInt(userId));
//            System.out.println(notifications);
            if (notifications != null && notifications.size() > 0) {
                returnMap.put("notifications", notifications);
                return Msg.success(returnMap);
            }

            returnMap.put("message", "获取数据失败");
            return Msg.fail(returnMap);
        } catch(Exception e) {
            e.printStackTrace();
            returnMap.put("message", "服务器异常");
            return Msg.fail(returnMap);
        }
    }
}
