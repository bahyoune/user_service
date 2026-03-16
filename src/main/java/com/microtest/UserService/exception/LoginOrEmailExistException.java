package com.microtest.UserService.exception;

public class LoginOrEmailExistException extends Exception{
    public LoginOrEmailExistException(String message){
        super(message);
    }

    public LoginOrEmailExistException(){
        super("The Email or Login exist try new identification");
    }
}
