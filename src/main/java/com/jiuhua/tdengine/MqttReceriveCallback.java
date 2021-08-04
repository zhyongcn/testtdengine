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
    private final HashSet<String> heatpumpsSet;
    private final HashSet<String> watershedsSet;

    public MqttReceriveCallback() {
        sensorsSet = new HashSet<>();
        boilersSet = new HashSet<>();
        fancoilsSet = new HashSet<>();
        heatpumpsSet = new HashSet<>();
        watershedsSet = new HashSet<>();

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

            sql = "select tbname from homedevice.heatpumps";
            resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String tablename = resultSet.getString(1);
                System.out.println("表名是： " + tablename + "");
                heatpumpsSet.add(tablename);
            }

            sql = "select tbname from homedevice.watersheds";
            resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String tablename = resultSet.getString(1);
                System.out.println("表名是： " + tablename + "");
                watershedsSet.add(tablename);
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
            //deviceType 5 || 6 传感器的消息
            if (str.contains("deviceType\":5,") || str.contains("deviceType\":6,")) {
                //json字符串返回值反序列化为实体类
                Sensor sensor = JSONObject.parseObject(str, Sensor.class);
                String tablename = "sensor" + sensor.getDeviceId();
                if (sensorsSet.contains(tablename)) {
                    if (sensor.getTimestamp() < 1600000000) {
                        sql = "insert into " + tablename + " (ts, currenttemperature, currenthumidity, adjustingtemperature,"
                                + "adjustinghumidity) values(now, " + sensor.getCurrentlyTemperature()
                                + "," + sensor.getCurrentlyHumidity()
                                + "," + sensor.getAdjustingTemperature()
                                + "," + sensor.getAdjustingHumidity()
                                + ");";
                    } else {
                        sql = "insert into " + tablename + " (ts, currenttemperature, currenthumidity, adjustingtemperature,"
                                + "adjustinghumidity) values(" + sensor.getTimestamp()
                                //这里要加 000 TDengine的时间戳毫秒级的。
                                + "000," + sensor.getCurrentlyTemperature()
                                + "," + sensor.getCurrentlyHumidity()
                                + "," + sensor.getAdjustingTemperature()
                                + "," + sensor.getAdjustingHumidity()
                                + ");";
                    }
                } else {
                    sensor.setLocation(topic);
                    sql = "create table " + tablename + " using homedevice.sensors tags(\""
                            + sensor.getLocation() + "\", "
                            + sensor.getRoomId() + ","
                            + sensor.getDeviceType() + ","
                            + sensor.getDeviceId() + ")";
                    sensorsSet.add(tablename);
                }
            }

            //deviceType 3  锅炉的消息
            if (str.contains("deviceType\":3,")) {
                //json字符串返回值反序列化为实体类
                Boiler boiler = JSONObject.parseObject(str, Boiler.class);
                String tablename = "boiler" + boiler.getDeviceId();
                if (boilersSet.contains(tablename)) {
                    if (boiler.getTimestamp() < 1600000000) {
                        sql = "insert into " + tablename + " (ts, currenttemperature, settingtemperature, boilerstate,"
                                + "roomstate) values(now, " + boiler.getCurrentlyTemperature()
                                + "," + boiler.getSettingTemperature()
                                + "," + boiler.isBoilerstate()
                                + "," + boiler.getRoomState()
                                + ");";
                    } else {
                        sql = "insert into " + tablename + " (ts, currenttemperature, settingtemperature, boilerstate,"
                                + "roomstate) values(" + boiler.getTimestamp()
                                //这里要加 000 TDengine的时间戳毫秒级的。
                                + "000," + boiler.getCurrentlyTemperature()
                                + "," + boiler.getSettingTemperature()
                                + "," + boiler.isBoilerstate()
                                + "," + boiler.getRoomState()
                                + ");";
                    }
                } else {
                    boiler.setLocation(topic);
                    sql = "create table " + tablename + " using homedevice.boilers tags(\""
                            + boiler.getLocation() + "\", "
                            + boiler.getRoomId() + ","
                            + boiler.getDeviceType() + ","
                            + boiler.getDeviceId() + ")";
                    boilersSet.add(tablename);
                }
            }

            //deviceType 0  风机盘管的消息
            if (str.contains("deviceType\":0,")) {
                //json字符串返回值反序列化为实体类
                Fancoil fancoil = JSONObject.parseObject(str, Fancoil.class);
                String tablename = "fancoil" + fancoil.getDeviceId();
                if (fancoilsSet.contains(tablename)) {
                    if (fancoil.getTimestamp() < 1600000000) {
                        sql = "insert into " + tablename + " (ts, roomstate, settingtemperature, settinghumidity,"
                                + "currenttemperature, currenthumidity, settingfanspeed, currentfanspeed,"
                                + " coilvalve) values(now, " + fancoil.getRoomState()
                                + "," + fancoil.getSettingTemperature()
                                + "," + fancoil.getSettingHumidity()
                                + "," + fancoil.getCurrentlyTemperature()
                                + "," + fancoil.getCurrentlyHumidity()
                                + "," + fancoil.getSettingFanSpeed()
                                + "," + fancoil.getCurrentFanSpeed()
                                + "," + fancoil.isCoilvalve()
                                + ");";
                    } else {
                        sql = "insert into " + tablename + " (ts, roomstate, settingtemperature, settinghumidity,"
                                + " currenttemperature, currenthumidity, settingfanspeed, currentfanspeed,"
                                + " coilvalve) values(" + fancoil.getTimestamp()
                                //这里要加 000 TDengine的时间戳毫秒级的。
                                + "000," + fancoil.getRoomState()
                                + "," + fancoil.getSettingTemperature()
                                + "," + fancoil.getSettingHumidity()
                                + "," + fancoil.getCurrentlyTemperature()
                                + "," + fancoil.getCurrentlyHumidity()
                                + "," + fancoil.getSettingFanSpeed()
                                + "," + fancoil.getCurrentFanSpeed()
                                + "," + fancoil.isCoilvalve()
                                + ");";
                    }
                } else {
                    fancoil.setLocation(topic);
                    sql = "create table " + tablename + " using homedevice.fancoils tags(\""
                            + fancoil.getLocation() + "\","
                            + fancoil.getRoomId() + ","
                            + fancoil.getDeviceType() + ","
                            + fancoil.getDeviceId() + ")";
                    fancoilsSet.add(tablename);
                }
            }

            //Heatpump  devicetype 4 热泵主机的消息
            if (str.contains("deviceType\":4,")) {
                //json字符串返回值反序列化为实体类
                Heatpump heatpump = JSONObject.parseObject(str, Heatpump.class);
                String tablename = "heatpump" + heatpump.getDeviceId();
                if (heatpumpsSet.contains(tablename)) {
                    if (heatpump.getTimestamp() < 1600000000) {
                        sql = "insert into " + tablename + " (ts, currenttemperature, settingtemperature, currenthumidity,"
                                + " settinghumidity, heatpumpstate, roomstate) values(now, "
                                + heatpump.getCurrentlyTemperature()
                                + "," + heatpump.getSettingTemperature()
                                + "," + heatpump.getCurrentlyHumidity()
                                + "," + heatpump.getSettingHumidity()
                                + "," + heatpump.isHeatpumpstate()
                                + "," + heatpump.getRoomState()
                                + ");";
                    } else {
                        sql = "insert into " + tablename + " (ts, currenttemperature, settingtemperature, currenthumidity,"
                                + " settinghumidity, heatpumpstate, roomstate) values(" + heatpump.getTimestamp()
                                //这里要加 000 TDengine的时间戳毫秒级的。
                                + "000," + heatpump.getCurrentlyTemperature()
                                + "," + heatpump.getSettingTemperature()
                                + "," + heatpump.getCurrentlyHumidity()
                                + "," + heatpump.getSettingHumidity()
                                + "," + heatpump.isHeatpumpstate()
                                + "," + heatpump.getRoomState()
                                + ");";
                    }
                } else {
                    heatpump.setLocation(topic);
                    sql = "create table " + tablename + " using homedevice.heatpumps tags(\""
                            + heatpump.getLocation() + "\", "
                            + heatpump.getRoomId() + ","
                            + heatpump.getDeviceType() + ","
                            + heatpump.getDeviceId() + ")";
                    heatpumpsSet.add(tablename);
                }
            }

            //Watershed  deviceType 1  模块现在是floorheat，指地暖的分水器，应该空调的分水器也适用。
            if (str.contains("deviceType\":1,")) {
                //json字符串返回值反序列化为实体类
                Watershed watershed = JSONObject.parseObject(str, Watershed.class);
                String tablename = "watershed" + watershed.getDeviceId();
                if (watershedsSet.contains(tablename)) {
                    if (watershed.getTimestamp() < 1600000000) {
                        sql = "insert into " + tablename + " (ts, roomid, roomstate, floorvalve, coilvalve,"
                                + "currenttemperature, currenthumidity,  settingtemperature, settinghumidity) values(now, "
                                + watershed.getRoomId()
                                + "," + watershed.getRoomState()
                                + "," + watershed.isFloorvalve()
                                + "," + watershed.isCoilvalve()
                                + "," + watershed.getCurrentlyTemperature()
                                + "," + watershed.getCurrentlyHumidity()
                                + "," + watershed.getSettingTemperature()
                                + "," + watershed.getSettingHumidity()
                                + ");";
                    } else {
                        sql = "insert into " + tablename + " (ts, roomid, roomstate, floorvalve, coilvalve,"
                                + "currenttemperature, currenthumidity,  settingtemperature, settinghumidity) values("
                                + watershed.getTimestamp()
                                //这里要加 000 TDengine的时间戳毫秒级的。
                                + "000," + watershed.getRoomId()
                                + "," + watershed.getRoomState()
                                + "," + watershed.isFloorvalve()
                                + "," + watershed.isCoilvalve()
                                + "," + watershed.getCurrentlyTemperature()
                                + "," + watershed.getCurrentlyHumidity()
                                + "," + watershed.getSettingTemperature()
                                + "," + watershed.getSettingHumidity()
                                + ");";
                    }
                } else {
                    watershed.setLocation(topic);
                    sql = "create table " + tablename + " using homedevice.watersheds tags(\""
                            + watershed.getLocation() + "\","
                            + watershed.getDeviceType() + ","
                            + watershed.getDeviceId() + ")";
                    watershedsSet.add(tablename);
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
            try {//数据不一定合规矩，try保护一下，偷懒 :)  it work!
                //执行SQL命令
                stmt.executeUpdate(sql);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
//            int affectedRows = stmt.executeUpdate(sql);
//            System.out.println("insert " + affectedRows + " rows");
            System.out.println("access databases");
        }

        stmt.close();
        conn.close();

    }

}
