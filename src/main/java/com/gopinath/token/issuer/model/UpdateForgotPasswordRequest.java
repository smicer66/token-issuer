package com.gopinath.token.issuer.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateForgotPasswordRequest {
    private String newPassword;
    private String emailAddress;
    private String forgotPasswordLink;
}
