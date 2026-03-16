package com.microtest.UserService.service;


import com.microtest.UserService.dto.SignUpRequest;
import com.microtest.UserService.dto.SignupResponse;
import com.microtest.UserService.exception.LoginOrEmailExistException;

public interface AuthService {

    SignupResponse createUser(SignUpRequest data) throws LoginOrEmailExistException;

}