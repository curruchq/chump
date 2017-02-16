package com.conversant.chump.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Tate Currie on 2017-02-16
 */
@Data
public class BusinessPartnerPaymentRequest {
    private int businessPartnerId;
    private int businessPartnerLocationId;
    private int invoiceId;
    private String creditCardNumber;
    private String creditCardVerificationCode;
    private int creditCardExpiryMonth;
    private int creditCardExpiryYear;
    private BigDecimal amount;
    private int orgId;
}
