package com.ansim.backend.dto;

public class TopFriendDto {

    private Long memberId;
    private String name;
    private long count;

    public TopFriendDto(
            Long memberId,
            String name,
            long count
    ) {
        this.memberId = memberId;
        this.name = name;
        this.count = count;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public long getCount() {
        return count;
    }
}
