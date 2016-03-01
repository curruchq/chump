package com.conversant.chump.route.v1.businesspartners;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.BusinessPartnerRadiusAccountsRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.BusinessPartner;
import com.conversant.webservice.ReadBusinessPartnerResponse;
import com.conversant.webservice.ReadRadiusAccountsSearchRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_DRUPAL;
import static com.conversant.chump.util.Constants.TYPE_READ_BUSINESS_PARTNER_RADIUS_ACCOUNTS;

/**
 * Created by trob on 19/02/16.
 */
@Component
public class BusinessPartnerRadiusAccountsRoute extends AbstractBusinessPartnersRoute {

    /**
     * Read business partner radius accounts
     */
    public static final ChumpOperation READ = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{businessPartnerSearchKey}/radius")
                    .requestType(BusinessPartnerRadiusAccountsRequest.class)
                    .method(POST)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerBySearchKeyRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_BY_SEARCH_KEY.getUri()),
                    ChumpOperation.pair(ReadBPRadiusAccountsRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_RADIUS_ACCOUNTS.getUri())))
            .postProcessors(Collections.singletonList(ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Read radius accounts request processor
     */
    private static final class ReadBPRadiusAccountsRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadBPRadiusAccountsRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            BusinessPartner businessPartner = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();

            BusinessPartnerRadiusAccountsRequest businessPartnerRadiusAccountsRequest = exchange.getProperty(BusinessPartnerRadiusAccountsRequest.class.getName(), BusinessPartnerRadiusAccountsRequest.class);


            ReadRadiusAccountsSearchRequest request = new ReadRadiusAccountsSearchRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_BUSINESS_PARTNER_RADIUS_ACCOUNTS, ADEMPIERE_USER_DRUPAL));
            request.setBusinessPartnerId(businessPartner.getBusinessPartnerId());

            //Required fields
            request.setBillingId(businessPartnerRadiusAccountsRequest.getBillingId());
            request.setBillingParty(businessPartnerRadiusAccountsRequest.getBillingParty());
            request.setDateFrom(businessPartnerRadiusAccountsRequest.getDateFrom());
            request.setDateTo(businessPartnerRadiusAccountsRequest.getDateTo());
            request.setOtherParty(businessPartnerRadiusAccountsRequest.getOtherParty());

            exchange.getIn().setBody(request);
        }
    }
}
