package com.conversant.chump.model;

import lombok.Data;

/**
 * Created by Saren Currie on 15-02-16.
 */
@Data
public class MigrateNumberRequest {
    private String number;
    private String realm;
    private int businessPartnerId;
    private int businessPartnerLocationId;
    private int priceListVersionId;
}
