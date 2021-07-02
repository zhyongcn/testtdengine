package com.jiuhua.tdengine;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.sql.*;
import java.util.HashSet;

/**
 * TODO: Restful 方式成功了，先跑通，要效率使用 C 连接器？ 线程池等技术更好？？
 */
public class MqttReceriveCallback implements MqttCallback {
    private final HashSet<String> sensorsSet;
    private final HashSet<String> boilersSet;
    private final HashSet<String> fancoilsSet;

    public MqttReceriveCallback() {
        sensorsSet = new HashSet<String>();
        boilersSet = new HashSet<String>();
        fancoilsSet = new HashSet<String>();

        try {
            Class.forName("com.taosdata.jdbc.rs.RestfulDriver");
            String jdbcUrl = "jdbc:TAOS-RS://175.24.33.56:6041/homedevice?user=zz&password=700802";
            Connection conn = DriverManager.getConnection(jdbcUrl);
            Statement stmt = conn.createStatement();

            //提取现有数据表的表名
            String sql;
            ResultSet resultSet;
            sql = "select tbname from homedevice.sensors";
            resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String tablename = resultSet.getString(1);
                System.out.println("表名是： " + tablename + "");
                sensorsSet.add(tablename);
            }
            sql = "select tbname from homedevice.boilers";
            resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String tablename = resultSet.getString(1);
                System.out.println("表名是： " + tablename + "");
                boilersSet.add(tablename);
            }
            sql = "select tbname from homedevice.fancoils";
            resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String tablename = resultSet.getString(1);
                System.out.println("表名是： " + tablename + "");
                fancoilsSet.add(tablename);
            }

            stmt.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

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
        System.out.println("接收消息内容 : " + str);
        String sql = null;

        if (str.startsWith("{") && str.endsWith("}")) {
            //json字符串返回值反序列化为实体类
            //TODO： 依据 deviceType 分拣一下
            if (str.contains("deviceType\":5,") || str.contains("deviceType\":6,")) {
                Sensor sensor = JSONObject.parseObject(str, Sensor.class);
                String tablename = "sensor" + sensor.getDeviceId();
                if (sensorsSet.contains(tablename)) {
                    if (sensor.getTimestamp() < 1600000000) {
                        sql = "insert into " + tablename + " (ts, currenttemperature, currenthumidity, adjustingtemperature,"
                                +"adjustinghumidity) values(now, " + sensor.getCurrentlyTemperature()
                                + "," + sensor.getCurrentlyHumidity()
                                + "," + sensor.getAdjustingTemperature()
                                + "," + sensor.getAdjustingHumidity()
                                + ");";
                    } else {
                        sql = "insert into " + tablename + " (ts, currenttemperature, currenthumidity, adjustingtemperature,"
                                +"adjustinghumidity) values(" + sensor.getTimestamp()
                                //这里要加 000 TDengine的时间戳毫秒级的。
                                + "000," + sensor.getCurrentlyTemperature()
                                + "," + sensor.getCurrentlyHumidity()
                                + "," + sensor.getAdjustingTemperature()
                                + "," + sensor.getAdjustingHumidity()
                                + ");";
                    }
                } else {
                    sql = "create table " + tablename + " using homedevice.sensors tags(\""
                            + sensor.getLocation() + "\", "
                            + sensor.getRoomId() + ","
                            + sensor.getDeviceType() + ","
                            + sensor.getDeviceId() + ")";
                    sensorsSet.add(tablename);
                }
            }

        }

        //Class.forName("com.taosdata.jdbc.TSDBDriver");
        Class.forName("com.taosdata.jdbc.rs.RestfulDriver");
        //String jdbcUrl = "jdbc:TAOS://175.24.33.56:6030/homedevice?user=zz&password=700802";
        String jdbcUrl = "jdbc:TAOS-RS://175.24.33.56:6041/homedevice?user=zz&password=700802";
        Connection conn = DriverManager.getConnection(jdbcUrl);
        Statement stmt = conn.createStatement();

        if (!(sql == null)) {
            //执行SQL命令
            stmt.executeUpdate(sql);
//            int affectedRows = stmt.executeUpdate(sql);
//            System.out.println("insert " + affectedRows + " rows");
        }

        stmt.close();
        conn.close();

    }

}
