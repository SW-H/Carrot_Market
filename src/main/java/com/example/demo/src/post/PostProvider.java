package com.example.demo.src.post;


import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class PostProvider {

    private final PostDao postDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PostProvider(PostDao postDao, JwtService jwtService) {
        this.postDao = postDao;
        this.jwtService = jwtService;
    }

    public List<GetPostRes> getPost(int userIdx) throws BaseException {
        List<GetPostRes> getPostRes = null;
        try {
            getPostRes = postDao.getPost(userIdx);
            return getPostRes;
        } catch (Exception exception) {
            if(getPostRes == null) {
                throw new BaseException(USERS_EMPTY_POST);
            }
            throw new BaseException(DATABASE_ERROR);
        }
    }

}



//
//    public List<GetPostRes> getPostsByEmail(String email) throws BaseException{
//        try{
//            List<GetPostRes> getPostsRes = postDao.getPostsByEmail(email);
//            return getPostsRes;
//        }
//        catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//
//    public GetPostRes getPost(int postIdx) throws BaseException {
//        try {
//            GetPostRes getPostRes = postDao.getPost(postIdx);
//            return getPostRes;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public int checkEmail(String email) throws BaseException{
//        try{
//            return postDao.checkEmail(email);
//        } catch (Exception exception){
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException{
//        Post post = postDao.getPwd(postLoginReq);
//        String encryptPwd;
//        try {
//            encryptPwd=new SHA256().encrypt(postLoginReq.getPassword());
//        } catch (Exception ignored) {
//            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
//        }
//
//        if(post.getPassword().equals(encryptPwd)){
//            int postIdx = post.getPostIdx();
//            String jwt = jwtService.createJwt(postIdx);
//            return new PostLoginRes(postIdx,jwt);
//        }
//        else{
//            throw new BaseException(FAILED_TO_LOGIN);
//        }
//
//    }
//
//}
