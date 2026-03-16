package com.microtest.UserService.service;

import com.microtest.UserService.entity.Users;
import com.microtest.UserService.dto.SignUpRequest;
import com.microtest.UserService.exception.LoginOrEmailExistException;
import com.microtest.UserService.repository.UsersRepository;
import com.microtest.UserService.service.impl.AuthServiceImpl;
import com.microtest.UserService.support.TestEventFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @Spy
    @InjectMocks
    private AuthServiceImpl authService;


    private Users users;

    @BeforeEach
     void setup() {
        users = TestEventFactory.users();
    }

    @Test
    public void testCreateUser_valid_user() throws Exception {
        //Given
        SignUpRequest   data = TestEventFactory.signUpRequest();

       Mockito.when(usersRepository.save(Mockito.any(Users.class)))
               .thenReturn(users);

       //WHEN
        var result = authService.createUser(data);

        //THEN
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.id());

        Mockito.verify(usersRepository, Mockito.timeout(1)).save(Mockito.any(Users.class));


    }

    @Test
    public void testCreateUser_email_or_login_exist()  {
        //Given
        SignUpRequest   data = TestEventFactory.signUpRequest();

        //Stub
        Mockito.doReturn(true)
                .when(authService)
                .hasUserWithEmailOrLogin("test@gmail.com", "test");


        //WHEN + THEN

        LoginOrEmailExistException exception = Assertions
                .assertThrows(LoginOrEmailExistException.class,
                        () -> authService.createUser(data));

        Assertions.assertEquals("The Email or Login exist try new identification", exception.getMessage());

        Mockito.verify(usersRepository, Mockito.never()).save(users);


    }


}
