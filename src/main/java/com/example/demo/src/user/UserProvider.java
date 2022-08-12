package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    public List<GetUserRes> getUsersByNickname(String nickName) throws BaseException {
        boolean isEmpty = false;
        try {
            List<GetUserRes> getUsersRes = userDao.getUsersByNickname(nickName);
            isEmpty = getUsersRes.isEmpty();
            if (isEmpty) {
                throw new BaseException(USERS_EMPTY_USER_NICKNAME);
            }

            return getUsersRes;
        } catch (Exception exception) {
            if (isEmpty) {
                throw new BaseException(USERS_EMPTY_USER_NICKNAME);
            }
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetUserRes> getUsersByCode(int userCode) throws BaseException {
        List<GetUserRes> getUsersRes = null;
        try {
            getUsersRes = userDao.getUsersByCode(userCode);
            return getUsersRes;
        } catch (Exception exception) {
            if (getUsersRes == null) {
                throw new BaseException(USERS_INVALID_USERCODE);
            }
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetUserRes> getUsers() throws BaseException {
        try {
            List<GetUserRes> getUserRes = userDao.getUsers();
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetProfileRes getProfiles(int userIdx) throws BaseException {
        GetProfileRes getProfileRes = null;
        try {
            getProfileRes = userDao.getProfiles(userIdx);
            return getProfileRes;
        } catch (Exception exception) {
            if(getProfileRes == null) {
                throw new BaseException(USERS_EMPTY_USER_IDX);
            }
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetDurationRes getNickNameDuration(int userIdx) throws BaseException {
        GetDurationRes getDurationRes = null;
        try {
            getDurationRes = userDao.getNickNameDuration(userIdx);
            return getDurationRes;
        } catch (Exception exception) {
            if(getDurationRes == null) {
                throw new BaseException(USERS_EMPTY_USER_IDX);
            }
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetCertsRes> getCertifications(int userIdx) throws BaseException {
        List<GetCertsRes> getCertsRes = null;
        try {
            getCertsRes = userDao.getCertifications(userIdx);
            return getCertsRes;
        } catch (Exception exception) {
            if(getCertsRes == null) {
                throw new BaseException(USERS_EMPTY_USER_IDX);
            }
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetBadgeRes> GetBadge(int userIdx) throws BaseException {
        List<GetBadgeRes> getBadgeRes = null;
        try {
            getBadgeRes = userDao.getBadge(userIdx);
            return getBadgeRes;
        } catch (Exception exception) {
            if(getBadgeRes == null) {
                throw new BaseException(USERS_EMPTY_USER_IDX);
            }
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkPhoneNum(String phoneNum) throws BaseException{
        try{
            return userDao.checkPhoneNum(phoneNum);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException{
        String encryptPhoneNum;
        User user = null;
        try {
            encryptPhoneNum = new SHA256().encrypt(postLoginReq.getPhoneNumber());
        } catch (Exception ignored) {
            throw new BaseException(PHONENUM_ENCRYPTION_ERROR);
        }
        try{
            user = userDao.getPhoneNumber(encryptPhoneNum);
        }catch (Exception exception){
            throw new BaseException(FAILED_TO_LOGIN);
        }
        if(user.getStatus() == 0){
            throw new BaseException(FAILED_TO_LOGIN);
        }
        int userIdx = user.getUserIdx();
        String jwt = jwtService.createJwt(userIdx);
        return new PostLoginRes(userIdx,jwt);
    }
}

