package com.SIH.Women.Safety.Device.model;

import java.util.List;

public class RouteRequest {
    private String userId;
    private List<RoutePoint> plannedRoute;
    private List<RoutePoint> alternativeRoute;
    private double currentLatitude;
    private double currentLongitude;

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public List<RoutePoint> getPlannedRoute() { return plannedRoute; }
    public void setPlannedRoute(List<RoutePoint> plannedRoute) { this.plannedRoute = plannedRoute; }
    
    public List<RoutePoint> getAlternativeRoute() { return alternativeRoute; }
    public void setAlternativeRoute(List<RoutePoint> alternativeRoute) { this.alternativeRoute = alternativeRoute; }
    
    public double getCurrentLatitude() { return currentLatitude; }
    public void setCurrentLatitude(double currentLatitude) { this.currentLatitude = currentLatitude; }
    
    public double getCurrentLongitude() { return currentLongitude; }
    public void setCurrentLongitude(double currentLongitude) { this.currentLongitude = currentLongitude; }
}