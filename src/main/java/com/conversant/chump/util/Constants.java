package com.conversant.chump.util;

/**
 * Created by jhill on 23/12/14.
 */
public class Constants {

    public static final String PROPERTY_API_RESPONSE = "apiResponse";
    public static final String PROPERTY_TRX_NAME = "trxName";

    public static final String TYPE_CREATE_TRX = "G-createTrx-Intalio";
    public static final String TYPE_COMMIT_TRX = "G-commitTrx-Intalio";
    public static final String TYPE_ROLLBACK_TRX = "G-rollbackTrx-Intalio";
    public static final String TYPE_CREATE_CALL_PRODUCT = "P-createCallProduct-Intalio";
    public static final String TYPE_CREATE_DID_PRODUCT = "P-createDIDProduct-Intalio";
    public static final String TYPE_CREATE_CALL_SUBSCRIPTION = "P-createCallSubscription-Intalio";
    public static final String TYPE_CREATE_DID_SUBSCRIPTION = "P-createDIDSubscription-Intalio";
    public static final String TYPE_CREATE_NUMBER_PORT_SUBSCRIPTION = "P-createNumberPortSubscription-Intalio";
    public static final String TYPE_UPDATE_DID_PRODUCT = "P-updateDIDProduct-Intalio";
    public static final String TYPE_READ_ORDER = "AD-readOrder-Intalio";
    public static final String TYPE_READ_ORDER_DIDS = "AD-readOrderDIDs-Intalio";

	public static final String TYPE_READ_BUSINESS_PARTNER = "AD-readBusinessPartnerByValue-Drupal";
	public static final String TYPE_READ_INVOICES = "AC-readInvoicesByBusinessPartner-Drupal";
	public static final String TYPE_READ_INVOICE_LINES = "AC-readInvoiceLines-Intalio";

	public static final String ADEMPIERE_USER_DEFAULT = "IntalioUser";
	public static final String ADEMPIERE_PASS_DEFAULT = "dJw%U#s5";
	public static final String ADEMPIERE_USER_DRUPAL  = "DrupalUser";
	public static final String ADEMPIERE_PASS_DRUPAL  = "l8B#2c$p";
	public static final String ADEMPIERE_USER_INTALIO = "IntalioUser";
	public static final String ADEMPIERE_PASS_INTALIO = "dJw%U#s5";
}
