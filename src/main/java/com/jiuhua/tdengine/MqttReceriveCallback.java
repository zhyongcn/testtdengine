package com.jiuhua.tdengine;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * TODO: Restful 方式成功了，先跑通，要效率使用 C 连接器？ 线程池等技术更好？？
 *
 * @author zz
 */
public class MqttReceriveCallback implements MqttCallback {

    @Override
    public void connectionLost(Throwable cause) {
        // 连接丢失后，一般在这里面进行重连
        System.out.println("连接断开，正在尝试做重连.......");
        //为什么不使用原来的客户端？？
        MyMqttClient client = new MyMqttClient();
        client.startReconnect();
        //一定成功了吗，不需要判断一下？
        System.out.println("重连成功..........");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {//这个负载到底是发送还是接收的？？多数应该是发送的。
        System.out.println("deliveryComplete---------" + token.isComplete());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("接收消息主题 : " + topic);
        System.out.println("接收消息Qos : " + message.getQos());
        //获取负载，再转为字符串
        String str = new String(message.getPayload());
//        System.out.println("接收消息内容 : " + new String(message.getPayload()));
        System.out.println("接收消息内容 : " + str);

        if (str.startsWith("{") && str.endsWith("}")) {
            //json字符串返回值反序列化为实体类
            Sensor sensor = JSONObject.parseObject(new String(message.getPayload()), Sensor.class);

            //Class.forName("com.taosdata.jdbc.TSDBDriver");
            Class.forName("com.taosdata.jdbc.rs.RestfulDriver");
            //String jdbcUrl = "jdbc:TAOS://175.24.33.56:6030/homedevice?user=zz&password=700802";
            String jdbcUrl = "jdbc:TAOS-RS://175.24.33.56:6041/homedevice?user=zz&password=700802";
            Connection conn = DriverManager.getConnection(jdbcUrl);
            Statement stmt = conn.createStatement();

            //插入一行数据
            //int affectedRows = stmt.executeUpdate("insert into sensor0001 values(now, 500,800,0,0)");
            String sql = "insert into sensor0001 (ts, currenttemperature, currenthumidity, adjustingtemperature," +
                    "adjustinghumidity) values(now, " + sensor.getCurrentlyTemperature()
                    + "," + sensor.getCurrentlyHumidity()
                    + "," + sensor.getAdjustingTemperature()
                    + "," + sensor.getAdjustingHumidity()
                    + ");";
            int affectedRows = stmt.executeUpdate(sql);
            System.out.println("insert " + affectedRows + " rows");

            stmt.close();
            conn.close();
        }


    }

}
