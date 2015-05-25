package com.conversant.chump.route.v1.businesspartners;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.BusinessPartnerRequest;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.BusinessPartner;
import com.conversant.webservice.ReadBusinessPartnerResponse;
import com.conversant.webservice.UpdateBusinessPartnerRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.PUT;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_INTALIO;
import static com.conversant.chump.util.Constants.TYPE_UPDATE_BUSINESS_PARTNER;

/**
 * Created by jhill on 20/06/15.
 */
@Component
public class UpdateBusinessPartnersRoute extends AbstractBusinessPartnersRoute {

    /**
     * Update business partner
     */
    public static final ChumpOperation UPDATE = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{businessPartnerSearchKey}")
                    .requestType(BusinessPartnerRequest.class)
                    .method(PUT)
                    .build())
            .preProcessors(Collections.singletonList(BusinessPartnerRequestProcessor.INSTANCE))
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerBySearchKeyRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_BY_SEARCH_KEY.getUri()),
                    ChumpOperation.pair(UpdateBusinessPartnerRequestProcessor.INSTANCE, AdempiereRoute.UPDATE_BUSINESS_PARTNER.getUri())))
            .build();

    /**
     * Update business partner request processor
     */
    private static final class UpdateBusinessPartnerRequestProcessor implements Processor {

        public static final Processor INSTANCE = new UpdateBusinessPartnerRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            BusinessPartner businessPartner = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();
            BusinessPartnerRequest businessPartnerRequest = exchange.getProperty(BusinessPartnerRequest.class.getName(), BusinessPartnerRequest.class);

            UpdateBusinessPartnerRequest updateBusinessPartnerRequest = new UpdateBusinessPartnerRequest();
            updateBusinessPartnerRequest.setLoginRequest(createLoginRequest(exchange, TYPE_UPDATE_BUSINESS_PARTNER, ADEMPIERE_USER_INTALIO));
            updateBusinessPartnerRequest.setOrgId(businessPartnerRequest.getOrgId());
            updateBusinessPartnerRequest.setBusinessPartnerId(businessPartner.getBusinessPartnerId());
            updateBusinessPartnerRequest.setSearchKey(businessPartnerRequest.getSearchKey());
            updateBusinessPartnerRequest.setName(businessPartnerRequest.getName());
            updateBusinessPartnerRequest.setTaxExempt(businessPartnerRequest.isTaxExempt());
            updateBusinessPartnerRequest.setBusinessPartnerGroupId(businessPartnerRequest.getBusinessPartnerGroupId());

            exchange.getIn().setBody(updateBusinessPartnerRequest);
        }
    }
}