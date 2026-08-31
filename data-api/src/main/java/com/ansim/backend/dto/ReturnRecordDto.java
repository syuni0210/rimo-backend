package com.ansim.backend.dto;

public class ReturnRecordDto {
    private String date;
    private String startLocation;
    private String destination;
    private String startTime;
    private String arrivalTime;
    private String duration;
    private String routeType;
    private String distance;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStartLocation() { return startLocation; }
    public void setStartLocation(String startLocation) { this.startLocation = startLocation; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getRouteType() { return routeType; }
    public void setRouteType(String routeType) { this.routeType = routeType; }
    public String getDistance() { return distance; }
    public void setDistance(String distance) { this.distance = distance; }
}
