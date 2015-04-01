package com.conversant.chump.route.v1;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.ReadIndividualSubscriptionRequest;
import com.conversant.chump.model.ReadSubscriptionsByBusinessPartnerRequest;
import com.conversant.chump.model.UpdateSubscriptionRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.ReadBusinessPartnerRequestProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.BusinessPartner;
import com.conversant.webservice.ReadBusinessPartnerResponse;
import com.conversant.webservice.ReadSubscriptionRequest;
import com.conversant.webservice.ReadSubscriptionsRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.conversant.chump.common.RestOperation.HttpMethod.GET;
import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.*;

/**
 * Defines a rest endpoint to get subscriptions by business partner from Adempiere.
 * <p/>
 * Created by Saren Currie on 15-02-10.
 */
@Component
public final class SubscriptionRoute implements ChumpRoute {
    private static final String RESOURCE = "/v1/subscriptions";

    public static final ChumpOperation READ = ChumpOperation.builder()
            .trx(false)
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .method(GET)
                    .requestType(ReadSubscriptionsByBusinessPartnerRequest.class)
                    .build())
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER.getUri()),
                    ChumpOperation.pair(ReadSubscriptionsRequestProcessor.INSTANCE, AdempiereRoute.READ_SUBSCRIPTIONS.getUri())
            ))
            .postProcessors(
                    Arrays.asList(new StandardResponseRemover("subscription"), ApiResponseProcessor.INSTANCE))
            .build();

    public static final ChumpOperation SINGLE = ChumpOperation.builder()
            .trx(false)
            .rest(RestOperation.builder()
                    .path("/{subscriptionId}")
                    .resource(RESOURCE)
                    .method(GET)
                    .requestType(ReadIndividualSubscriptionRequest.class)
                    .build())
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadSubscriptionRequestProcessor.INSTANCE, AdempiereRoute.READ_SUBSCRIPTION.getUri())
            ))
            .postProcessors(
                    Arrays.asList(new StandardResponseRemover("subscription"), ApiResponseProcessor.INSTANCE))
            .build();

    public static final ChumpOperation UPDATE = ChumpOperation.builder()
            .trx(false)
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{subscriptionId}")
                    .method(POST)
                    .requestType(UpdateSubscriptionRequest.class)
                    .build()
            ).to(Arrays.asList(
                    //TODO
            ))
            .build();

    private static final class ReadSubscriptionsRequestProcessor implements Processor {
        public static final Processor INSTANCE = new ReadSubscriptionsRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {
            BusinessPartner bp = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();

            ReadSubscriptionsRequest request = new ReadSubscriptionsRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_SUBSCRIPTIONS, ADEMPIERE_USER_DRUPAL));
            request.setBusinessPartnerId(bp.getBusinessPartnerId());

            exchange.getIn().setBody(request);
        }
    }

    private static final class ReadSubscriptionRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadSubscriptionRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {
            ReadSubscriptionRequest request = new ReadSubscriptionRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_SUBSCRIPTION, ADEMPIERE_USER_DRUPAL));
            request.setSubscriptionId(Integer.parseInt((String) exchange.getIn().getHeader("subscriptionId")));

            exchange.getIn().setBody(request);
        }
    }
}
