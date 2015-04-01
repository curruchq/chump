package com.conversant.chump.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by Saren Currie on 2015-04-02.
 */
@Data
public class UpdateSubscriptionRequest {

    private String name;
    private int subscriptionTypeId;
    private int businessPartnerId;
    private int businessPartnerLocationId;
    private int productId;
    private Date startDate;
    private Date renewalDate;
    private Date paidUntilDate;
    private boolean billInAdvance;
    private int qty; //This is the name of the field returned by adempiere, I went for conisistency over non abreveation.
    private boolean isDue;
    private int userId;
}
