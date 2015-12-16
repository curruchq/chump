package com.conversant.chump.route.v1.businesspartners.subscriptions;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.chump.route.v1.businesspartners.AbstractBusinessPartnersRoute;
import com.conversant.webservice.BusinessPartner;
import com.conversant.webservice.ReadSubscriptionsRequest;
import com.conversant.webservice.ReadBusinessPartnerResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.conversant.chump.common.RestOperation.HttpMethod.GET;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_DRUPAL;
import static com.conversant.chump.util.Constants.TYPE_READ_BUSINESS_PARTNER_SUBSCRIPTIONS;

/**
 * Created by Tate on 9/12/2015.
 */
@Component
public class ReadBusinessPartnerSubscriptionsRoute extends AbstractBusinessPartnersRoute {

    /**
     * Read business partner subscriptions
     */
    public static final ChumpOperation READ = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{businessPartnerSearchKey}/subscriptions")
                    .method(GET)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerBySearchKeyRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_BY_SEARCH_KEY.getUri()),
                    ChumpOperation.pair(ReadBusinessPartnerSubscriptionsRequestProcessor.INSTANCE, AdempiereRoute.READ_SUBSCRIPTIONS.getUri())))
            .postProcessors(Arrays.asList(new StandardResponseRemover("subscription"), ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Read business partner subscriptions request processor
     */
    private static final class ReadBusinessPartnerSubscriptionsRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadBusinessPartnerSubscriptionsRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            BusinessPartner businessPartner = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();

            ReadSubscriptionsRequest request = new ReadSubscriptionsRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_BUSINESS_PARTNER_SUBSCRIPTIONS, ADEMPIERE_USER_DRUPAL));
            request.setBusinessPartnerId(businessPartner.getBusinessPartnerId());

            exchange.getIn().setBody(request);
        }
    }

}