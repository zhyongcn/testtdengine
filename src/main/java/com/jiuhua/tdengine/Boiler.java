package com.jiuhua.tdengine;

public class Boiler {

    private long timestamp;
    private int roomState;
    private boolean boilerstate;
    private int currentlyTemperature;
    private int settingTemperature;
    private String location;
    private int roomId;
    private int deviceType;
    private int deviceId;

    public Boiler(long timestamp, int roomState, boolean boilerstate,
                  int currentlyTemperature, int settingTemperature,
                  String location, int roomId, int deviceType, int deviceId) {
        this.timestamp = timestamp;
        this.roomState = roomState;
        this.boilerstate = boilerstate;
        this.currentlyTemperature = currentlyTemperature;
        this.settingTemperature = settingTemperature;
        this.location = location;
        this.roomId = roomId;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getRoomState() {
        return roomState;
    }

    public void setRoomState(int roomState) {
        this.roomState = roomState;
    }

    public boolean isBoilerstate() {
        return boilerstate;
    }

    public void setBoilerstate(boolean boilerstate) {
        this.boilerstate = boilerstate;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
}
