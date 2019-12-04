package com.schooltraining.storesdistribution.newspush;

//@ServerEndpoint(prefix = "netty-websocket")
//@Component
public class MyWebSocket {

//    private static final Logger logger = LoggerFactory.getLogger(MyWebSocket.class);
//
//    /**
//     * 用线程安全的CopyOnWriteArraySet来存放客户端连接的信息
//     */
//    private static CopyOnWriteArraySet<Client> socketServers = new CopyOnWriteArraySet<>();
//
//    /**
//     * websocket封装的session,信息推送，就是通过它来信息推送
//     */
//    private Session session;
//
//    /**
//     * 服务端的userName,因为用的是set，每个客户端的username必须不一样，否则会被覆盖。
//     * 要想完成ui界面聊天的功能，服务端也需要作为客户端来接收后台推送用户发送的信息
//     */
//    private final static String SYS_USERNAME = "niezhiliang9595";
//
//    @OnOpen
//    public void onOpen(Session session, HttpHeaders headers, ParameterMap parameterMap) throws IOException {
//        System.out.println("new connection");
//
//        String paramValue = parameterMap.getParameter("name");
//        paramValue = URLDecoder.decode(paramValue, "UTF-8");//解决请求路径的中文乱码
//        System.out.println(paramValue);
//
//        //添加client
//        this.session = session;
//        socketServers.add(new Client(paramValue, session));
//
//        logger.info("客户端:【{}】连接成功", paramValue);
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("socketSet", socketServers);
//        sendMessage("",SYS_USERNAME, 100);
//    }
//
//    @OnClose
//    public void onClose(Session session) throws IOException {
//        System.out.println("one connection closed");
//
//        socketServers.forEach(client -> {
//            if (client.getSession().id().equals(session.id())) {
//
//                logger.info("客户端:【{}】断开连接", client.getUserName());
//                socketServers.remove(client);
//
//            }
//        });
//    }
//
//    @OnError
//    public void onError(Session session, Throwable throwable) {
//        throwable.printStackTrace();
//    }
//
//    /*
//     * @OnMessage
//     * 当接收到字符串消息时，对该方法进行回调 注入参数的类型:Session、String
//     */
//    @OnMessage
//    public void onMessage(Session session, String message) {
//        System.out.println("onMessage coming ...");
//        System.out.println(message);
////        session.sendText("Hello Netty!");
//        MyWebSocket.sendAll(message);
//    }
//
//    /*
//     * @OnBinary
//     * 当接收到二进制消息时，对该方法进行回调 注入参数的类型:Session、byte[]
//     */
//    @OnBinary
//    public void onBinary(Session session, byte[] bytes) {
//        for (byte b : bytes) {
//            System.out.println(b);
//        }
//        session.sendBinary(bytes);
//    }
//
//    /*
//     * @OnEvent
//     * 当接收到Netty的事件时，对该方法进行回调 注入参数的类型:Session、Object
//     */
//    @OnEvent
//    public void onEvent(Session session, Object evt) {
//        if (evt instanceof IdleStateEvent) {
//            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
//            switch (idleStateEvent.state()) {
//                case READER_IDLE:
//                    System.out.println("read idle");
//                    break;
//                case WRITER_IDLE:
//                    System.out.println("write idle");
//                    break;
//                case ALL_IDLE:
//                    System.out.println("all idle");
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//    /**
//     * 信息发送的方法，通过客户端的userName
//     * 拿到其对应的session，调用信息推送的方法
//     *
//     * @param message
//     * @param userName
//     */
//    //sendObject 需要将对象转换为json 对象
//    public synchronized static void sendMessage(String message, String userName, int typeCode) {//typeCode 100 代表onopen 200 代表onmessage
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("message", message);
//        map.put("num", getOnlineNum());
//        map.put("typeCode", typeCode);
//
//        String json = JSON.toJSONString(map);//转为json字符串，前台再转为json对象
//        System.out.println(json);
//        socketServers.forEach(client -> {
//            if (userName.equals(client.getUserName())) {
//                try {
//                    client.getSession().sendText(json);
//
//                    logger.info("服务端推送给客户端 :【{}】", client.getUserName(), message);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    /**
//     * 获取服务端当前客户端的连接数量，
//     * 因为服务端本身也作为客户端接受信息，
//     * 所以连接总数还要减去服务端
//     * 本身的一个连接数
//     * <p>
//     * 这里运用三元运算符是因为客户端第一次在加载的时候
//     * 客户端本身也没有进行连接，-1 就会出现总数为-1的情况，
//     * 这里主要就是为了避免出现连接数为-1的情况
//     *
//     * @return
//     */
//    public synchronized static int getOnlineNum() {
//        return socketServers.stream().filter(client -> !client.getUserName().equals(SYS_USERNAME))
//                .collect(Collectors.toList()).size();
//    }
//
//    /**
//     * 获取在线用户名，前端界面需要用到
//     *
//     * @return
//     */
//    public synchronized static List<String> getOnlineUsers() {
//
//        List<String> onlineUsers = socketServers.stream()
//                .filter(client -> !client.getUserName().equals(SYS_USERNAME))
//                .map(client -> client.getUserName())
//                .collect(Collectors.toList());
//
//        return onlineUsers;
//    }
//
//    /**
//     * 信息群发，我们要排除服务端自己不接收到推送信息
//     * 所以我们在发送的时候将服务端排除掉
//     *
//     * @param message
//     */
//    public synchronized static void sendAll(String message) {
//        //群发，不能发送给服务端自己
//        socketServers.stream().filter(cli -> cli.getUserName() != SYS_USERNAME)
//                .forEach(client -> {
//                    try {
//                        System.out.println("sendAll: " + client);
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("message", message);
//                        map.put("num", 0);
//                        map.put("typeCode", 200);
//
//                        String json = JSON.toJSONString(map);//转为json字符串，前台再转为json对象
//
//                        client.getSession().sendText(json);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
//
//        logger.info("服务端推送给所有客户端 :【{}】", message);
//    }
//
//    /**
//     * 多个人发送给指定的几个用户
//     *
//     * @param message
//     * @param persons
//     */
//    public synchronized static void SendMany(String message, String[] persons) {
//        for (String userName : persons) {
//            sendMessage(message, userName, 200);
//        }
//    }

}