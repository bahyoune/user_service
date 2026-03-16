package com.microtest.UserService.repository;

import com.microtest.UserService.entity.Users;
import com.microtest.UserService.support.TestEventFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@DataJpaTest (
        properties = "spring.cloud.config.enabled=false"
)
@ActiveProfiles("test")
public class UsersRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;
    private Users users;

    @BeforeEach
    void setup() {
        users = TestEventFactory.users_without_id();
    }

    @Test
    public void test_findIdByUserEmail() {
        //GIVEN
        usersRepository.save(users);

        //WHEN
        Optional<Long> exist = usersRepository.findIdByUserEmail("test@gmail.com");

        //THEN
        Assertions.assertTrue(exist.isPresent());
    }

    @Test
    public void test_findIdByUserLogin() {
        //GIVEN
        usersRepository.save(users);

        //WHEN
        Optional<Long> exist = usersRepository.findIdByUserLogin("test");

        //THEN
        Assertions.assertTrue(exist.isPresent());
    }

    @Test
    public void test_findByEmailOrLogin_email_exist() {
        //GIVEN
        usersRepository.save(users);

        //WHEN
        Optional<Users> exist = usersRepository.findByEmailOrLogin("test@gmail.com", "fsd");

        //THEN
        Assertions.assertTrue(exist.isPresent());
    }

    @Test
    public void test_findByEmailOrLogin_login_exist() {
        //GIVEN
        usersRepository.save(users);

        //WHEN
        Optional<Users> exist = usersRepository.findByEmailOrLogin("testd@gmail.com", "test");

        //THEN
        Assertions.assertTrue(exist.isPresent());
    }

    @Test
    public void test_findByEmailOrLogin_login_both() {
        //GIVEN
        usersRepository.save(users);

        //WHEN
        Optional<Users> exist = usersRepository.findByEmailOrLogin("test@gmail.com", "test");

        //THEN
        Assertions.assertTrue(exist.isPresent());
    }

    @Test
    public void test_findByEmailOrLogin_notExist() {
        //GIVEN
        usersRepository.save(users);

        //WHEN
        Optional<Users> exist = usersRepository.findByEmailOrLogin("test1@gmail.com", "test1");

        //THEN
        Assertions.assertFalse(exist.isPresent());
    }
}
