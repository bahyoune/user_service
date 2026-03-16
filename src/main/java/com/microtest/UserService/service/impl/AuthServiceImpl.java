package com.microtest.UserService.service.impl;


import com.microtest.UserService.entity.Users;
import com.microtest.UserService.dto.SignUpRequest;
import com.microtest.UserService.dto.SignupResponse;
import com.microtest.UserService.exception.LoginOrEmailExistException;
import com.microtest.UserService.repository.UsersRepository;
import com.microtest.UserService.service.AuthService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsersRepository userRepository;


    //<editor-fold defaultState="collapsed" desc="Create User">
    @Override
    @Transactional
    public SignupResponse createUser(SignUpRequest data) throws LoginOrEmailExistException, IllegalArgumentException {

        if (hasUserWithEmailOrLogin(data.email(), data.login())) {
            throw new LoginOrEmailExistException();
        }

        if (data.password().length() < 3) {
            throw new IllegalArgumentException("The Password is short");
        }

        Users user = Users.builder()
                .email(data.email().toLowerCase())
                .login(data.login().toLowerCase())
                .role(data.role())
                .state(true)
                .fullName(data.fullName())
                .d0(new Date())
                .password(new BCryptPasswordEncoder().encode(data.password()))
                .build();


        user = userRepository.save(user);

        return SignupResponse.builder()
                .fullName(user.getFullName())
                .id(user.getId())
                .login(user.getLogin())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

    }


    public Boolean hasUserWithEmailOrLogin(String email, String login) {

        if (userRepository.findIdByUserEmail(email).isPresent()) {
            return userRepository.findIdByUserLogin(login).isPresent();
        }
        return false;
    }
//</editor-fold>

}


