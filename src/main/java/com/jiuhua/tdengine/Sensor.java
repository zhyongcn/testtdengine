package com.jiuhua.tdengine;

public class Sensor {

    private long timestamp;
    private int currentlyTemperature;
    private int currentlyHumidity;
    private int adjustingTemperature;
    private int adjustingHumidity;
    private String location;
    private int roomId;
    private int deviceType;
    private String deviceId;

    public Sensor(long timestamp, int currentlyTemperature, int currentlyHumidity,
                  int adjustingTemperature, int adjustingHumidity, String location,
                  int roomId, int deviceType, String deviceId) {
        this.timestamp = timestamp;
        this.currentlyTemperature = currentlyTemperature;
        this.currentlyHumidity = currentlyHumidity;
        this.adjustingTemperature = adjustingTemperature;
        this.adjustingHumidity = adjustingHumidity;
        this.location = location;
        this.roomId = roomId;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "timestamp=" + timestamp +
                ", currentTemperature=" + currentlyTemperature +
                ", currentHumidity=" + currentlyHumidity +
                ", adjustingTemperature=" + adjustingTemperature +
                ", adjustingHumidity=" + adjustingHumidity +
                ", location='" + location + '\'' +
                ", roomId=" + roomId +
                ", devicetype=" + deviceType +
                ", deviceid=" + deviceId +
                '}';
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public int getAdjustingTemperature() {
        return adjustingTemperature;
    }

    public void setAdjustingTemperature(int adjustingTemperature) {
        this.adjustingTemperature = adjustingTemperature;
    }

    public int getAdjustingHumidity() {
        return adjustingHumidity;
    }

    public void setAdjustingHumidity(int adjustingHumidity) {
        this.adjustingHumidity = adjustingHumidity;
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
