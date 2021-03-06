package com.conversant.chump.model;

import lombok.Data;

import java.util.List;

/**
 * Created by jhill on 31/12/14.
 */
@Data
public class CreateNumberRequest extends BatchRequest<CreateNumberRequest> {

    private String domain;
    private String number;
    private List<String> numbers;
    private int priceListVersionId;
    private String countryId;
    private String countryCode;
    private String areaCode;
    private String areaCodeDescription;
    private String freeMinutes;
    private String perMinuteCharge;
    private int businessPartnerId;
    private String setupCost;
    private String monthlyCharge;
    private int currencyId;
    private int orgId;
    private String name;
}
