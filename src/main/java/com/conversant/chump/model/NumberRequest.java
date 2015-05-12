package com.conversant.chump.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by jhill on 31/12/14.
 */
@Data
public class NumberRequest {

    private String number;
    private String realm;
    private String proxy;
    private int businessPartnerId;
    private int businessPartnerLocationId;
    private Date startDate;
    private Date paidUntilDate;
}
