package com.conversant.chump.route.v1;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.ReadIndividualSubscriptionRequest;
import com.conversant.chump.model.SubscriptionRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.ReadBusinessPartnerRequestProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.GET;
import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.common.RestOperation.HttpMethod.PUT;
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

    /**
     * Read multiple subscriptions
     */
    public static final ChumpOperation READ = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .method(GET)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBusinessPartnerRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER_BY_SEARCH_KEY.getUri()),
                    ChumpOperation.pair(ReadSubscriptionsRequestProcessor.INSTANCE, AdempiereRoute.READ_SUBSCRIPTIONS.getUri())))
            .postProcessors(
                    Arrays.asList(new StandardResponseRemover("subscription"), ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Read single subscription
     */
    public static final ChumpOperation SINGLE = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .path("/{subscriptionId}")
                    .resource(RESOURCE)
                    .method(GET)
                    .requestType(ReadIndividualSubscriptionRequest.class)
                    .build())
            .trx(false)
            .to(Collections.singletonList(
                    ChumpOperation.pair(ReadSubscriptionRequestProcessor.INSTANCE, AdempiereRoute.READ_SUBSCRIPTION.getUri())))
            .postProcessors(
                    Arrays.asList(new StandardResponseRemover("subscription"), ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Update a subscription
     */
    public static final ChumpOperation UPDATE = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{subscriptionId}")
                    .method(PUT)
                    .requestType(SubscriptionRequest.class)
                    .build())
            .trx(false)
            .to(Collections.singletonList(
                    ChumpOperation.pair(UpdateSubscriptionRequestProcessor.INSTANCE, AdempiereRoute.UPDATE_SUBSCRIPTION.getUri())))
            .build();

    /**
     * Create a subscription
     */
    public static final ChumpOperation CREATE = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .method(POST)
                    .requestType(SubscriptionRequest.class)
                    .build())
            .trx(false)
            .to(Collections.singletonList(
                    ChumpOperation.pair(CreateSubscriptionRequestProcessor.INSTANCE, AdempiereRoute.CREATE_SUBSCRIPTION.getUri())))
            .postProcessors(Collections.singletonList(ApiResponseProcessor.INSTANCE)) // TODO: Needed?
            .build();

    private static final class ReadSubscriptionsRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadSubscriptionsRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            BusinessPartner businessPartner = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();

            ReadSubscriptionsRequest readSubscriptionsRequest = new ReadSubscriptionsRequest();
            readSubscriptionsRequest.setLoginRequest(createLoginRequest(exchange, TYPE_READ_SUBSCRIPTIONS, ADEMPIERE_USER_DRUPAL));
            readSubscriptionsRequest.setBusinessPartnerId(businessPartner.getBusinessPartnerId());

            exchange.getIn().setBody(readSubscriptionsRequest);
        }
    }

    private static final class ReadSubscriptionRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadSubscriptionRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ReadSubscriptionRequest readSubscriptionRequest = new ReadSubscriptionRequest();
            readSubscriptionRequest.setLoginRequest(createLoginRequest(exchange, TYPE_READ_SUBSCRIPTION, ADEMPIERE_USER_DRUPAL));
            readSubscriptionRequest.setSubscriptionId(Integer.parseInt((String) exchange.getIn().getHeader("subscriptionId")));

            exchange.getIn().setBody(readSubscriptionRequest);
        }
    }

    private static final class UpdateSubscriptionRequestProcessor implements Processor {

        public static final Processor INSTANCE = new UpdateSubscriptionRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            SubscriptionRequest request = exchange.getIn().getBody(SubscriptionRequest.class);

            UpdateSubscriptionRequest updateSubscriptionRequest = new UpdateSubscriptionRequest();
            updateSubscriptionRequest.setLoginRequest(createLoginRequest(exchange, TYPE_UPDATE_SUBSCRIPTION, ADEMPIERE_USER_DRUPAL));
            updateSubscriptionRequest.setSubscriptionId(Integer.parseInt((String) exchange.getIn().getHeader("subscriptionId")));
            updateSubscriptionRequest.setName(request.getName());
            updateSubscriptionRequest.setBusinessPartnerId(request.getBusinessPartnerId());
            updateSubscriptionRequest.setBusinessPartnerLocationId(request.getBusinessPartnerLocationId());
            updateSubscriptionRequest.setProductId(request.getProductId());
            updateSubscriptionRequest.setSubscriptionTypeId(request.getSubscriptionTypeId());
            updateSubscriptionRequest.setStartDate(request.getStartDate());
            updateSubscriptionRequest.setRenewalDate(request.getRenewalDate());
            updateSubscriptionRequest.setPaidUntilDate(request.getPaidUntilDate());
            updateSubscriptionRequest.setBillInAdvance(request.isBillInAdvance());
            updateSubscriptionRequest.setQty(BigDecimal.valueOf(request.getQty()));
            updateSubscriptionRequest.setIsDue(request.isDue());
            updateSubscriptionRequest.setUserId(request.getUserId());

            exchange.getIn().setBody(updateSubscriptionRequest);
        }
    }

    private static final class CreateSubscriptionRequestProcessor implements Processor {

        public static final Processor INSTANCE = new CreateSubscriptionRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            SubscriptionRequest subscriptionRequest = exchange.getIn().getBody(SubscriptionRequest.class);

            CreateSubscriptionRequest createSubscriptionRequest = new CreateSubscriptionRequest();
            createSubscriptionRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_SUBSCRIPTION, ADEMPIERE_USER_DRUPAL));
            createSubscriptionRequest.setName(subscriptionRequest.getName());
            createSubscriptionRequest.setBusinessPartnerId(subscriptionRequest.getBusinessPartnerId());
            createSubscriptionRequest.setBusinessPartnerLocationId(subscriptionRequest.getBusinessPartnerLocationId());
            createSubscriptionRequest.setProductId(subscriptionRequest.getProductId());
            createSubscriptionRequest.setSubscriptionTypeId(subscriptionRequest.getSubscriptionTypeId());
            createSubscriptionRequest.setStartDate(subscriptionRequest.getStartDate());
            createSubscriptionRequest.setRenewalDate(subscriptionRequest.getRenewalDate());
            createSubscriptionRequest.setPaidUntilDate(subscriptionRequest.getPaidUntilDate());
            createSubscriptionRequest.setBillInAdvance(subscriptionRequest.isBillInAdvance());
            createSubscriptionRequest.setQty(BigDecimal.valueOf(subscriptionRequest.getQty()));
            createSubscriptionRequest.setIsDue(subscriptionRequest.isDue());
            createSubscriptionRequest.setUserId(subscriptionRequest.getUserId());
            createSubscriptionRequest.setOrgId(subscriptionRequest.getOrgId());

            exchange.getIn().setBody(createSubscriptionRequest);
        }
    }
}
