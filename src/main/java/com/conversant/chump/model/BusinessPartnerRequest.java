package com.conversant.chump.model;

import lombok.Data;

@Data
public class BusinessPartnerRequest {

	private int orgId;
	private String searchKey;
	private String name;
	private boolean taxExempt;
	private int businessPartnerGroupId;
	private int salesRepId;
	private int priceListId;
}
