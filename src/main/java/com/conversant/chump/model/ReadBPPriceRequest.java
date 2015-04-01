package com.conversant.chump.model;

import lombok.Data;

/**
 * Created by Saren Currie on 2015-03-05.
 */
@Data
public class ReadBPPriceRequest {
    private String businessPartnerSearchKey;
    private String productId;
}
