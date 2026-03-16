package com.microtest.UserService.dto;

import com.microtest.UserService.enums.ROLE_ENUM;
import lombok.Builder;

@Builder
public record SignupResponse(Long id,
                             String fullName,
                             ROLE_ENUM role,
                             String login,
                             String email
) { }
