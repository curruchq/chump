package com.conversant.chump.route.v2.numbers;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.NumberRequest;
import com.conversant.chump.processor.batch.BatchAggregationStrategy;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.CreateCallSubscription2Request;
import com.conversant.webservice.CreateDIDSubscriptionRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.TYPE_CREATE_CALL_SUBSCRIPTION_2;
import static com.conversant.chump.util.Constants.TYPE_CREATE_DID_SUBSCRIPTION;

/**
 * Created by jhill on 23/05/15.
 */
@Component(value = "subscribeNumberRoute-v2")
public class SubscribeNumbersRoute extends AbstractNumbersRoute {

    /**
     * Subscribe a number
     */
    public static final ChumpOperation SUBSCRIBE_SINGLE = ChumpOperation.builder()
            .uri("direct://subscribeNumber-v2")
            .to(Arrays.asList(
                    ChumpOperation.pair(CreateCallSubscription2RequestProcessor.INSTANCE, AdempiereRoute.CREATE_CALL_SUBSCRIPTION_2.getUri()),
                    ChumpOperation.pair(CreateDidSubscriptionRequestProcessor.INSTANCE, AdempiereRoute.CREATE_DID_SUBSCRIPTION.getUri()).excludable("did")))
            .build();

    /**
     * Subscribe batch of numbers
     */
    public static final ChumpOperation BATCH_SUBSCRIBE = ChumpOperation.builder()

            // POST /v2/numbers/{number}/subscribe
            .rest(RestOperation.builder()
                    .method(POST)
                    .resource(RESOURCE)
                    .path("/subscribe")
                    .requestType(NumberRequest.class)
                    .build())
            .trx(false)
            .preProcessors(Collections.singletonList(NumberRequestProcessor.INSTANCE))
            .to(Collections.singletonList(
                    ChumpOperation.single(SUBSCRIBE_SINGLE.getUri())
                            .split(new BatchAggregationStrategy(e ->
                                    "Subscribe " + e.getProperty(NumberRequest.class.getName(), NumberRequest.class).getNumber()))))
            .build();

    /**
     * Subscribe batch of numbers
     */
    public static final ChumpOperation SUBSCRIBE = ChumpOperation.builder()

            // POST /v2/numbers/{number}/subscribe
            .rest(RestOperation.builder()
                    .method(POST)
                    .resource(RESOURCE)
                    .path("/{number}/subscribe")
                    .requestType(NumberRequest.class)
                    .build())
            .trx(false)
            .preProcessors(Collections.singletonList(NumberRequestProcessor.INSTANCE))
            .to(Collections.singletonList(
                    ChumpOperation.single(SUBSCRIBE_SINGLE.getUri())
                            .split(new BatchAggregationStrategy(e ->
                                    "Subscribe " + e.getProperty(NumberRequest.class.getName(), NumberRequest.class).getNumber()))))
            .build();

    /**
     * Create call subscription request processor
     */
    static final class CreateCallSubscription2RequestProcessor implements Processor {

        public static final Processor INSTANCE = new CreateCallSubscription2RequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            NumberRequest request = exchange.getProperty(NumberRequest.class.getName(), NumberRequest.class);

            CreateCallSubscription2Request callSubscriptionRequest = new CreateCallSubscription2Request();
            callSubscriptionRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_CALL_SUBSCRIPTION_2));
            callSubscriptionRequest.setNumber(request.getNumber());
            callSubscriptionRequest.setDomain(request.getRealm());
            callSubscriptionRequest.setBusinessPartnerId(request.getBusinessPartnerId());
            callSubscriptionRequest.setBusinessPartnerLocationId(request.getBusinessPartnerLocationId());
            callSubscriptionRequest.setStartDate(request.getStartDate());

            exchange.getIn().setBody(callSubscriptionRequest);
        }
    }

    /**
     * Create did subscription request processor
     */
    static final class CreateDidSubscriptionRequestProcessor implements Processor {

        public static final Processor INSTANCE = new CreateDidSubscriptionRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            NumberRequest request = exchange.getProperty(NumberRequest.class.getName(), NumberRequest.class);

            CreateDIDSubscriptionRequest didSubscriptionRequest = new CreateDIDSubscriptionRequest();
            didSubscriptionRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_DID_SUBSCRIPTION));
            didSubscriptionRequest.setNumber(request.getNumber());
            didSubscriptionRequest.setBusinessPartnerId(request.getBusinessPartnerId());
            didSubscriptionRequest.setBusinessPartnerLocationId(request.getBusinessPartnerLocationId());
            didSubscriptionRequest.setStartDate(request.getStartDate());
            didSubscriptionRequest.setPaidUntilDate(request.getPaidUntilDate());

            exchange.getIn().setBody(didSubscriptionRequest);
        }
    }
}
