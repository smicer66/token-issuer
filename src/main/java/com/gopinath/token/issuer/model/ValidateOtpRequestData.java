package com.gopinath.token.issuer.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateOtpRequestData {
    String username;
    String otp;
    String key;

}

