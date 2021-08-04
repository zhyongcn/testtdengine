package com.jiuhua.tdengine;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MyMqttClient {
    public static final String HOST = "tcp://175.24.33.56:1883";
//        public static final String SUB_TOPIC = "86518/#";
    public static final String SUB_TOPIC1 = "86518/yuxiuhuayuan/12-1-101/Room1";
    public static final String SUB_TOPIC2 = "86518/yuxiuhuayuan/12-1-101/Room2";
    public static final String SUB_TOPIC3 = "86518/yuxiuhuayuan/12-1-101/room3";//TODO 注意 R 的大小写
    public static final String SUB_TOPIC4 = "86518/yuxiuhuayuan/12-1-101/Room4";
    public static final String SUB_TOPIC5 = "86518/yuxiuhuayuan/12-1-101/Room5";
    public static final String SUB_TOPIC6 = "86518/yuxiuhuayuan/12-1-101/Room6";
    public static final String SUB_TOPIC7 = "86518/yuxiuhuayuan/12-1-101/Room7";
    public static final String SUB_TOPIC8 = "86518/yuxiuhuayuan/12-1-101/Room8";
    public static final String SUB_TOPIC9 = "86518/yuxiuhuayuan/12-1-101/Room9";
    public static final String SUB_TOPIC10 = "86518/yuxiuhuayuan/12-1-101/Room10";
    public static final String SUB_TOPIC11 = "86518/yuxiuhuayuan/12-1-101/Room11";
    public static final String SUB_TOPIC_YLHY = "86518/yulonghuayuan/c-82/room1";
    public static final String SUB_TOPIC_QXGY = "86518/qianxigongyu/2-4-102/room1";
    public static final String SUB_TOPIC_HYJD = "86518/HYJD/7-3-402/Room1";
    public static final String SUB_TOPIC_JHDCYH1 = "86518/JHDCYH/2-4-101/Room1";
    public static final String SUB_TOPIC_JHDCYH2 = "86518/JHDCYH/2-4-101/Room2";
    public static final String SUB_TOPIC_JHDCYH3 = "86518/JHDCYH/2-4-101/Room3";
    public static final String SUB_TOPIC_JHDCYH4 = "86518/JHDCYH/2-4-101/Room4";

    public static final String LASTWILL_TOPIC = "86518/lastwill";
    private static final String clientid = "client5ID";
    private MqttClient client;
    private MqttConnectOptions options;
    private String userName = "userName";
    private String passWord = "password";
    private ScheduledExecutorService scheduler;

    //重新链接
    public void startReconnect() {
        //单线程排定执行器？？
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!client.isConnected()) {
                    try {
                        client.connect(options);
                    } catch (MqttSecurityException e) {
                        e.printStackTrace();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);

    }

    private void start() {
        try {
            // host为主机名，test为 clientid 即连接MQTT的客户端ID，一般以客户端唯一标识符表示，
            // MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(HOST, clientid, new MemoryPersistence());
            // MQTT的连接设置
            options = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
            // 这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            // 设置连接的用户名
            options.setUserName(userName);
            // 设置连接的密码   //密码需要toCharArray()
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(60);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，
            // 但这个方法并没有重连的机制
            options.setKeepAliveInterval(300);
            // 设置回调
            client.setCallback(new MqttReceriveCallback());
            //MqttTopic 不是普通的String，需要过一下
            MqttTopic lastwillTopic = client.getTopic(LASTWILL_TOPIC);

            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
            //消息或者负载的字符串需要转换为bytes
            options.setWill(lastwillTopic, "86518#TDengine_Connector-close".getBytes(), 0, false);

            client.connect(options);
            //订阅消息
            int[] Qos = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
            //设置订阅的topic，TODO：将来按照城市来布置服务器
            String[] subTopics = {SUB_TOPIC1, SUB_TOPIC2, SUB_TOPIC3, SUB_TOPIC4, SUB_TOPIC5, SUB_TOPIC6, SUB_TOPIC7,
                    SUB_TOPIC8, SUB_TOPIC9, SUB_TOPIC10, SUB_TOPIC11, SUB_TOPIC_YLHY, SUB_TOPIC_QXGY, SUB_TOPIC_HYJD,
                    SUB_TOPIC_JHDCYH1, SUB_TOPIC_JHDCYH2, SUB_TOPIC_JHDCYH3, SUB_TOPIC_JHDCYH4};
            client.subscribe(subTopics, Qos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws MqttException {
        MyMqttClient client = new MyMqttClient();
        client.start();
    }


}