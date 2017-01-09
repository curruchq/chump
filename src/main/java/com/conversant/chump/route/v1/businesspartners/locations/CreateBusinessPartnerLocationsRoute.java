package com.conversant.chump.route.v1.businesspartners.locations;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.BusinessPartnerLocationRequest;
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
import static com.conversant.chump.util.Constants.TYPE_CREATE_BUSINESS_PARTNER_LOCATION;
import static com.conversant.chump.util.Constants.TYPE_CREATE_LOCATION;

/**
 * Created by jhill on 20/06/15.
 */
@Component
public class CreateBusinessPartnerLocationsRoute extends AbstractBusinessPartnersRoute {

    /**
     * Create business partner locations
     */
    public static final ChumpOperation CREATE = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{businessPartnerSearchKey}/locations")
                    .requestType(BusinessPartnerLocationRequest.class)
                    .method(POST)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerBySearchKeyRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_BY_SEARCH_KEY.getUri()),
                    ChumpOperation.pair(CreateLocationRequestProcessor.INSTANCE, AdempiereRoute.CREATE_LOCATION.getUri()),
                    ChumpOperation.pair(CreateBusinessPartnerLocationRequestProcessor.INSTANCE, AdempiereRoute.CREATE_BUSINESS_PARTNER_LOCATION.getUri())))
            .postProcessors(Collections.singletonList(ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Create location request processor
     */
    private static final class CreateLocationRequestProcessor implements Processor {

        public static final Processor INSTANCE = new CreateLocationRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            // Load and store business partner response
            BusinessPartner businessPartner = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();
            exchange.setProperty(BusinessPartner.class.getName(), businessPartner);

            BusinessPartnerLocationRequest businessPartnerLocationRequest = exchange.getProperty(BusinessPartnerLocationRequest.class.getName(), BusinessPartnerLocationRequest.class);

            CreateLocationRequest createLocationRequest = new CreateLocationRequest();
            createLocationRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_LOCATION, ADEMPIERE_USER_INTALIO));
            createLocationRequest.setAddress1(businessPartnerLocationRequest.getAddress1());
            createLocationRequest.setAddress2(businessPartnerLocationRequest.getAddress2());
            createLocationRequest.setAddress3(businessPartnerLocationRequest.getAddress3());
            createLocationRequest.setAddress4(businessPartnerLocationRequest.getAddress4());
            createLocationRequest.setCity(businessPartnerLocationRequest.getCity());
            createLocationRequest.setZip(businessPartnerLocationRequest.getZip());
            createLocationRequest.setRegion(businessPartnerLocationRequest.getRegion());
            createLocationRequest.setCountryId(businessPartnerLocationRequest.getCountryId());

            exchange.getIn().setBody(createLocationRequest);
        }
    }

    /**
     * Create business partner location request processor
     */
    private static final class CreateBusinessPartnerLocationRequestProcessor implements Processor {

        public static final Processor INSTANCE = new CreateBusinessPartnerLocationRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            StandardResponse createLocationResponse = exchange.getIn().getBody(StandardResponse.class);

            BusinessPartner businessPartner = exchange.getProperty(BusinessPartner.class.getName(), BusinessPartner.class);
            BusinessPartnerLocationRequest businessPartnerLocationRequest = exchange.getProperty(BusinessPartnerLocationRequest.class.getName(), BusinessPartnerLocationRequest.class);
            businessPartnerLocationRequest.setBusinessPartnerId(businessPartner.getBusinessPartnerId());

            CreateBusinessPartnerLocationRequest createBusinessPartnerLocationRequest = new CreateBusinessPartnerLocationRequest();
            createBusinessPartnerLocationRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_BUSINESS_PARTNER_LOCATION, ADEMPIERE_USER_INTALIO));
            createBusinessPartnerLocationRequest.setName(businessPartnerLocationRequest.getName());
            createBusinessPartnerLocationRequest.setBusinessPartnerId(businessPartnerLocationRequest.getBusinessPartnerId());
            createBusinessPartnerLocationRequest.setLocationId(createLocationResponse.getId());
            createBusinessPartnerLocationRequest.setPaymentRule(businessPartnerLocationRequest.getPaymentRule());

            if (businessPartnerLocationRequest.getShipAddress() != null && businessPartnerLocationRequest.getShipAddress())
                createBusinessPartnerLocationRequest.setShipAddress(businessPartnerLocationRequest.getShipAddress());

            if (businessPartnerLocationRequest.getInvoiceAddress() != null && businessPartnerLocationRequest.getInvoiceAddress())
                createBusinessPartnerLocationRequest.setInvoiceAddress(businessPartnerLocationRequest.getInvoiceAddress());

            if (businessPartnerLocationRequest.getPayFromAddress() != null && businessPartnerLocationRequest.getPayFromAddress())
                createBusinessPartnerLocationRequest.setPayFromAddress(businessPartnerLocationRequest.getPayFromAddress());

            if (businessPartnerLocationRequest.getRemitToAddress() != null && businessPartnerLocationRequest.getRemitToAddress())
                createBusinessPartnerLocationRequest.setRemitToAddress(businessPartnerLocationRequest.getRemitToAddress());

            exchange.getIn().setBody(createBusinessPartnerLocationRequest);
        }
    }
}
