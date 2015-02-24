package com.conversant.chump.model;

import lombok.Data;

/**
 * POJO for a migration request.
 * <p/>
 * Created by Saren Currie on 15-02-10.
 */
@Data
public class MigrationRequest {
    private String businessPartnerSearchKey;
    private String realm;
    private String mainNumber;
    private String[] numbers;
    private int priceListVersionId;
    private int businessPartnerLocationId;
}
