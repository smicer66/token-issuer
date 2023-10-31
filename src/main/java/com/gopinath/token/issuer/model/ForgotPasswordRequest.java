package com.gopinath.token.issuer.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ForgotPasswordRequest{
    private String emailAddress;
    private String forgotPasswordEndpoint;
}
