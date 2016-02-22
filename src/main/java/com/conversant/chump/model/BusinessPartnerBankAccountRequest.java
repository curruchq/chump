package com.conversant.chump.model;

import lombok.Data;

/**
 * Created by Tate on 25/01/2016.
 */
@Data
public class BusinessPartnerBankAccountRequest {

    private int userId;
    private int businessPartnerId;
    private int bankId;
    private Boolean ACH;
    private String creditCardType;
    private String creditCardNumber;
    private String creditCardVerificationCode;
    private int creditCardExpiryMonth;
    private int creditCardExpiryYear;
    private String accountName;
    private String accountStreet;
    private String accountCity;
    private String accountZip;
    private String accountState;
    private String accountCountry;
    private String accountUsage;
    private String accountType;
    private String accountNo;
    private int orgId;
    private int locationId;
}