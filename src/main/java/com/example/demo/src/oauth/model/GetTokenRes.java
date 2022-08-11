package com.example.demo.src.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetTokenRes {
    public GetTokenRes(){}
    String access_token;
    String refresh_token;
    String token_type;
    String expires_in;
    String scope;
    int refresh_token_expires_in;
}
