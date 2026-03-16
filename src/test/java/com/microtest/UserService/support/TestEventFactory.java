package com.microtest.UserService.support;

import com.microtest.UserService.entity.Users;
import com.microtest.UserService.dto.SignUpRequest;
import com.microtest.UserService.dto.SignupResponse;
import com.microtest.UserService.enums.ROLE_ENUM;

import java.util.Date;

public class TestEventFactory {

    public static SignupResponse signupResponse() {
        return SignupResponse.builder()
                .login("test")
                .email("test@gmail.com")
                .role(ROLE_ENUM.ADMIN)
                .fullName("Marouane Diallo")
                .id(1L)
                .build();
    }

    public static SignUpRequest signUpRequest(){
        return SignUpRequest.builder()
                .login("test")
                .email("test@gmail.com")
                .role(ROLE_ENUM.ADMIN)
                .fullName("Marouane Diallo")
                .password("1234")
                .build();
    }

    public static SignUpRequest signUpRequest_for_user_exist(){
        return SignUpRequest.builder()
                .login("test1")
                .email("test1@gmail.com")
                .role(ROLE_ENUM.ADMIN)
                .fullName("Marouane Diallo")
                .password("1234")
                .build();
    }

    public static SignUpRequest signUpRequest_with_error_password(){
        return SignUpRequest.builder()
                .login("test")
                .email("test@gmail.com")
                .role(ROLE_ENUM.ADMIN)
                .fullName("Marouane Diallo")
                .password("12")
                .build();
    }

    public static Users users() {
        return Users.builder()
                .id(1L)
                .d0(new Date())
                .login("test")
                .email("test@gmail.com")
                .role(ROLE_ENUM.ADMIN)
                .fullName("Marouane Diallo")
                .password("1234")
                .build();
    }

    public static Users users_without_id() {
        return Users.builder()
                .d0(new Date())
                .login("test")
                .email("test@gmail.com")
                .role(ROLE_ENUM.ADMIN)
                .fullName("Marouane Diallo")
                .password("1234")
                .build();
    }
}
