package com.ansim.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class EmergencyTriggerRequest {
    private Long memberId;
    private BigDecimal lat;
    private BigDecimal lng;
}
