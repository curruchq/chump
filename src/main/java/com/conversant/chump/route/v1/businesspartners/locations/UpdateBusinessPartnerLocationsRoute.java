package com.conversant.chump.route.v1.businesspartners.locations;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.BusinessPartnerLocationRequest;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.chump.route.v1.businesspartners.AbstractBusinessPartnersRoute;
import com.conversant.webservice.UpdateBusinessPartnerLocationRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Collections;

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
            .to(Collections.singletonList(ChumpOperation.pair(UpdateBusinessPartnerLocationRequestProcessor.INSTANCE, AdempiereRoute.UPDATE_BUSINESS_PARTNER_LOCATION.getUri())))
            .build();

    /**
     * Update business partner location request processor
     */
    private static final class UpdateBusinessPartnerLocationRequestProcessor implements Processor {

        public static final Processor INSTANCE = new UpdateBusinessPartnerLocationRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            BusinessPartnerLocationRequest businessPartnerLocationRequest = exchange.getProperty(BusinessPartnerLocationRequest.class.getName(), BusinessPartnerLocationRequest.class);
            businessPartnerLocationRequest.setBusinessPartnerLocationId(Integer.parseInt((String) exchange.getIn().getHeader("businessPartnerLocationId")));

            UpdateBusinessPartnerLocationRequest updateBusinessPartnerLocationRequest = new UpdateBusinessPartnerLocationRequest();
            updateBusinessPartnerLocationRequest.setLoginRequest(createLoginRequest(exchange, TYPE_UPDATE_BUSINESS_PARTNER_LOCATION, ADEMPIERE_USER_INTALIO));
            updateBusinessPartnerLocationRequest.setBusinessPartnerLocationId(businessPartnerLocationRequest.getBusinessPartnerLocationId());
            updateBusinessPartnerLocationRequest.setName(businessPartnerLocationRequest.getName());
            updateBusinessPartnerLocationRequest.setAddress1(businessPartnerLocationRequest.getAddress1());
            updateBusinessPartnerLocationRequest.setAddress2(businessPartnerLocationRequest.getAddress2());
            updateBusinessPartnerLocationRequest.setAddress3(businessPartnerLocationRequest.getAddress3());
            updateBusinessPartnerLocationRequest.setAddress4(businessPartnerLocationRequest.getAddress4());
            updateBusinessPartnerLocationRequest.setCity(businessPartnerLocationRequest.getCity());
            updateBusinessPartnerLocationRequest.setZip(businessPartnerLocationRequest.getZip());
            updateBusinessPartnerLocationRequest.setRegion(businessPartnerLocationRequest.getRegion());
            updateBusinessPartnerLocationRequest.setCountryId(businessPartnerLocationRequest.getCountryId());

            if (businessPartnerLocationRequest.getShipAddress() != null && businessPartnerLocationRequest.getShipAddress())
                updateBusinessPartnerLocationRequest.setShipAddress(businessPartnerLocationRequest.getShipAddress());

            if (businessPartnerLocationRequest.getInvoiceAddress() != null && businessPartnerLocationRequest.getInvoiceAddress())
                updateBusinessPartnerLocationRequest.setInvoiceAddress(businessPartnerLocationRequest.getInvoiceAddress());

            if (businessPartnerLocationRequest.getPayFromAddress() != null && businessPartnerLocationRequest.getPayFromAddress())
                updateBusinessPartnerLocationRequest.setPayFromAddress(businessPartnerLocationRequest.getPayFromAddress());

            if (businessPartnerLocationRequest.getRemitToAddress() != null && businessPartnerLocationRequest.getRemitToAddress())
                updateBusinessPartnerLocationRequest.setRemitToAddress(businessPartnerLocationRequest.getRemitToAddress());

            exchange.getIn().setBody(updateBusinessPartnerLocationRequest);
        }
    }
}