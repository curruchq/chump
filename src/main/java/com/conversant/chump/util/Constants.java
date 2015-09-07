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
    public static final String TYPE_CREATE_CALL_PRODUCT_2 = "P-createCallProduct2-Intalio";
    public static final String TYPE_CREATE_DID_PRODUCT = "P-createDIDProduct-Intalio";
    public static final String TYPE_CREATE_CALL_SUBSCRIPTION = "P-createCallSubscription-Intalio";
    public static final String TYPE_CREATE_CALL_SUBSCRIPTION_2 = "P-createCallSubscription2-Intalio";
    public static final String TYPE_CREATE_DID_SUBSCRIPTION = "P-createDIDSubscription-Intalio";
    public static final String TYPE_CREATE_NUMBER_PORT_SUBSCRIPTION = "P-createNumberPortSubscription-Intalio";
    public static final String TYPE_UPDATE_DID_PRODUCT = "P-updateDIDProduct-Intalio";
    public static final String TYPE_CREATE_ORDER = "AD-createOrder-Intalio";
    public static final String TYPE_READ_ORDER = "AD-readOrder-Intalio";
    public static final String TYPE_READ_ORDER_DIDS = "AD-readOrderDIDs-Intalio";
    public static final String TYPE_READ_ORDER_NUMBER_PORTS = "AD-readOrderNumberPorts-Intalio";
    public static final String TYPE_READ_ORDER_LINES = "AD-readOrderLines-Intalio";
    public static final String TYPE_UPDATE_ORDER = "AD-updateOrder-Intalio";

    public static final String TYPE_READ_BUSINESS_PARTNER = "AD-readBusinessPartnerByValue-Drupal";
    public static final String TYPE_READ_BUSINESS_PARTNER_LOCATION = "AD-readBusinessPartnerLocation-Drupal";
    public static final String TYPE_READ_BUSINESS_PARTNER_USERS = "AD-readUsersByBusinessPartner-Drupal";
    public static final String TYPE_READ_BUSINESS_PARTNER_ORDERS = "AD-readOrderByBPartnerSearchKey-Intalio";
    public static final String TYPE_CREATE_BUSINESS_PARTNER = "AD-createBusinessPartner-Drupal";
    public static final String TYPE_CREATE_LOCATION = "AD-createLocation-Intalio";
    public static final String TYPE_CREATE_BUSINESS_PARTNER_LOCATION = "AD-createBusinessPartnerLocation-Intalio";
    public static final String TYPE_READ_BUSINESS_PARTNERS = "AD-readBusinessPartner-Drupal";
    public static final String TYPE_UPDATE_BUSINESS_PARTNER = "AD-updateBusinessPartner-Intalio";
    public static final String TYPE_UPDATE_BUSINESS_PARTNER_LOCATION = "AD-updateBusinessPartnerLocation-Intalio";
    public static final String TYPE_READ_BUSINESS_PARTNERS_BY_GROUP = "AD-readBusinessPartnersByGroup-Intalio";

    public static final String TYPE_READ_SUBSCRIBED_NUMBERS = "AD-readSubscribedNumbers-Drupal";
    public static final String TYPE_READ_INVOICES = "AC-readInvoicesByBusinessPartner-Drupal";
    public static final String TYPE_READ_INVOICE_LINES = "AC-readInvoiceLines-Intalio";
    public static final String TYPE_READ_RADIUS_ACCOUNTS = "P-readRadiusAccountsByInvoice-Intalio";
    public static final String TYPE_READ_SUBSCRIPTIONS = "AD-readSubscriptions-Drupal";
    public static final String TYPE_READ_SUBSCRIPTION = "AD-readSubscription-Drupal";
    public static final String TYPE_UPDATE_SUBSCRIPTION = "AD-updateSubscription-Drupal";
    public static final String TYPE_CREATE_SUBSCRIPTION = "AD-createSubscription-Drupal";
    public static final String TYPE_READ_PRODUCT = "AD-readProduct-Intalio";
    public static final String TYPE_READ_PRODUCT_BP_PRICE = "AD-readProductBPPrice-Intalio";
    public static final String TYPE_READ_ORGANISATION = "AD-readOrganization-Intalio";

    public static final String TYPE_CREATE_USER = "AD-createUser-Drupal";
    public static final String TYPE_READ_USER = "AD-readUser-Drupal";
    public static final String TYPE_UPDATE_USER = "AD-updateUser-Drupal";
    public static final String TYPE_DELETE_USER = "AD-deleteUser-Drupal";

    public static final String TYPE_READ_USER_ROLE = "AD-readUserRole-Drupal";
    public static final String TYPE_CREATE_USER_ROLE = "AD-createUserRole-Drupal";
    public static final String TYPE_DELETE_USER_ROLE = "AD-deleteUserRole-Drupal";

    public static final String ADEMPIERE_USER_DEFAULT = "IntalioUser";
    public static final String ADEMPIERE_PASS_DEFAULT = "dJw%U#s5";
    public static final String ADEMPIERE_USER_DRUPAL = "DrupalUser";
    public static final String ADEMPIERE_PASS_DRUPAL = "l8B#2c$p";
    public static final String ADEMPIERE_USER_INTALIO = "IntalioUser";
    public static final String ADEMPIERE_PASS_INTALIO = "dJw%U#s5";
}
