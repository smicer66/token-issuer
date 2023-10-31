package com.gopinath.token.issuer.controller;

import com.gopinath.token.issuer.model.ForgotPasswordRequest;
import com.gopinath.token.issuer.model.UpdateForgotPasswordRequest;
import com.gopinath.token.issuer.model.User;
import com.gopinath.token.issuer.responses.TokenResponse;
import com.gopinath.token.issuer.service.TokenService;
import com.gopinath.token.issuer.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class UserController {




    @Autowired
    UserService userService;
    @Autowired
    TokenService tokenService;


}
