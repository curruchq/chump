package com.conversant.chump.processor;

import com.conversant.webservice.ReadBusinessPartnerBySearchKeyRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_DRUPAL;
import static com.conversant.chump.util.Constants.TYPE_READ_BUSINESS_PARTNER;

/**
 * Created by Saren Currie on 15-02-10.
 */
public final class GetBusinessPartnerProcessor implements Processor {

    public static final Processor INSTANCE = new GetBusinessPartnerProcessor();

    @Override
    public void process(Exchange exchange) throws Exception {

        ReadBusinessPartnerBySearchKeyRequest request = new ReadBusinessPartnerBySearchKeyRequest();
        request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_BUSINESS_PARTNER, ADEMPIERE_USER_DRUPAL));
        request.setSearchKey((String) exchange.getIn().getHeader("businessPartnerSearchKey"));

        exchange.getIn().setBody(request);

    }
}
