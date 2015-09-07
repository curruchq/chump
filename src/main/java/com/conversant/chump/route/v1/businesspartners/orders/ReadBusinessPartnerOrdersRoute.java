package com.conversant.chump.route.v1.businesspartners.orders;

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
import static com.conversant.chump.util.Constants.TYPE_READ_BUSINESS_PARTNER_ORDERS;

/**
 * Created by jhill on 7/09/15.
 */
@Component
public class ReadBusinessPartnerOrdersRoute extends AbstractBusinessPartnersRoute {

    /**
     * Read business partner orders
     */
    public static final ChumpOperation READ = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{businessPartnerSearchKey}/orders")
                    .method(GET)
                    .build())
            .trx(false)
            .to(Collections.singletonList(ChumpOperation.pair(ReadBusinessPartnerOrdersRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_ORDERS.getUri())))
            .postProcessors(Arrays.asList(new StandardResponseRemover("order"), ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Read business partner users request processor
     */
    private static final class ReadBusinessPartnerOrdersRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadBusinessPartnerOrdersRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ReadOrderByBusinessPartnerSearchKeyRequest request = new ReadOrderByBusinessPartnerSearchKeyRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_BUSINESS_PARTNER_ORDERS, ADEMPIERE_USER_INTALIO));
            request.setSearchKey((String) exchange.getIn().getHeader("businessPartnerSearchKey"));

            exchange.getIn().setBody(request);
        }
    }
}