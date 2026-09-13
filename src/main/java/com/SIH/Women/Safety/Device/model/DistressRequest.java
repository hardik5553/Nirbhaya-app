package com.SIH.Women.Safety.Device.model;

public class DistressRequest {
    private String userId;
    private double latitude;
    private double longitude;
    private int heartRate;
    private boolean movementDetected;
    private double acceleration;
    private boolean fallDetected;
    private boolean shakeDetected;

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    
    public int getHeartRate() { return heartRate; }
    public void setHeartRate(int heartRate) { this.heartRate = heartRate; }
    
    public boolean isMovementDetected() { return movementDetected; }
    public void setMovementDetected(boolean movementDetected) { this.movementDetected = movementDetected; }
    
    public double getAcceleration() { return acceleration; }
    public void setAcceleration(double acceleration) { this.acceleration = acceleration; }
    
    public boolean isFallDetected() { return fallDetected; }
    public void setFallDetected(boolean fallDetected) { this.fallDetected = fallDetected; }
    
    public boolean isShakeDetected() { return shakeDetected; }
    public void setShakeDetected(boolean shakeDetected) { this.shakeDetected = shakeDetected; }
}