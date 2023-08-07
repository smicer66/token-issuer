package com.gopinath.token.issuer.service;

import com.gopinath.token.issuer.dao.UserDao;
import com.gopinath.token.issuer.dao.UserOtpDao;
import com.gopinath.token.issuer.model.RequestData;
import com.gopinath.token.issuer.model.User;
import com.gopinath.token.issuer.model.UserOtp;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class UserOtpService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    UserOtpDao userOtpDao;

    @Autowired
    public UserOtpService(UserOtpDao userOtpDao){
        this.userOtpDao = userOtpDao;
    }

    public UserOtp getUserOtp(String username, String otp, String key) {

        List<UserOtp> allUserOtps = userOtpDao.getUserOtpByOtpAndKey(username, key, otp);
        logger.info("{}", allUserOtps);

        UserOtp us = allUserOtps.get(0);

        return us;

    }
}
