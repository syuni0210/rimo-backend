package com.ansim.backend.dto;

public class FriendListItemDto {

    private Long mmbrId;
    private String memberName;
    private String loginId;
    private boolean locationSharing;

    public FriendListItemDto(Long mmbrId, String memberName, String loginId, boolean locationSharing) {
        this.mmbrId = mmbrId;
        this.memberName = memberName;
        this.loginId = loginId;
        this.locationSharing = locationSharing;
    }

    public Long getMmbrId() {
        return mmbrId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getLoginId() {
        return loginId;
    }

    public boolean isLocationSharing() {
        return locationSharing;
    }
}
