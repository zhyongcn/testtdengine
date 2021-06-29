package com.jiuhua.tdengine;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MyMqttClient {
    public static final String HOST = "tcp://175.24.33.56:1883";
    public static final String TOPIC = "86518/YXHY/12-1-101/phone";
    private static final String clientid = "client3ID";
    private MqttClient client;
    private MqttConnectOptions options;
    private String userName = "userName";
    private String passWord = "password";
    private ScheduledExecutorService scheduler;

    //重新链接
    public void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();//单线程排定执行器？？
        scheduler.scheduleAtFixedRate(new Runnable() {//新的线程
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
            // 设置连接的密码
            options.setPassword(passWord.toCharArray());//密码需要toCharArray()
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(60);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，
            // 但这个方法并没有重连的机制
            options.setKeepAliveInterval(300);
            // 设置回调
            client.setCallback(new MqttReceriveCallback());
            MqttTopic topic = client.getTopic(TOPIC);//回调的时候从客户端获取topic，可以避免错误？？

            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
            //消息或者负载的字符串需要转换为bytes
            options.setWill(topic, "close".getBytes(), 0, false);

            client.connect(options);
            //订阅消息
            int[] Qos = {1};
            String[] topic1 = {TOPIC};
            client.subscribe(topic1, Qos);
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