package com.gopinath.token.issuer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gopinath.token.issuer.model.RequestData;
import com.gopinath.token.issuer.service.TokenService;
import com.gopinath.token.issuer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.validation.Valid;

import java.util.stream.Collectors;

@RestController
public class TokenController {

    @Autowired
    TokenService tokenService;

    @Autowired
    UserService userService;

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
        String jwe = tokenService.getToken(requestData, userService);
        String json = ("{\"subject\":\"" + subject 
                + "\",\"token\":\"" + jwe + "\"}");
        LOG.info("Token generated for " + subject);
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwe);
        LOG.info("Authorization Header set with token");
        return (new ResponseEntity<>(json, headers, HttpStatus.OK));
    }
}
