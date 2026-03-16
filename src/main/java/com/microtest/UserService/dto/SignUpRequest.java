package com.microtest.UserService.dto;

import com.microtest.UserService.enums.ROLE_ENUM;
import lombok.Builder;

@Builder
public record SignUpRequest(
        String email,
        String login,
        String password,
        String fullName,
        ROLE_ENUM role
) {
}
