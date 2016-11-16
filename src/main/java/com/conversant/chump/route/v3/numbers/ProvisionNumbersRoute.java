package com.conversant.chump.route.v3.numbers;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.NumberRequest;
import com.conversant.chump.processor.batch.BatchAggregationStrategy;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.chump.route.v1.UserPreferenceRoute;
import com.conversant.webservice.CreateSubscriptionRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.route.v2.numbers.SubscribeNumbersRoute.CreateCallSubscription2RequestProcessor;
import static com.conversant.chump.route.v2.numbers.SubscribeNumbersRoute.CreateDidSubscriptionRequestProcessor;
import static com.conversant.chump.route.v2.numbers.ProvisionNumbersRoute.UpdateDIDProductRequestProcessor;
import static com.conversant.chump.route.v2.numbers.ProvisionNumbersRoute.InboundDestinationUserPreferenceRequestProcessor;
import static com.conversant.chump.route.v2.numbers.ProvisionNumbersRoute.CallerIdv2UserPreferenceRequestProcessor;
import static com.conversant.chump.route.v2.numbers.ProvisionNumbersRoute.AuthorisedCallerIdUserPreferenceRequestProcessor;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.TYPE_CREATE_SUBSCRIPTION;

/**
 * Created by Tate on 17/11/2016.
 */
@Component(value = "provisionNumberRoute-v3")
public class ProvisionNumbersRoute extends AbstractNumbersRoute {

    /**
     * Provision number by creating subscriptions and user preferences
     */
    public static final ChumpOperation PROVISION_SINGLE = ChumpOperation.builder()
            .uri("direct://provisionNumber-v3")
            .to(Arrays.asList(
                    // TODO: Use SUBSCRIBE.getUri() once implemented joining of trx across top level ChumpOperations
                    ChumpOperation.pair(CreateCallSubscription2RequestProcessor.INSTANCE, AdempiereRoute.CREATE_CALL_SUBSCRIPTION_2.getUri()).excludable("call"),
                    ChumpOperation.pair(CreateDidSubscriptionRequestProcessor.INSTANCE, AdempiereRoute.CREATE_DID_SUBSCRIPTION.getUri()).excludable("did"),
                    ChumpOperation.pair(UpdateDIDProductRequestProcessor.INSTANCE, AdempiereRoute.UPDATE_DID_PRODUCT.getUri()).excludable("did"),
                    ChumpOperation.pair(InboundDestinationUserPreferenceRequestProcessor.INSTANCE, UserPreferenceRoute.INSERT.getUri()).excludable("inbound"),
                    ChumpOperation.pair(CallerIdv2UserPreferenceRequestProcessor.INSTANCE, UserPreferenceRoute.INSERT_IF_NOT_EXISTS.getUri()),
                    ChumpOperation.pair(AuthorisedCallerIdUserPreferenceRequestProcessor.INSTANCE, UserPreferenceRoute.INSERT.getUri()),
                    ChumpOperation.pair(CreateNumberPortSubscriptionRequestProcessor.INSTANCE, AdempiereRoute.CREATE_SUBSCRIPTION.getUri())))
            .build();

    /**
     * Provision batch of numbers by creating subscriptions and user preferences
     */
    public static final ChumpOperation PROVISION_BATCH = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .method(POST)
                    .resource(RESOURCE)
                    .path("/provision")
                    .requestType(NumberRequest.class)
                    .build())
            .trx(false)
            .preProcessors(Collections.singletonList(NumberRequestProcessor.INSTANCE))
            .to(Collections.singletonList(
                    ChumpOperation.single(PROVISION_SINGLE.getUri())
                            .split(new BatchAggregationStrategy(e ->
                                    "Provision " + e.getProperty(NumberRequest.class.getName(), NumberRequest.class).getNumber()))))
            .build();

    /**
     * Provision batch of numbers by creating subscriptions and user preferences
     */
    public static final ChumpOperation PROVISION = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .method(POST)
                    .resource(RESOURCE)
                    .path("/{number}/provision")
                    .requestType(NumberRequest.class)
                    .build())
            .trx(false)
            .preProcessors(Collections.singletonList(NumberRequestProcessor.INSTANCE))
            .to(Collections.singletonList(
                    ChumpOperation.single(PROVISION_SINGLE.getUri())
                            .split(new BatchAggregationStrategy(e ->
                                    "Provision " + e.getProperty(NumberRequest.class.getName(), NumberRequest.class).getNumber()))))
            .build();

    /**
     * Create did subscription request processor
     */
    static final class CreateNumberPortSubscriptionRequestProcessor implements Processor {

        public static final Processor INSTANCE = new CreateNumberPortSubscriptionRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            NumberRequest request = exchange.getProperty(NumberRequest.class.getName(), NumberRequest.class);

            CreateSubscriptionRequest createSubscriptionRequest = new CreateSubscriptionRequest();
            createSubscriptionRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_SUBSCRIPTION));
            createSubscriptionRequest.setBusinessPartnerId(request.getBusinessPartnerId());
            createSubscriptionRequest.setBusinessPartnerLocationId(request.getBusinessPartnerLocationId());
            createSubscriptionRequest.setName(request.getNumber());
            createSubscriptionRequest.setProductId(1000089);
            createSubscriptionRequest.setSubscriptionTypeId(1000005);
            createSubscriptionRequest.setStartDate(request.getStartDate());
            createSubscriptionRequest.setPaidUntilDate(request.getPaidUntilDate());
            createSubscriptionRequest.setRenewalDate(request.getPaidUntilDate());
            createSubscriptionRequest.setBillInAdvance(true);
            createSubscriptionRequest.setQty(BigDecimal.valueOf(1));
            createSubscriptionRequest.setUserId(0);
            createSubscriptionRequest.setOrgId(request.getOrgId());

            exchange.getIn().setBody(createSubscriptionRequest);
        }
    }
}
