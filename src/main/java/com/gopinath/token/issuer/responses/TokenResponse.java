package com.gopinath.token.issuer.responses;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponse {
    private String message;
    private String responseCode;
}
