package com.conversant.chump.model;

import lombok.Data;

import java.util.Date;

@Data
public class BusinessPartnerRadiusAccountsRequest {

	private int businessPartnerId;
	private String billingParty;
	private String otherParty;
	private String dateFrom;
	private String dateTo;
	private String billingId;
	private boolean classified;

}
