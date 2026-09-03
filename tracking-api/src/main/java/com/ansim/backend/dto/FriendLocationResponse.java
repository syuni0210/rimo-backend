package com.ansim.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendLocationResponse {
    private boolean success;
    private double lat;
    private double lng;
    private String message;
}
