package com.gopinath.token.issuer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gopinath.token.issuer.enums.Permission;
import com.gopinath.token.issuer.model.*;
import com.gopinath.token.issuer.responses.TokenResponse;
import com.gopinath.token.issuer.service.TokenService;
import com.gopinath.token.issuer.service.UserOtpService;
import com.gopinath.token.issuer.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.minidev.json.JSONObject;
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
import java.util.ArrayList;
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


    @CrossOrigin
    @RequestMapping(value="/api/authenticate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Authenticate", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity<?> getToken(@Valid @RequestBody final RequestData requestData, final Errors errors) throws JsonProcessingException {

        LOG.error("1212 = ");
        if (errors.hasErrors()) {
            String errorMessage = errors.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(","));
            LOG.error("Error = " + errorMessage);
            return ResponseEntity.badRequest().body(errorMessage);
        }
        String subject = requestData.getUsername();
        Permission permission = Permission.CREATE_ROLE_PERMISSION;
        AuthResponse jweAuth = tokenService.getToken(requestData, userService, Arrays.asList(permission), tokenPeriodInMins, null);

        JSONObject jsonObject = new JSONObject();
        if(jweAuth.getValid().equals(Boolean.FALSE))
        {
            jsonObject.put("status", 1);
            jsonObject.put("message", jweAuth.getMessage());
            jsonObject.put("subject", subject);
            jsonObject.put("token", jweAuth.getToken());
            jsonObject.put("merchantList", new ArrayList<>());


            LOG.info("Token generated for " + subject);
            final HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + jwe);
            LOG.info("Authorization Header set with token");
            return (new ResponseEntity<>(jsonObject, headers, HttpStatus.OK));
        }
        List<Long> merchantIdList = userService.getMerchantIdsByUsername(requestData.getUsername());
        User us = userService.getUserByEmailAddress(subject);
        ObjectMapper objectMapper = new ObjectMapper();
        String merchantIds = objectMapper.writeValueAsString(merchantIdList);
//        String json = ("{\"subject\":\"" + subject
//                + "\",\"token\":\"" + jwe + "\",\"merchantList\":}");

        String jwe = jweAuth.getToken();

        if(jwe!=null)
        {
            jsonObject.put("status", 0);
            jsonObject.put("subject", new ObjectMapper().writeValueAsString(us));
            jsonObject.put("token", jwe);
            jsonObject.put("merchantList", merchantIdList);
            jsonObject.put("role", us.getUserRole());

            List<UserRolePermission> userRolePermissionList = userService.getPermissionsByRole(us.getUserRole().name());
            jsonObject.put("permissions", userRolePermissionList);


            LOG.info("Token generated for " + subject);
            final HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + jwe);
            LOG.info("Authorization Header set with token");
            return (new ResponseEntity<>(jsonObject, headers, HttpStatus.OK));
        }
        else {
            jsonObject.put("status", 1);
            jsonObject.put("message", jweAuth.getMessage());
            jsonObject.put("subject", subject);
            jsonObject.put("token", jwe);
            jsonObject.put("merchantList", merchantIdList);


            LOG.info("Token generated for " + subject);
            final HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + jwe);
            LOG.info("Authorization Header set with token");
            return (new ResponseEntity<>(jsonObject, headers, HttpStatus.OK));
        }
    }



    @CrossOrigin
    @RequestMapping(value="/api/otp-token", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Generate OTP For Authentication", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
//            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
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
        AuthResponse jweAuth = tokenService.getToken(requestData, userService, Arrays.asList(permission), tokenOtpPeriodInMins, otpKey);
        if(jweAuth.getToken()!=null)
            userService.createOtp(requestData, tokenOtpPeriodInMins, otpKey);
        else
            return (new ResponseEntity<>(jweAuth.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR));


        String json = ("{\"subject\":\"" + subject
                + "\",\"token\":\"" + jweAuth.getToken() + "\"}");
        LOG.info("Token generated for " + subject);
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jweAuth.getToken());
        LOG.info("Authorization Header set with token");
        return (new ResponseEntity<>(json, headers, HttpStatus.OK));
    }



    @CrossOrigin
    @RequestMapping(value="/api/validate-otp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Validate Users OTP", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
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

        List<Permission> permissions = Arrays.asList(Permission.CREATE_ROLE_PERMISSION);
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
