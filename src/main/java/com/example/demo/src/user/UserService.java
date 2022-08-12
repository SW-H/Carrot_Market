package com.example.demo.src.user;



import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    //POST
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {

        String phoneNumber;
        try{
            //암호화
            phoneNumber = new SHA256().encrypt(postUserReq.getPhoneNumber());
            postUserReq.setPhoneNumber(phoneNumber);

        } catch (Exception ignored) {
            throw new BaseException(PHONENUM_ENCRYPTION_ERROR);
        }
        int status = 0;
        try {
            status = userProvider.checkPhoneNum(phoneNumber);
            if (status == 1) { // 탈퇴한지 7일 이내로 가입 불가
                throw new BaseException(USERS_INVALID_UNREGISTER_DURATION);
            }
            if (status == 2) {// 이미 가입된 사용자
                throw new BaseException(POST_USERS_EXISTS_PHONENUM);
            }
        } catch (Exception exception)  {
            if (status == 1) {
                throw new BaseException(USERS_INVALID_UNREGISTER_DURATION);
            }
            else if (status == 2) {
                throw new BaseException(POST_USERS_EXISTS_PHONENUM);
            }
            else {
                throw new BaseException(DATABASE_ERROR);
            }
        }
        int userIdx = userDao.createUser(postUserReq);
        userDao.createUserTown(userIdx, postUserReq);
        //jwt 발급.
        String jwt = jwtService.createJwt(userIdx);
        return new PostUserRes(jwt,userIdx);

    }

//    public void modifyNickName(PatchUserReq patchUserReq) throws BaseException {
//        try{
//            int result = userDao.modifyNickName(patchUserReq);
//            if(result == 0){
//                throw new BaseException(MODIFY_FAIL_NICKNAME);
//            }
//        } catch(Exception exception){
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }


}

