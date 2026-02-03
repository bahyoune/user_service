package com.microtest.UserService.service.auth;


import com.microtest.UserService.bean.Users;
import com.microtest.UserService.dto.SignupRequest;
import com.microtest.UserService.enums.ROLE_ENUM;
import com.microtest.UserService.repo.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsersRepository userRepository;


    @Override
    public Long getAdminID(String email) {
        Optional<Users> user = userRepository.findFirstByEmailOrLogin(email);
        return user.map(Users::getId).orElse(null);
    }


    //<editor-fold defaultState="collapsed" desc="Find User Admin and Manager">
    @Override
    @Transactional
    //Admin and Manager
    public SignupRequest createUser(SignupRequest data) {

        if (!hasUserWithEmailOrLogin(data.getEmail(), data.getLogin())) {
            Users user = new Users();
            user.setEmail(data.getEmail().toLowerCase());
            user.setLogin(data.getLogin().toLowerCase());
            user.setRole(getRole(data.getRole()));
            user.setState(true);
            user.setFullName(data.getFullName());
            user.setD0(new Date());
            user.setPassword(new BCryptPasswordEncoder().encode(data.getPassword()));


            user = userRepository.save(user);
            data.setId(user.getId());

            return data;
        }

        return null;

    }

    private ROLE_ENUM getRole(int role) {
        return switch (role) {
            case 0 -> ROLE_ENUM.ADMIN;
            case 3 -> ROLE_ENUM.USER;
            default -> ROLE_ENUM.MANAGER_1;
        };
    }

    private Boolean hasUserWithEmailOrLogin(String email, String login) {

        if (userRepository.findEmailUser(email).isPresent()) {
            return userRepository.findLoginUser(login).isPresent();
        }

        return false;
    }
//</editor-fold>

}


