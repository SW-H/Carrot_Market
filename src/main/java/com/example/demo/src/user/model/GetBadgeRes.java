package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class GetBadgeRes {
    private int userBadgeIdx;
    private Timestamp achivedAt;
    private boolean isRepresented;
    private String iconPath;
    private String badgeName;
    private String badgeDescription;
}
