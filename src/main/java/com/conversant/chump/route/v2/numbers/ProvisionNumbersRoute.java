package com.conversant.chump.route.v2.numbers;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.InsertUserPreferenceRequest;
import com.conversant.chump.model.NumberRequest;
import com.conversant.chump.processor.batch.BatchAggregationStrategy;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.chump.route.v1.UserPreferenceRoute;
import com.conversant.webservice.UpdateDIDProductRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.route.v2.numbers.SubscribeNumbersRoute.CreateCallSubscription2RequestProcessor;
import static com.conversant.chump.route.v2.numbers.SubscribeNumbersRoute.CreateDidSubscriptionRequestProcessor;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.TYPE_UPDATE_DID_PRODUCT;

/**
 * Created by jhill on 23/05/15.
 */
@Component(value = "provisionNumberRoute-v2")
public class ProvisionNumbersRoute extends AbstractNumbersRoute {

    private static final String USER_PREF_SUBSCRIBER_ID = "0";
    private static final String USER_PREF_TYPE_NUMERIC = "2";

    /**
     * Provision number by creating subscriptions and user preferences
     */
    public static final ChumpOperation PROVISION_SINGLE = ChumpOperation.builder()
            .uri("direct://provisionNumber-v2")
            .to(Arrays.asList(
                    // TODO: Use SUBSCRIBE.getUri() once implemented joining of trx across top level ChumpOperations
                    ChumpOperation.pair(CreateCallSubscription2RequestProcessor.INSTANCE, AdempiereRoute.CREATE_CALL_SUBSCRIPTION_2.getUri()),
                    ChumpOperation.pair(CreateDidSubscriptionRequestProcessor.INSTANCE, AdempiereRoute.CREATE_DID_SUBSCRIPTION.getUri()).excludable("didSubscription"),
                    ChumpOperation.pair(UpdateDIDProductRequestProcessor.INSTANCE, AdempiereRoute.UPDATE_DID_PRODUCT.getUri()),
                    ChumpOperation.pair(InboundDestinationUserPreferenceRequestProcessor.INSTANCE, UserPreferenceRoute.INSERT.getUri()),
                    ChumpOperation.pair(CallerIdv2UserPreferenceRequestProcessor.INSTANCE, UserPreferenceRoute.INSERT.getUri()),
                    ChumpOperation.pair(AuthorisedCallerIdUserPreferenceRequestProcessor.INSTANCE, UserPreferenceRoute.INSERT.getUri())))
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
     * Update did product request processor
     */
    private static final class UpdateDIDProductRequestProcessor implements Processor {

        public static final Processor INSTANCE = new UpdateDIDProductRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            NumberRequest request = exchange.getProperty(NumberRequest.class.getName(), NumberRequest.class);

            UpdateDIDProductRequest didProductRequest = new UpdateDIDProductRequest();
            didProductRequest.setLoginRequest(createLoginRequest(exchange, TYPE_UPDATE_DID_PRODUCT));
            didProductRequest.setNumber(request.getNumber());
            didProductRequest.setSubscribed(true);

            exchange.getIn().setBody(didProductRequest);
        }
    }

    /**
     * Inbound destination user preference request processor
     */
    private static final class InboundDestinationUserPreferenceRequestProcessor implements Processor {

        public static final Processor INSTANCE = new InboundDestinationUserPreferenceRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            NumberRequest numberRequest = exchange.getProperty(NumberRequest.class.getName(), NumberRequest.class);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(numberRequest.getStartDate().getTime());
            calendar.add(Calendar.YEAR, 20);

            InsertUserPreferenceRequest request = new InsertUserPreferenceRequest();
            request.setUuid(String.valueOf(numberRequest.getBusinessPartnerId()));
            request.setUsername(numberRequest.getNumber());
            request.setDomain("conversant.co.nz");
            request.setAttribute("20116");
            request.setValue("sip:" + numberRequest.getNumber() + "@" + numberRequest.getProxy());
            request.setType(USER_PREF_TYPE_NUMERIC);
            request.setModified(numberRequest.getStartDate());
            request.setDateStart(numberRequest.getStartDate());
            request.setDateEnd(calendar.getTime());
            request.setSubscriberId(USER_PREF_SUBSCRIBER_ID);

            exchange.getIn().setBody(request);
        }
    }

    /**
     * Caller id v2 user preference request processor
     */
    private static final class CallerIdv2UserPreferenceRequestProcessor implements Processor {

        public static final Processor INSTANCE = new CallerIdv2UserPreferenceRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            NumberRequest numberRequest = exchange.getProperty(NumberRequest.class.getName(), NumberRequest.class);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(numberRequest.getStartDate().getTime());
            calendar.add(Calendar.YEAR, 20);

            InsertUserPreferenceRequest request = new InsertUserPreferenceRequest();
            request.setUuid(String.valueOf(numberRequest.getBusinessPartnerId()));
            request.setUsername("");
            request.setDomain(numberRequest.getRealm());
            request.setAttribute("37501");
            request.setValue("sip:+" + numberRequest.getNumber() + "@" + numberRequest.getRealm());
            request.setType(USER_PREF_TYPE_NUMERIC);
            request.setModified(numberRequest.getStartDate());
            request.setDateStart(numberRequest.getStartDate());
            request.setDateEnd(calendar.getTime());
            request.setSubscriberId(USER_PREF_SUBSCRIBER_ID);

            exchange.getIn().setBody(request);
        }
    }

    /**
     * Authorised caller id user preference request processor
     */
    private static final class AuthorisedCallerIdUserPreferenceRequestProcessor implements Processor {

        public static final Processor INSTANCE = new AuthorisedCallerIdUserPreferenceRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            NumberRequest numberRequest = exchange.getProperty(NumberRequest.class.getName(), NumberRequest.class);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(numberRequest.getStartDate().getTime());
            calendar.add(Calendar.YEAR, 20);

            InsertUserPreferenceRequest request = new InsertUserPreferenceRequest();
            request.setUuid(String.valueOf(numberRequest.getBusinessPartnerId()));
            request.setUsername(numberRequest.getNumber());
            request.setDomain(numberRequest.getRealm());
            request.setAttribute("20301");
            request.setValue("sip:+" + numberRequest.getNumber() + "@" + numberRequest.getRealm());
            request.setType(USER_PREF_TYPE_NUMERIC);
            request.setModified(numberRequest.getStartDate());
            request.setDateStart(numberRequest.getStartDate());
            request.setDateEnd(calendar.getTime());
            request.setSubscriberId(USER_PREF_SUBSCRIBER_ID);

            exchange.getIn().setBody(request);
        }
    }
}
