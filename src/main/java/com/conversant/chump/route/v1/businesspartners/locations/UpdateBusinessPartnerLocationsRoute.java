package com.conversant.chump.route.v1.businesspartners.locations;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.BusinessPartnerLocationRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.chump.route.v1.businesspartners.AbstractBusinessPartnersRoute;
import com.conversant.webservice.StandardResponse;
import com.conversant.webservice.UpdateBusinessPartnerLocationRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

import com.conversant.chump.route.v1.businesspartners.locations.CreateBusinessPartnerLocationsRoute;
import static com.conversant.chump.common.RestOperation.HttpMethod.PUT;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_INTALIO;
import static com.conversant.chump.util.Constants.TYPE_UPDATE_BUSINESS_PARTNER_LOCATION;

/**
 * Created by jhill on 20/06/15.
 */
@Component
public class UpdateBusinessPartnerLocationsRoute extends AbstractBusinessPartnersRoute {

    /**
     * Update business partner locations
     */
    public static final ChumpOperation UPDATE = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{businessPartnerSearchKey}/locations/{businessPartnerLocationId}")
                    .requestType(BusinessPartnerLocationRequest.class)
                    .method(PUT)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerBySearchKeyRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_BY_SEARCH_KEY.getUri()),
                    ChumpOperation.pair(CreateBusinessPartnerLocationsRoute.CreateLocationRequestProcessor.INSTANCE, AdempiereRoute.CREATE_LOCATION.getUri()),
                    ChumpOperation.pair(UpdateBusinessPartnerLocationRequestProcessor.INSTANCE, AdempiereRoute.UPDATE_BUSINESS_PARTNER_LOCATION.getUri())))
            .postProcessors(Collections.singletonList(ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Update business partner location request processor
     */
    private static final class UpdateBusinessPartnerLocationRequestProcessor implements Processor {

        public static final Processor INSTANCE = new UpdateBusinessPartnerLocationRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            StandardResponse createLocationResponse = exchange.getIn().getBody(StandardResponse.class);

            BusinessPartnerLocationRequest businessPartnerLocationRequest = exchange.getProperty(BusinessPartnerLocationRequest.class.getName(), BusinessPartnerLocationRequest.class);

            UpdateBusinessPartnerLocationRequest updateBusinessPartnerLocationRequest = new UpdateBusinessPartnerLocationRequest();
            updateBusinessPartnerLocationRequest.setLoginRequest(createLoginRequest(exchange, TYPE_UPDATE_BUSINESS_PARTNER_LOCATION, ADEMPIERE_USER_INTALIO));
            updateBusinessPartnerLocationRequest.setBusinessPartnerLocationId(businessPartnerLocationRequest.getBusinessPartnerLocationId());
            updateBusinessPartnerLocationRequest.setName(businessPartnerLocationRequest.getName());
            updateBusinessPartnerLocationRequest.setLocationId(createLocationResponse.getId());
            updateBusinessPartnerLocationRequest.setPaymentRule(businessPartnerLocationRequest.getPaymentRule());

            if (businessPartnerLocationRequest.getShipAddress() != null)
                updateBusinessPartnerLocationRequest.setShipAddress(businessPartnerLocationRequest.getShipAddress());

            if (businessPartnerLocationRequest.getInvoiceAddress() != null)
                updateBusinessPartnerLocationRequest.setInvoiceAddress(businessPartnerLocationRequest.getInvoiceAddress());

            if (businessPartnerLocationRequest.getPayFromAddress() != null)
                updateBusinessPartnerLocationRequest.setPayFromAddress(businessPartnerLocationRequest.getPayFromAddress());

            if (businessPartnerLocationRequest.getRemitToAddress() != null)
                updateBusinessPartnerLocationRequest.setRemitToAddress(businessPartnerLocationRequest.getRemitToAddress());

            exchange.getIn().setBody(updateBusinessPartnerLocationRequest);
        }
    }
}