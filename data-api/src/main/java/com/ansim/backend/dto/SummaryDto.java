package com.ansim.backend.dto;

public class SummaryDto {
    private long totalCount;
    private double avgDurationMin;

    public SummaryDto(long totalCount, double avgDurationMin) {
        this.totalCount = totalCount;
        this.avgDurationMin = avgDurationMin;
    }

    public long getTotalCount() { return totalCount; }
    public double getAvgDurationMin() { return avgDurationMin; }
}
