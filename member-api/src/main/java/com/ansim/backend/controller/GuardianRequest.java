package com.ansim.backend.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuardianRequest {
    private Long memberId;
    private String guardianName;
    private String phoneNumber;
    private String relationName;
}
