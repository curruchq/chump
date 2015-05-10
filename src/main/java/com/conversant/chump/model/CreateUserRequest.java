package com.conversant.chump.model;

import lombok.Data;

@Data
public class CreateUserRequest {
	
	private String searchKey;
	private String name;
	private String password;
	private String email;
	private String phone;
	private String mobile;
	private int businessPartnerId;
	private int businessPartnerLocationId;
}
