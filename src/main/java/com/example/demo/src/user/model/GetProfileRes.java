package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetProfileRes {
    private String nickName;
    private int userIdx;
    private String userTemperature;
    private String desiredRate;
    private String responseRate;
    private String loginedAt;
    private String createdAt;
    private int badgeCnt;
    private int salesCnt;

}
