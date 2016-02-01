package com.conversant.chump.route.v1.businesspartners.payments;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.chump.route.v1.businesspartners.AbstractBusinessPartnersRoute;
import com.conversant.webservice.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.GET;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_INTALIO;
import static com.conversant.chump.util.Constants.TYPE_READ_BUSINESS_PARTNER_PAYMENTS;

/**
 * Created by Tate on 27/01/2016.
 */
@Component
public class ReadBusinessPartnerPaymentsRoute extends AbstractBusinessPartnersRoute {

    /**
     * Read business partner payments
     */
    public static final ChumpOperation READ = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{businessPartnerSearchKey}/payments")
                    .method(GET)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerBySearchKeyRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_BY_SEARCH_KEY.getUri()),
                    ChumpOperation.pair(ReadBusinessPartnerPaymentsRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_PAYMENTS.getUri())))
            .postProcessors(Arrays.asList(new StandardResponseRemover("payment"), ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Read business partner payments request processor
     */
    private static final class ReadBusinessPartnerPaymentsRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadBusinessPartnerPaymentsRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            BusinessPartner businessPartner = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();

            ReadPaymentsByBusinessPartnerRequest request = new ReadPaymentsByBusinessPartnerRequest();

            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_BUSINESS_PARTNER_PAYMENTS, ADEMPIERE_USER_INTALIO));
            request.setBusinessPartnerId(businessPartner.getBusinessPartnerId());

            exchange.getIn().setBody(request);
        }
    }
}
