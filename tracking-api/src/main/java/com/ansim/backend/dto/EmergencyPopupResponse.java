package com.ansim.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyPopupResponse {

    private boolean hasEmergency;

    private Long emergencyId;

    private Long senderId;

    private String senderName;
}