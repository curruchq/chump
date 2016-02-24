package com.conversant.chump.route.v1.businesspartners.bankaccount;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.BusinessPartnerBankAccountRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.chump.route.v1.businesspartners.AbstractBusinessPartnersRoute;
import com.conversant.webservice.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_INTALIO;
import static com.conversant.chump.util.Constants.TYPE_CREATE_BUSINESS_PARTNER_BANK_ACCOUNT;

/**
 * Created by Tate on 25/01/2016.
 */
@Component
public class CreateBusinessPartnerBankAccountRoute extends AbstractBusinessPartnersRoute {
    /**
     * Create business partner bank accounts
     */
    public static final ChumpOperation CREATE = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{businessPartnerSearchKey}/bankaccount")
                    .requestType(BusinessPartnerBankAccountRequest.class)
                    .method(POST)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerBySearchKeyRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_BY_SEARCH_KEY.getUri()),
                    ChumpOperation.pair(CreateBusinessPartnerBankAccountRequestProcessor.INSTANCE, AdempiereRoute.CREATE_BUSINESS_PARTNER_BANK_ACCOUNT.getUri())))
            .postProcessors(Collections.singletonList(ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Create business partner bank account request processor
     */
    private static final class CreateBusinessPartnerBankAccountRequestProcessor implements Processor {

        public static final Processor INSTANCE = new CreateBusinessPartnerBankAccountRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            // Load and store business partner response
            BusinessPartner businessPartner = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();

            BusinessPartnerBankAccountRequest businessPartnerBankAccountRequest = exchange.getProperty(BusinessPartnerBankAccountRequest.class.getName(), BusinessPartnerBankAccountRequest.class);
            CreateBPBankAccountRequest createBPBankAccountRequest = new CreateBPBankAccountRequest();

            createBPBankAccountRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_BUSINESS_PARTNER_BANK_ACCOUNT, ADEMPIERE_USER_INTALIO));
            createBPBankAccountRequest.setBusinessPartnerId(businessPartner.getBusinessPartnerId());

            //Required fields
            createBPBankAccountRequest.setUserId(businessPartnerBankAccountRequest.getUserId());
            createBPBankAccountRequest.setAccountName(businessPartnerBankAccountRequest.getAccountName());
            createBPBankAccountRequest.setLocationId(businessPartnerBankAccountRequest.getLocationId());

            exchange.getIn().setBody(createBPBankAccountRequest);
        }
    }
}