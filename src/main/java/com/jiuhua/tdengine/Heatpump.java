package com.jiuhua.tdengine;

public class Heatpump {

    private long timestamp;
    private int roomId;
    private int deviceType;
    private String deviceId;
    private int roomState;
    private boolean heatpumpstate;
    private int currentlyTemperature;
    private int settingTemperature;
    private int currentlyHumidity;
    private int settingHumidity;
    private String location;

    public Heatpump() {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getRoomState() {
        return roomState;
    }

    public void setRoomState(int roomState) {
        this.roomState = roomState;
    }

    public boolean isHeatpumpstate() {
        return heatpumpstate;
    }

    public void setHeatpumpstate(boolean heatpumpstate) {
        this.heatpumpstate = heatpumpstate;
    }

    public int getCurrentlyTemperature() {
        return currentlyTemperature;
    }

    public void setCurrentlyTemperature(int currentlyTemperature) {
        this.currentlyTemperature = currentlyTemperature;
    }

    public int getSettingTemperature() {
        return settingTemperature;
    }

    public void setSettingTemperature(int settingTemperature) {
        this.settingTemperature = settingTemperature;
    }

    public int getCurrentlyHumidity() {
        return currentlyHumidity;
    }

    public void setCurrentlyHumidity(int currentlyHumidity) {
        this.currentlyHumidity = currentlyHumidity;
    }

    public int getSettingHumidity() {
        return settingHumidity;
    }

    public void setSettingHumidity(int settingHumidity) {
        this.settingHumidity = settingHumidity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
