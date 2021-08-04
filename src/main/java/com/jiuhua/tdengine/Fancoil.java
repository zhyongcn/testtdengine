package com.jiuhua.tdengine;

public class Fancoil {

    private long timestamp;
    private int roomState;
    private int currentlyTemperature;
    private int currentlyHumidity;
    private int settingTemperature;
    private int settingHumidity;
    private int settingFanSpeed;
    private int currentFanSpeed;
    private boolean coilvalve;
    private String location;
    private int roomId;
    private int deviceType;
    private String deviceId;

    public Fancoil(long timestamp, int roomState, int currentlyTemperature,
                   int currentlyHumidity, int settingTemperature, int settingHumidity,
                   int settingFanSpeed, int currentFanSpeed, boolean coilvalve,
                   String location, int roomId, int deviceType, String deviceId) {
        this.timestamp = timestamp;
        this.roomState = roomState;
        this.currentlyTemperature = currentlyTemperature;
        this.currentlyHumidity = currentlyHumidity;
        this.settingTemperature = settingTemperature;
        this.settingHumidity = settingHumidity;
        this.settingFanSpeed = settingFanSpeed;
        this.currentFanSpeed = currentFanSpeed;
        this.coilvalve = coilvalve;
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

    public int getCurrentlyTemperature() {
        return currentlyTemperature;
    }

    public void setCurrentlyTemperature(int currentlyTemperature) {
        this.currentlyTemperature = currentlyTemperature;
    }

    public int getCurrentlyHumidity() {
        return currentlyHumidity;
    }

    public void setCurrentlyHumidity(int currentlyHumidity) {
        this.currentlyHumidity = currentlyHumidity;
    }

    public int getSettingTemperature() {
        return settingTemperature;
    }

    public void setSettingTemperature(int settingTemperature) {
        this.settingTemperature = settingTemperature;
    }

    public int getSettingHumidity() {
        return settingHumidity;
    }

    public void setSettingHumidity(int settingHumidity) {
        this.settingHumidity = settingHumidity;
    }

    public int getSettingFanSpeed() {
        return settingFanSpeed;
    }

    public void setSettingFanSpeed(int settingFanSpeed) {
        this.settingFanSpeed = settingFanSpeed;
    }

    public int getCurrentFanSpeed() {
        return currentFanSpeed;
    }

    public void setCurrentFanSpeed(int currentFanSpeed) {
        this.currentFanSpeed = currentFanSpeed;
    }

    public boolean isCoilvalve() {
        return coilvalve;
    }

    public void setCoilvalve(boolean coilvalve) {
        this.coilvalve = coilvalve;
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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
