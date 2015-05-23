package com.conversant.chump.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by jhill on 31/12/14.
 */
@Data
public class NumberRequest extends BatchRequest<NumberRequest> {

    private String number;
    private List<String> numbers;
    private String realm;
    private String proxy;
    private int businessPartnerId;
    private int businessPartnerLocationId;
    private Date startDate;
    private Date paidUntilDate;
}
