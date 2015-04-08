package com.conversant.chump.model;

import lombok.Data;

/**
 * Created by Saren Currie on 2015-04-09.
 */

@Data
public class InsertBillingCustomerOutboundRequest {
    private String domain;
    private String profileName;
    private String profileNameAlt;
    private String timezone;
}
