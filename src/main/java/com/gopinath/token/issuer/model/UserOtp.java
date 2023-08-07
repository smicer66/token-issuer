package com.gopinath.token.issuer.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "users_otp")
public class UserOtp implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String userId;
    private String otp;
    private LocalDateTime expiryDate;
    private boolean isUsed;
    private String otpKey;
}
