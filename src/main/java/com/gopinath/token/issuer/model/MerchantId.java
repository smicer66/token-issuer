package com.gopinath.token.issuer.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantId {
    private Long id;
    private String businessName;
    private String merchantCode;
    private String businessLogo;
}
