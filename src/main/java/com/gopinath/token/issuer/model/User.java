package com.gopinath.token.issuer.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    private long id;

    private String country;
    private String firstName;
    private String lastName;
    @Column(nullable= false, unique=true)
    private String emailAddress;
    @Column(nullable= false)
    private String password;

    @Column(nullable= false)
    private boolean isSoftwareDeveloper;
    @Column(nullable= true)
    private String verificationLink;

    @Column(nullable= false)
    private String userStatus;

    private String gender;
    private Date dateOfBirth;
    private String identificationDocument;
    private String identificationNumber;
    private String identificationDocumentPath;
}
