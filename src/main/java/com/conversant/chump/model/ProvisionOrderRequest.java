package com.conversant.chump.model;

import lombok.Data;

/**
 * Created by jhill on 2/01/15.
 */
@Data
public class ProvisionOrderRequest {

    private String orderNo;
    private String realm;
    private String proxy;
}
