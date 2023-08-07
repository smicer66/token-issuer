package com.gopinath.token.issuer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gopinath.token.issuer.enums.Permission;
import com.gopinath.token.issuer.model.RequestData;
import com.gopinath.token.issuer.model.User;
import com.gopinath.token.issuer.model.UserOtp;
import com.gopinath.token.issuer.model.ValidateOtpRequestData;
import com.gopinath.token.issuer.responses.TokenResponse;
import com.gopinath.token.issuer.service.TokenService;
import com.gopinath.token.issuer.service.UserOtpService;
import com.gopinath.token.issuer.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TokenController {

    @Autowired
    TokenService tokenService;

    @Autowired
    UserOtpService userOtpService;

    @Autowired
    UserService userService;

    @Value("${token.period.in.mins}")
    int tokenPeriodInMins;

    @Value("${token.otp.period.in.mins}")
    int tokenOtpPeriodInMins;

    private final Logger LOG = LoggerFactory.getLogger(TokenController.class);

    @RequestMapping(value="/api/jwe", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getToken(@Valid @RequestBody final RequestData requestData, final Errors errors) throws JsonProcessingException {

        LOG.error("1212 = ");
        if (errors.hasErrors()) {
            String errorMessage = errors.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(","));
            LOG.error("Error = " + errorMessage);
            return ResponseEntity.badRequest().body(errorMessage);
        }
        String subject = requestData.getUsername();
        Permission permission = Permission.ALL_PERMISSIONS;
        String jwe = tokenService.getToken(requestData, userService, Arrays.asList(permission), tokenPeriodInMins, null);
        String json = ("{\"subject\":\"" + subject 
                + "\",\"token\":\"" + jwe + "\"}");
        LOG.info("Token generated for " + subject);
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwe);
        LOG.info("Authorization Header set with token");
        return (new ResponseEntity<>(json, headers, HttpStatus.OK));
    }



    @RequestMapping(value="/api/otp-token", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOtpToken(@Valid @RequestBody final RequestData requestData, final Errors errors) throws JsonProcessingException {

        LOG.error("1212 = ");
        if (errors.hasErrors()) {
            String errorMessage = errors.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(","));
            LOG.error("Error = " + errorMessage);
            return ResponseEntity.badRequest().body(errorMessage);
        }
        String subject = requestData.getUsername();
        Permission permission = Permission.AUTHENTICATE_WITH_OTP;
        String otpKey = RandomStringUtils.randomAlphanumeric(16).toUpperCase();
        LOG.info("key...{}", otpKey);
        String jwe = tokenService.getToken(requestData, userService, Arrays.asList(permission), tokenOtpPeriodInMins, otpKey);
        if(jwe!=null)
            userService.createOtp(requestData, tokenOtpPeriodInMins, otpKey);
        else
            return (new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR));


        String json = ("{\"subject\":\"" + subject
                + "\",\"token\":\"" + jwe + "\"}");
        LOG.info("Token generated for " + subject);
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwe);
        LOG.info("Authorization Header set with token");
        return (new ResponseEntity<>(json, headers, HttpStatus.OK));
    }




    @RequestMapping(value="/api/validate-otp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postValidateOtp(@Valid @RequestBody final ValidateOtpRequestData validateOtpRequestData, final Errors errors,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws JsonProcessingException {

        String username = validateOtpRequestData.getUsername();
        String otp = validateOtpRequestData.getOtp();
        String key = validateOtpRequestData.getKey();


        UserOtp userOtp = userOtpService.getUserOtp(username, otp, key);

        LocalDateTime now = LocalDateTime.now();

        if(now.isAfter(userOtp.getExpiryDate()))
        {
            return (new ResponseEntity<>("One-Time Password has expired", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR));
        }

        List<Permission> permissions = Arrays.asList(Permission.ALL_PERMISSIONS);
        String token = tokenService.getTokenByUsername(validateOtpRequestData.getUsername(), userService, permissions, tokenPeriodInMins, null);

        TokenResponse tokenResponse = new TokenResponse();
        if(token!=null) {
            tokenResponse.setResponseCode("00");
            tokenResponse.setMessage(token);
        }
        else
        {
            tokenResponse.setResponseCode("01");
            tokenResponse.setMessage(null);
        }
        return (new ResponseEntity<>(tokenResponse, new HttpHeaders(), HttpStatus.OK));
    }
}
