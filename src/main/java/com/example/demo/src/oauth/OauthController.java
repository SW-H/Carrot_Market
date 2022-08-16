package com.example.demo.src.oauth;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.oauth.model.PostLoginRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

// http://kauth.kakao.com/oauth/authorize?client_id={REST_API_키}&redirect_uri={redirect_uri}&response_type=code
@RestController
@RequestMapping("/oauth")
public class OauthController {
    @Autowired
    private final OauthProvider oauthService;
    @Autowired
    private final OauthProvider oauthProvider;
    @Autowired
    private final JwtService jwtService;

    public OauthController(OauthProvider oauthService, OauthProvider oauthProvider, JwtService jwtService) {
        this.oauthService = oauthService;
        this.oauthProvider = oauthProvider;
        this.jwtService = jwtService;
    }

    /**
     * 카카오 callback(access code->Access Token 요청)
     * [Post] /oauth/kakao/login
     */
    @ResponseBody
    @GetMapping("/kakao/login")
    public BaseResponse<PostLoginRes> kakaoLogin(@RequestParam String code) {
        try {
            String accessToken = oauthProvider.getAccessToken(code);
            HashMap<String, Object> userInfo = oauthProvider.getUserInfo(accessToken);
            PostLoginRes postLoginRes = oauthProvider.checkKakaoId((long)userInfo.get("id"));
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
