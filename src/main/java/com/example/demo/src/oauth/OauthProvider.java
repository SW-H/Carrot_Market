package com.example.demo.src.oauth;

import com.example.demo.config.BaseException;
import com.example.demo.src.oauth.model.GetTokenRes;
import com.example.demo.src.oauth.model.PostLoginRes;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class OauthProvider {
    private final OauthDao oauthDao;
    private final JwtService jwtService;

    @Autowired
    public OauthProvider(OauthDao oauthDao, JwtService jwtService) {
        this.oauthDao = oauthDao;
        this.jwtService = jwtService;
    }
    String getAccessToken(String accessCode) {
        String accessToken = "";
        String refreshToken = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();


            conn.setRequestMethod("GET");
            conn.setDoOutput(true); // POST요청을 위해서는 setDoOuput값을 true로 해야함.

            // POST 요청에 필수로 요구하는 데이터를 파라미트 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=0bf49fcf0dc962c414de1b15b7804ab3");
            sb.append("&redirect_uri=http://localhost:9000/oauth/kakao/login");
            sb.append("&code=" + accessCode);
            bw.write(sb.toString());
            bw.flush();

            // 결과 코드 출력(200=성공)
            int responseCode = conn.getResponseCode();

            // 요청을 통해 얻은 json 타입의 response 메시지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";
            while ((line = br.readLine()) != null) {
                result += line;
            }
            ObjectMapper om = new ObjectMapper();
            GetTokenRes tokenRes = om.readValue(result, GetTokenRes.class);
            accessToken = tokenRes.getAccess_token();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return accessToken;
    }

    public HashMap<String, Object> getUserInfo(String accessToken) throws BaseException {
        HashMap<String, Object> userInfo = new HashMap<>();
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        // responseBody에 있는 정보를 꺼냄
        JsonNode jsonNode;
        try {
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            jsonNode = objectMapper.readTree(responseBody);
        }catch(JsonProcessingException jsonProcessingException){
            throw new BaseException(FAILED_TO_KAKAOLOGIN);
        }
        try {
            Long id = jsonNode.get("id").asLong();
            userInfo.put("id", id);
        }catch (Exception exception){
            userInfo.put("id", null);
        }
        try {
            String nickname = jsonNode.get("properties").get("nickname").asText();
            userInfo.put("nickname", nickname);
        }catch (Exception exception){
            userInfo.put("nickname", null);
        }
        try {
            String email = jsonNode.get("kakao_account").get("email").asText();
            userInfo.put("email", email);
        }catch (Exception exception){
            userInfo.put("email", null);
        }
        return userInfo;
    }

    PostLoginRes checkKakaoId(long id) throws BaseException {
        try{
            int userIdx = oauthDao.getUserIdx(id);
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx,jwt);
        } catch(Exception exception) {
            throw new BaseException(FAILED_TO_KAKAOLOGIN);
        }
    }
}
