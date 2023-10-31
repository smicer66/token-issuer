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
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String mobileNumber;
    private String password;
    private String forgotPasswordLink;

    private String userStatus;

    private String gender;
    private Date dateOfBirth;
    private String otp;
    private LocalDateTime otpExpiryDate;
    private String verificationLink;
    private String primaryBusinessName;
    private String country;
    private Boolean softwareDeveloper;
    private String identificationDocument;
    private String identificationNumber;
    private String identificationDocumentPath;
    private Long primaryMerchantId;
}
