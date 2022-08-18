package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공 - 추가 필요 x
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users

    USERS_EMPTY_USER_NICKNAME(false, 2011, "유저 닉네임 값을 확인해주세요."),
    USERS_EMPTY_USER_IDX(false, 2012, "유저 식별자 값을 확인해주세요."),

    USERS_INVALID_PHONENUM(false, 2013, "핸드폰 번호 형식을 확인해주세요."),
    USERS_EMPTY_PHONENUM(false, 2014, "핸드폰 번호를 입력해주세요."),
    USERS_INVALID_TOWNNAME(false, 2015,"동 이름을 확인해주세요" ),
    USERS_EMPTY_TOWNNAME(false, 2016, "동 이름을 입력해주세요."),
    USERS_INVALID_NICKNAME(false, 2017,"닉네임 형식을 확인해주세요" ),
    USERS_EMPTY_NICKNAME(false, 2018, "닉네임을 입력해 주세요"),


    // [POST] /users
    USERS_EMPTY_POST(false, 2023, "작성한 판매글이 없습니다"),


    POST_USERS_EXISTS_PHONENUM(false,2025,"중복된 핸드폰 번호입니다."),
    USERS_INVALID_UNREGISTER_DURATION(false,2026,"일주일 내에 탈퇴한 기록이 있습니다"),
    POST_INVALID__LENGTH(false, 2030, "게시글 글자수를 확인하세요"),
    POST_INVALID__PRICE(false, 2031, "금액을 확인하세요"),
    POST_INVALID_CATEGORY(false, 2032, "카테고리 범위를 확인하세요"),


    /**
     * 3000 : Response 오류 - 의미적 validation
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    FAILED_TO_LOGIN(false,3014,"없는 핸드폰 번호입니다."),
    USERS_INVALID_USERCODE(false,3016,"존재하지 않는 유저코드입니다"),
    FAILED_TO_KAKAOLOGIN(false,3017,"카카오로부터 데이터를 불러오는데에 실패하였습니다"),


    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_NICKNAME(false,4014,"닉네임 수정 실패"),

    PHONENUM_ENCRYPTION_ERROR(false, 4011, "핸드폰 번호 암호화에 실패하였습니다."),
    PHONENUM_DECRYPTION_ERROR(false, 4012, "핸드폰 번호 복호화에 실패하였습니다.");



    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
