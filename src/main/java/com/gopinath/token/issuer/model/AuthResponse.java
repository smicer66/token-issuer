package com.gopinath.token.issuer.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    private Boolean valid;
    private String message;
    private String token;


    public AuthResponse(Boolean valid, String message, String token)
    {
        this.valid = valid;
        this.message = message;
        this.token = token;
    }
}
