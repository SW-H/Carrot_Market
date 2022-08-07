package com.example.demo.src.post;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.post.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.REQUEST_ERROR;
import static com.example.demo.config.BaseResponseStatus.USERS_EMPTY_USER_NICKNAME;

@RestController
@RequestMapping("/posts")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final PostProvider postProvider;
    @Autowired
    private final PostService postService;
    @Autowired
    private final JwtService jwtService;


    public PostController(PostProvider postProvider, PostService postService, JwtService jwtService) {
        this.postProvider = postProvider;
        this.postService = postService;
        this.jwtService = jwtService;
    }

    /**
     * 판매 게시글 목록 조회 API
     * [GET] /posts/{userIdx}
     *
     * @return BaseResponse<List < GetPostRes>>
     */
    //Query String
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<List<GetPostRes>> getPost(@PathVariable("userIdx") int userIdx) {
        try {
            List<GetPostRes> getPostRes = postProvider.getPost(userIdx);
            return new BaseResponse<>(getPostRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}