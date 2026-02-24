package com.microtest.UserService.service.auth;


import com.microtest.UserService.dto.SignupRequest;

public interface AuthService {

    SignupRequest createUser(SignupRequest data);

}