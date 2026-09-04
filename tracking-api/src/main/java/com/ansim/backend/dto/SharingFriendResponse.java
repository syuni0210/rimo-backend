package com.ansim.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SharingFriendResponse {
    private Long friendId;
    private String friendName;
    private double lat;
    private double lng;
}
