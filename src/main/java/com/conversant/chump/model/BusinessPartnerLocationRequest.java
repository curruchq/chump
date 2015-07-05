package com.conversant.chump.model;

import lombok.Data;

/**
 * Created by jhill on 20/07/15.
 */
@Data
public class BusinessPartnerLocationRequest {

    private String name;
    private int businessPartnerId;
    private int businessPartnerLocationId;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String city;
    private int cityId;
    private String zip;
    private String region;
    private int regionId;
    private int countryId;
}
