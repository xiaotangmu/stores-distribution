package com.schooltraining.storesdistribution.newspush;

import com.alibaba.fastjson.JSON;
import com.schooltraining.storesdistribution.entities.Notification;
import com.schooltraining.storesdistribution.entities.Shop;
import com.schooltraining.storesdistribution.service.NotificationService;
import com.schooltraining.storesdistribution.service.ShopService;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.pojo.ParameterMap;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

@ServerEndpoint(prefix = "netty-websocket")
@Component
public class MyWebSocket {

    public static NotificationService notificationService;

    
    public static ShopService shopService;
    
    @Autowired
    NotificationService notificationService2;
    
    @Autowired
    ShopService shopService2;
    
    
    @PostConstruct
    public void beforeInit() {
    	notificationService = notificationService2;
    	shopService = shopService2;
    }
    

    private static final Logger logger = LoggerFactory.getLogger(MyWebSocket.class);

    /**
     * 用线程安全的CopyOnWriteArraySet来存放客户端连接的信息
     */
    private static CopyOnWriteArraySet<Client> socketServers = new CopyOnWriteArraySet<>();
//    private static Map<String, CopyOnWriteArraySet<Client>> socketMap = new HashMap<>();//分店管理

    /**
     * websocket封装的session,信息推送，就是通过它来信息推送
     */
    private Session session;

    @OnOpen//有客户端连接进来
    public void onOpen(Session session, HttpHeaders headers, ParameterMap parameterMap) throws IOException {
        String userId = parameterMap.getParameter("userId");
        String shopId = parameterMap.getParameter("shopId");
        String userName = parameterMap.getParameter("userName");//没有该参数会报空指针异常
        userName = URLDecoder.decode(userName, "UTF-8");//解决请求路径的中文乱码

        //添加client
        this.session = session;
        Client client = new Client(userId, shopId, userName, session);
        socketServers.add(client);
//        CopyOnWriteArraySet<Client> clients = socketMap.get(shopId);
//        if (clients == null) {
//            clients = new CopyOnWriteArraySet<>();
//        }
//        clients.add(client);
//        socketMap.put(shopId, clients);

        logger.info("客户端:【{}】连接成功", "userId : " + userId + ", userName: " + userName);

//        Map<String, Object> map = new HashMap<>();
//        map.put("socketSet", socketServers);

//        sendMessage("",SYS_USERNAME, 100);//可以用来发送上线的消息
    }

    @OnClose//客户端关闭连接时触发
    public void onClose(Session session) throws IOException {// session为当前触发操作的客户端
        socketServers.forEach(client -> {
            if (client.getSession().id().equals(session.id())) {

                logger.info("客户端:【{}】断开连接", "userId : " + client.getUserId() + ", userName: " + client.getUserName());
                socketServers.remove(client);//移出在线客户端集合

            }
        });
    }

    @OnError//session 发生错误时触发
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    /*
     * @OnMessage
     * 当接收到字符串消息时，对该方法进行回调 注入参数的类型:Session、String
     */
    @OnMessage//接收消息
    public void onMessage(Session session, String message) {
        Notification notification = JSON.parseObject(message, Notification.class);
        //创建时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        notification.setCreateTime(sdf.format(new Date()));
        //保存到数据库
        notification = notificationService.add(notification);
        System.out.println(notification);
        if(notification != null){//插入成功
            pushNews(notification);
        }

        //转发消息
//        MyWebSocket.sendAll(message);
    }

    //推送消息给分店店员，包括分店店长自己
    public synchronized static void pushNews(Notification notification){
        int notificationId = notification.getId();
        int shopId = notification.getShopId();
        //将消息与会员的关系保存到数据库
        //得到分店会员id
        List<Integer> userIds = shopService.getUserIds(shopId);
        int i = notificationService.keepWithMemberRelation(userIds, notificationId);
        if(i != 0){//插入成功
            List<Integer> readUsers = new ArrayList<>();
            //将消息推送给分店在线会员
            socketServers.forEach(client ->{
                if (Integer.parseInt(client.getShopId()) == shopId) {
                    String jsonStr = JSON.toJSONString(notification);
                    client.getSession().sendText(jsonStr);

                    readUsers.add(Integer.parseInt(client.getUserId()));
                }
            });

            //更新已读数据
            if(readUsers != null && readUsers.size() > 0){
                notificationService.updateStatusWithUser(readUsers, notificationId);
            }
        }
    }


    /**
     * 服务端的userName,因为用的是set，每个客户端的username必须不一样，否则会被覆盖。
     * 要想完成ui界面聊天的功能，服务端也需要作为客户端来接收后台推送用户发送的信息
     */
    private final static String SYS_USERNAME = "niezhiliang9595";

    /**
     * 信息发送的方法，通过客户端的userId
     * 拿到其对应的session，调用信息推送的方法
     */
    public synchronized static void sendMessage(String message, String userName, int typeCode) {//typeCode 100 代表onopen 200 代表onmessage

        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("num", getOnlineNum());
        map.put("typeCode", typeCode);

        String json = JSON.toJSONString(map);//转为json字符串，前台再转为json对象
        System.out.println(json);
        socketServers.forEach(client -> {
            if (userName.equals(client.getUserName())) {
                try {
                    client.getSession().sendText(json);

                    logger.info("服务端推送给客户端 :【{}】", client.getUserName(), message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取服务端当前客户端的连接数量，
     * 因为服务端本身也作为客户端接受信息，
     * 所以连接总数还要减去服务端
     * 本身的一个连接数
     * <p>
     * 这里运用三元运算符是因为客户端第一次在加载的时候
     * 客户端本身也没有进行连接，-1 就会出现总数为-1的情况，
     * 这里主要就是为了避免出现连接数为-1的情况
     *
     * @return
     */
    public synchronized static int getOnlineNum() {
        return socketServers.stream().filter(client -> !client.getUserName().equals(SYS_USERNAME))
                .collect(Collectors.toList()).size();
    }

    /**
     * 获取在线用户名，前端界面需要用到
     *
     * @return
     */
    public synchronized static List<String> getOnlineUsers() {

        List<String> onlineUsers = socketServers.stream()
                .filter(client -> !client.getUserName().equals(SYS_USERNAME))
                .map(client -> client.getUserName())
                .collect(Collectors.toList());

        return onlineUsers;
    }

    /**
     * 信息群发，我们要排除服务端自己不接收到推送信息
     * 所以我们在发送的时候将服务端排除掉
     *
     * @param message
     */
    public synchronized static void sendAll(String message) {
        //群发，不能发送给服务端自己
        socketServers.stream().filter(cli -> cli.getUserName() != SYS_USERNAME)
                .forEach(client -> {
                    try {
                        System.out.println("sendAll: " + client);
                        Map<String, Object> map = new HashMap<>();
                        map.put("message", message);
                        map.put("num", 0);
                        map.put("typeCode", 200);

                        String json = JSON.toJSONString(map);//转为json字符串，前台再转为json对象

                        client.getSession().sendText(json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        logger.info("服务端推送给所有客户端 :【{}】", message);
    }

    /**
     * 多个人发送给指定的几个用户
     *
     * @param message
     * @param persons
     */
    public synchronized static void SendMany(String message, String[] persons) {
        for (String userName : persons) {
            sendMessage(message, userName, 200);
        }
    }

}