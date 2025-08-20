package com.microtest.UserService.dto;

import lombok.Data;

@Data
public class SignupRequest {

    private Long id;

    private String email;

    private String password;

    private String login;

    private String fullName;

    private int role;



}
