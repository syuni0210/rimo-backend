package com.ansim.backend.dto;

public class RoutePreferenceDto {
    private String code;
    private String label;
    private long count;
    private double percent;

    public RoutePreferenceDto(String code, String label, long count, double percent) {
        this.code = code;
        this.label = label;
        this.count = count;
        this.percent = percent;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
    public long getCount() { return count; }
    public double getPercent() { return percent; }
}
