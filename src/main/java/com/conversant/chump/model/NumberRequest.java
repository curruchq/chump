package com.conversant.chump.model;

import com.conversant.chump.util.DateUtil;
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
    private int orgId;

    public void setStartDate(Date startDate) {
        this.startDate = DateUtil.zeroTime(startDate);
    }

    public void setPaidUntilDate(Date paidUntilDate) {
        this.paidUntilDate = DateUtil.zeroTime(paidUntilDate);
    }
}
