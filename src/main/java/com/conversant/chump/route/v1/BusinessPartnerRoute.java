package com.conversant.chump.route.v1;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.ReadBusinessPartnerRequestProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.BusinessPartner;
import com.conversant.webservice.ReadBPLocationRequest;
import com.conversant.webservice.ReadBusinessPartnerBySearchKeyResponse;
import com.conversant.webservice.ReadBusinessPartnerResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_DRUPAL;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_INTALIO;
import static com.conversant.chump.util.Constants.TYPE_READ_BUSINESS_PARTNER_LOCATION;

/**
 * Created by Saren Currie on 2015-04-17.
 */
@Component
public class BusinessPartnerRoute implements ChumpRoute {

    private static final String RESOURCE = "/v1/businesspartners";

    public static final ChumpOperation READ = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{businessPartnerSearchKey}/locations")
                    .method(RestOperation.HttpMethod.GET)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER.getUri()),
                    ChumpOperation.pair(ReadBusinessPartnerLocationRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_LOCATION.getUri())))
            .postProcessors(Arrays.asList(
                    new StandardResponseRemover("bpLocation"), ApiResponseProcessor.INSTANCE))
            .build();

    private static final class ReadBusinessPartnerLocationRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadBusinessPartnerLocationRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {
            BusinessPartner bp = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();

            ReadBPLocationRequest request = new ReadBPLocationRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_BUSINESS_PARTNER_LOCATION, ADEMPIERE_USER_DRUPAL));
            request.setBusinessPartnerId(bp.getBusinessPartnerId());

            exchange.getIn().setBody(request);
        }
    }
}
