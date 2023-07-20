package com.gopinath.token.issuer.service;

import com.gopinath.token.issuer.model.User;
import com.gopinath.token.issuer.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    UserDao userDao;

    @Autowired
    public UserService(UserDao userDao){
        this.userDao = userDao;
    }

    public User loadUserByUsername(String username) {

        List<User> allUsers = userDao.getUserByEmailAddress(username);
        logger.info("{}", allUsers);

        User us = allUsers.get(0);
        logger.info("user dob ...{}", us.getDateOfBirth());

        return us;

    }
}
