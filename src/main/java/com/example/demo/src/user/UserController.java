package com.example.demo.src.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 회원 조회 API
     * [GET] /users
     * 닉네임으로 회원 검색 API
     * [GET] /users? nickName=
     *
     * @return BaseResponse<List < GetUserRes>>
     */
    //Query String
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/users
    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String nickName) {
        try {
            if (nickName == null) {
                List<GetUserRes> getUsersRes = userProvider.getUsers();
                return new BaseResponse<>(getUsersRes);
            }
            if(nickName.length() > 12){
                throw new BaseException(USERS_EMPTY_USER_NICKNAME);
            }
            List<GetUserRes> getUsersRes = userProvider.getUsersByNickname(nickName);
            return new BaseResponse<>(getUsersRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저코드로 회원 검색 API
     * [GET] /users/{userCode}
     *
     * @return BaseResponse<List < GetUserRes>>
     */
    //Query String
    @ResponseBody
    @GetMapping("/{userCode}")
    public BaseResponse<List<GetUserRes>> getUsersByCode(@PathVariable("userCode") int userCode) {
        try {
            List<GetUserRes> getUsersRes = userProvider.getUsersByCode(userCode);
            return new BaseResponse<>(getUsersRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원 프로필 조회 API
     * [GET] /users/profiles/:userIdx
     *
     * @return BaseResponse<GetProfileRes>
     */
    @ResponseBody
    @GetMapping("/profiles/{userIdx}") // (GET) 127.0.0.1:9000/users/profiles/:userIdx
    public BaseResponse<GetProfileRes> getProfiles(@PathVariable("userIdx") int userIdx) {
        try{
            if(userIdx < 0){
                throw new BaseException(REQUEST_ERROR);
            }
            GetProfileRes getProfileRes = userProvider.getProfiles(userIdx);
            return new BaseResponse<>(getProfileRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }
    /**
     * 닉네임 변경 불가 기간 조회
     * [GET] /users/nickname-duration/:userIdx
     *
     * @return BaseResponse<GetProfileRes>
     */
    @ResponseBody
    @GetMapping("/nickname-duration/{userIdx}") // (GET) 127.0.0.1:9000/users/profiles/:userIdx
    public BaseResponse<GetDurationRes> getNickNameDuration(@PathVariable("userIdx") int userIdx) {
        try{
            if(userIdx < 0){
                throw new BaseException(REQUEST_ERROR);
            }
            GetDurationRes getDurationRes = userProvider.getNickNameDuration(userIdx);
            return new BaseResponse<>(getDurationRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 최근 30일 이내 인증 동네 및 횟수 조회
     * [GET] /users/certificactions/:userIdx
     *
     * @return BaseResponse<GetCertsRes>
     */
    @ResponseBody
    @GetMapping("/certifications/{userIdx}")
    public BaseResponse<List<GetCertsRes>> getCertifications(@PathVariable("userIdx") int userIdx) {
        try{
            if(userIdx < 0){
                throw new BaseException(REQUEST_ERROR);
            }
            List<GetCertsRes> getCertsRes = userProvider.getCertifications(userIdx);
            return new BaseResponse<>(getCertsRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 획득한 활동 배지 조회
     * [GET] /users/badge/:userIdx
     *
     * @return BaseResponse<GetBadgeRes>
     */
    @ResponseBody
    @GetMapping("/badge/{userIdx}")
    public BaseResponse<List<GetBadgeRes>> GetBadge(@PathVariable("userIdx") int userIdx) {
        try{
            if(userIdx < 0){
                throw new BaseException(REQUEST_ERROR);
            }
            List<GetBadgeRes> getBadgeRes = userProvider.GetBadge(userIdx);
            return new BaseResponse<>(getBadgeRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}



//    // Path-variable
//    @ResponseBody
//    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/app/users/:userIdx
//    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx) {
//        // Get Users
//        try{
//            GetUserRes getUserRes = userProvider.getUser(userIdx);
//            return new BaseResponse<>(getUserRes);
//        } catch(BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//
//    }
//
//    /**
//     * 회원가입 API
//     * [POST] /users
//     * @return BaseResponse<UserUserRes>
//     */
//    // Body
//    @ResponseBody
//    @UserMapping("")
//    public BaseResponse<UserUserRes> createUser(@RequestBody UserUserReq userUserReq) {
//        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
//        if(userUserReq.getEmail() == null){
//            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
//        }
//        //이메일 정규표현
//        if(!isRegexEmail(userUserReq.getEmail())){
//            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
//        }
//        try{
//            UserUserRes userUserRes = userService.createUser(userUserReq);
//            return new BaseResponse<>(userUserRes);
//        } catch(BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
//    /**
//     * 로그인 API
//     * [POST] /users/logIn
//     * @return BaseResponse<UserLoginRes>
//     */
//    @ResponseBody
//    @UserMapping("/logIn")
//    public BaseResponse<UserLoginRes> logIn(@RequestBody UserLoginReq userLoginReq){
//        try{
//            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
//            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
//            UserLoginRes userLoginRes = userProvider.logIn(userLoginReq);
//            return new BaseResponse<>(userLoginRes);
//        } catch (BaseException exception){
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }
//
//    /**
//     * 유저정보변경 API
//     * [PATCH] /users/:userIdx
//     * @return BaseResponse<String>
//     */
//    @ResponseBody
//    @PatchMapping("/{userIdx}")
//    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody User user){
//        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //같다면 유저네임 변경
//            PatchUserReq patchUserReq = new PatchUserReq(userIdx,user.getUserName());
//            userService.modifyUserName(patchUserReq);
//
//            String result = "";
//        return new BaseResponse<>(result);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
//
//
//}
