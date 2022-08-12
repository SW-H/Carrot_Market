package com.example.demo.src.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

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
    @GetMapping("{userIdx}/parm") // (GET) 127.0.0.1:9000/users
    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String nickName,
                                                   @PathVariable("userIdx") int userIdx) {
        try {
            if (nickName == null) {
                throw new BaseException(USERS_EMPTY_NICKNAME);
            }
            List<GetUserRes> getUsersRes = userProvider.getUsersByNickname(nickName);
            if(jwtService.getUserIdx() == userIdx) {
                return new BaseResponse<>(getUsersRes);
            }
            throw new BaseException(INVALID_USER_JWT);
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
    @GetMapping("/{userIdx}")
    public BaseResponse<List<GetUserRes>> getUsersByCode(@PathVariable("userIdx") int userIdx) {
        try {
            if(jwtService.getUserIdx() == userIdx) {
                List<GetUserRes> getUsersRes = userProvider.getUsersByCode(userIdx);
                return new BaseResponse<>(getUsersRes);
            }
            throw new BaseException(INVALID_USER_JWT);
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
        try {
            if (userIdx < 0) {
                throw new BaseException(REQUEST_ERROR);
            }
            if(jwtService.getUserIdx() == userIdx) {
                GetProfileRes getProfileRes = userProvider.getProfiles(userIdx);
                return new BaseResponse<>(getProfileRes);
            }
            throw new BaseException(INVALID_USER_JWT);
        } catch (BaseException exception) {
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
        try {
            if (userIdx < 0) {
                throw new BaseException(REQUEST_ERROR);
            }
            if(jwtService.getUserIdx() == userIdx) {
                GetDurationRes getDurationRes = userProvider.getNickNameDuration(userIdx);
                return new BaseResponse<>(getDurationRes);
            }
            throw new BaseException(INVALID_USER_JWT);
        } catch (BaseException exception) {
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
        try {
            if (userIdx < 0) {
                throw new BaseException(REQUEST_ERROR);
            }
            if(jwtService.getUserIdx() == userIdx) {
                List<GetCertsRes> getCertsRes = userProvider.getCertifications(userIdx);
                return new BaseResponse<>(getCertsRes);
            }
            throw new BaseException(INVALID_USER_JWT);
        } catch (BaseException exception) {
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
        try {
            if (userIdx < 0) {
                throw new BaseException(REQUEST_ERROR);
            }
            if(jwtService.getUserIdx() == userIdx) {
                List<GetBadgeRes> getBadgeRes = userProvider.GetBadge(userIdx);
                return new BaseResponse<>(getBadgeRes);
            }
            throw new BaseException(INVALID_USER_JWT);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원가입 API
     * [POST] /users/sign-up
     *
     * @return BaseResponse<PostUserRes>
     */
    // TODO: 회원가입이 아닌 로그인 시에만 jwt 토큰 발급하도록 수정해야???
    // Body
    @ResponseBody
    @PostMapping("/sign-up")
    public BaseResponse<PostUserRes> createUser(@Validated @RequestBody PostUserReq postUserReq) {
        // TODO: 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        if (postUserReq.getPhoneNumber() == null) {
            return new BaseResponse<>(USERS_EMPTY_PHONENUM);
        }
        if (postUserReq.getTownName() == null){
            return new BaseResponse<>(USERS_EMPTY_TOWNNAME);
        }
        if (postUserReq.getNickName() == null){
            return new BaseResponse<>(USERS_EMPTY_NICKNAME);
        }
        if(!isRegexPhoneNum(postUserReq.getPhoneNumber())) {
            return new BaseResponse<>(USERS_INVALID_PHONENUM);
        }
        if(!isRegexTownName(postUserReq.getTownName())){
            return new BaseResponse<>(USERS_INVALID_TOWNNAME);
        }
        if(!isRegexNickName(postUserReq.getNickName())){
            return new BaseResponse<>(USERS_INVALID_NICKNAME);
        }
        try {
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        if (postLoginReq.getPhoneNumber() == null) {
            return new BaseResponse<>(USERS_EMPTY_PHONENUM);
        }
        if (!isRegexPhoneNum(postLoginReq.getPhoneNumber())) {
            return new BaseResponse<>(USERS_INVALID_PHONENUM);
        }
        try{
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

//    /**
//     * 유저닉네임변경 API
//     * [PATCH] /users/:userIdx/nickName
//     * @return BaseResponse<String>
//     */
//    @ResponseBody
//    @PatchMapping("/{userIdx}/nickName")
//    public BaseResponse<String> modifyNickName(@PathVariable("userIdx") int userIdx,
//                                               @RequestBody UserNickName userNickName){
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//
//        if (userNickName.getNewNickName() == null){
//            return new BaseResponse<>(USERS_EMPTY_NICKNAME);
//        }
//        if(!isRegexNickName(userNickName.getNewNickName())){
//            return new BaseResponse<>(USERS_INVALID_NICKNAME);
//        }
//        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //같다면 닉네임 변경
//            PatchUserReq patchUserReq = new PatchUserReq(userNickName.getUserIdx(),userNickName.getNewNickName(),timestamp);
//            userService.modifyNickName(patchUserReq);
//
//            String result = "사용자 닉네임이 변경 되었습니다.";
//            return new BaseResponse<String>(result);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
}





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
//
//}
