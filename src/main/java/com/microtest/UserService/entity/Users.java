package com.microtest.UserService.entity;

import com.microtest.UserService.enums.ROLE_ENUM;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "USERS")
public class Users {

    private static final long serialVersionID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String email;
    @Column(nullable = false, unique = true, length = 80)
    private String login;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private ROLE_ENUM role;

    @Column(nullable = false)
    private boolean state;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date d0;


}
