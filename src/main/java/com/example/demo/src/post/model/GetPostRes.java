package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetPostRes {
    private int postIdx;
    private String title;
    private int attention;
    private String price;
    private String townName;
    private int status;
    private boolean isRenewed;
    private String displayTime;
    private int chatCnt;
    private String imagePath;
}
