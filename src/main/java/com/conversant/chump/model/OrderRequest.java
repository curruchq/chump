package com.conversant.chump.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by jhill on 20/07/15.
 */
@Data
public class OrderRequest {

    private int businessPartnerId;
    private int businessPartnerLocationId;
    private int pricelistId;
    private int warehouseId;
    private Date datePromised;
    private Date dateOrdered;
    private int orgId;
}
