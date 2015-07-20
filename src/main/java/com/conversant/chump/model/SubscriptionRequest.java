package com.conversant.chump.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by Saren Currie on 2015-04-02.
 */
@Data
public class SubscriptionRequest {

    private String name;
    private int subscriptionTypeId;
    private int businessPartnerId;
    private int businessPartnerLocationId;
    private int productId;
    private Date startDate;
    private Date renewalDate;
    private Date paidUntilDate;
    private boolean billInAdvance;
    private int qty;
    private boolean isDue;
    private int userId;
    private int orgId;
}
