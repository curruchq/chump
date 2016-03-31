package com.conversant.chump.route.v1.numbers;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.NumberRequest;
import com.conversant.chump.processor.batch.BatchAggregationStrategy;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.EndCallSubscriptionRequest;
import com.conversant.webservice.EndDIDSubscriptionRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.TYPE_END_CALL_SUBSCRIPTION;
import static com.conversant.chump.util.Constants.TYPE_END_DID_SUBSCRIPTION;

/**
 * Created by trobertson on 24/03/16.
 */
@Component(value = "unsubscribeNumberRoute-v1")
public class UnsubscribeNumbersRoute extends AbstractNumbersRoute {

    /**
     * Unsubscribe a number
     */
    public static final ChumpOperation UNSUBSCRIBE_SINGLE = ChumpOperation.builder()
            .uri("direct://unsubscribeNumber-v1")
            .to(Arrays.asList(
                    ChumpOperation.pair(EndCallSubscriptionRequestProcessor.INSTANCE, AdempiereRoute.END_CALL_SUBSCRIPTION.getUri()),
                    ChumpOperation.pair(EndDIDSubscriptionRequestProcessor.INSTANCE, AdempiereRoute.END_DID_SUBSCRIPTION.getUri()).excludable("did")))
            .build();

    /**
     * Unsubscribe batch of numbers
     */
    public static final ChumpOperation BATCH_UNSUBSCRIBE = ChumpOperation.builder()

            // POST /v2/numbers/{number}/unsubscribe
            .rest(RestOperation.builder()
                    .method(POST)
                    .resource(RESOURCE)
                    .path("/unsubscribe")
                    .requestType(NumberRequest.class)
                    .build())
            .trx(false)
            .preProcessors(Collections.singletonList(NumberRequestProcessor.INSTANCE))
            .to(Collections.singletonList(
                    ChumpOperation.single(UNSUBSCRIBE_SINGLE.getUri())
                            .split(new BatchAggregationStrategy(e ->
                                    "Unsubscribe " + e.getProperty(NumberRequest.class.getName(), NumberRequest.class).getNumber()))))
            .build();

    /**
     * Unsubscribe batch of numbers
     */
    public static final ChumpOperation UNSUBSCRIBE = ChumpOperation.builder()

            // POST /v2/numbers/{number}/unsubscribe
            .rest(RestOperation.builder()
                    .method(POST)
                    .resource(RESOURCE)
                    .path("/{number}/unsubscribe")
                    .requestType(NumberRequest.class)
                    .build())
            .trx(false)
            .preProcessors(Collections.singletonList(NumberRequestProcessor.INSTANCE))
            .to(Collections.singletonList(
                    ChumpOperation.single(UNSUBSCRIBE_SINGLE.getUri())
                            .split(new BatchAggregationStrategy(e ->
                                    "Unsubscribe " + e.getProperty(NumberRequest.class.getName(), NumberRequest.class).getNumber()))))
            .build();

    /**
     * end date call subscription request processor
     */
    static final class EndCallSubscriptionRequestProcessor implements Processor {

        public static final Processor INSTANCE = new EndCallSubscriptionRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            NumberRequest request = exchange.getProperty(NumberRequest.class.getName(), NumberRequest.class);

            EndCallSubscriptionRequest callSubscriptionRequest = new EndCallSubscriptionRequest();
            callSubscriptionRequest.setLoginRequest(createLoginRequest(exchange, TYPE_END_CALL_SUBSCRIPTION));
            callSubscriptionRequest.setNumber(request.getNumber());
            callSubscriptionRequest.setBusinessPartnerId(request.getBusinessPartnerId());
            callSubscriptionRequest.setEndDate(request.getEndDate());

            exchange.getIn().setBody(callSubscriptionRequest);
        }
    }

    /**
     * end date did subscription request processor
     */
    static final class EndDIDSubscriptionRequestProcessor implements Processor {

        public static final Processor INSTANCE = new EndDIDSubscriptionRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            NumberRequest request = exchange.getProperty(NumberRequest.class.getName(), NumberRequest.class);

            EndDIDSubscriptionRequest didSubscriptionRequest = new EndDIDSubscriptionRequest();
            didSubscriptionRequest.setLoginRequest(createLoginRequest(exchange, TYPE_END_DID_SUBSCRIPTION));
            didSubscriptionRequest.setNumber(request.getNumber());
            didSubscriptionRequest.setBusinessPartnerId(request.getBusinessPartnerId());
            didSubscriptionRequest.setEndDate(request.getEndDate());

            exchange.getIn().setBody(didSubscriptionRequest);
        }
    }
}
