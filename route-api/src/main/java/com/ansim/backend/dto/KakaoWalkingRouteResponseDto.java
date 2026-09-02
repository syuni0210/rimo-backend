package com.ansim.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoWalkingRouteResponseDto {

    private WalkingRoute route;
    private String status;

    public WalkingRoute getRoute() {
        return route;
    }

    public void setRoute(WalkingRoute route) {
        this.route = route;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalkingRoute {

        private WalkingRouteProperties properties;
        private List<WalkingLeg> legs;

        public WalkingRouteProperties getProperties() {
            return properties;
        }

        public void setProperties(
                WalkingRouteProperties properties
        ) {
            this.properties = properties;
        }

        public List<WalkingLeg> getLegs() {
            return legs;
        }

        public void setLegs(
                List<WalkingLeg> legs
        ) {
            this.legs = legs;
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalkingRouteProperties {

        private Integer totalDistance;
        private Integer totalTime;
        private String landingUrl;

        public Integer getTotalDistance() {
            return totalDistance;
        }

        public void setTotalDistance(
                Integer totalDistance
        ) {
            this.totalDistance = totalDistance;
        }

        public Integer getTotalTime() {
            return totalTime;
        }

        public void setTotalTime(
                Integer totalTime
        ) {
            this.totalTime = totalTime;
        }

        public String getLandingUrl() {
            return landingUrl;
        }

        public void setLandingUrl(
                String landingUrl
        ) {
            this.landingUrl = landingUrl;
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalkingLeg {

        private WalkingLegProperties properties;
        private List<WalkingStep> steps;

        public WalkingLegProperties getProperties() {
            return properties;
        }

        public void setProperties(
                WalkingLegProperties properties
        ) {
            this.properties = properties;
        }

        public List<WalkingStep> getSteps() {
            return steps;
        }

        public void setSteps(
                List<WalkingStep> steps
        ) {
            this.steps = steps;
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalkingLegProperties {

        private Integer distance;
        private Integer time;

        public Integer getDistance() {
            return distance;
        }

        public void setDistance(
                Integer distance
        ) {
            this.distance = distance;
        }

        public Integer getTime() {
            return time;
        }

        public void setTime(
                Integer time
        ) {
            this.time = time;
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalkingStep {

        private WalkingStepProperties properties;
        private WalkingPath path;

        public WalkingStepProperties getProperties() {
            return properties;
        }

        public void setProperties(
                WalkingStepProperties properties
        ) {
            this.properties = properties;
        }

        public WalkingPath getPath() {
            return path;
        }

        public void setPath(
                WalkingPath path
        ) {
            this.path = path;
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalkingStepProperties {

        private Integer distance;
        private String guidance;
        private Integer time;
        private Double x;
        private Double y;

        public Integer getDistance() {
            return distance;
        }

        public void setDistance(
                Integer distance
        ) {
            this.distance = distance;
        }

        public String getGuidance() {
            return guidance;
        }

        public void setGuidance(
                String guidance
        ) {
            this.guidance = guidance;
        }

        public Integer getTime() {
            return time;
        }

        public void setTime(
                Integer time
        ) {
            this.time = time;
        }

        public Double getX() {
            return x;
        }

        public void setX(
                Double x
        ) {
            this.x = x;
        }

        public Double getY() {
            return y;
        }

        public void setY(
                Double y
        ) {
            this.y = y;
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalkingPath {

        private List<List<Double>> points;

        public List<List<Double>> getPoints() {
            return points;
        }

        public void setPoints(
                List<List<Double>> points
        ) {
            this.points = points;
        }
    }
}
