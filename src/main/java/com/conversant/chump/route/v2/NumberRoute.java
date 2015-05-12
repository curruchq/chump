package com.conversant.chump.route.v2;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.CreateNumberRequest;
import com.conversant.chump.model.InsertUserPreferenceRequest;
import com.conversant.chump.model.NumberRequest;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.chump.route.v1.UserPreferenceRoute;
import com.conversant.webservice.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.*;

/**
 * Number related endpoints and operations.
 *
 * @author jhill
 */
@Component(value = "numberRoute-v2")
public class NumberRoute implements ChumpRoute {

    private static final String USER_PREF_SUBSCRIBER_ID = "0";
    private static final String USER_PREF_TYPE_NUMERIC = "2";

    /** Base resource */
    private static final String RESOURCE = "/v2/numbers";

    /** Create calling and DID products */
    public static final ChumpOperation CREATE = ChumpOperation.builder()

            // POST /v2/numbers
            .rest(RestOperation.builder()
                    .method(POST)
                    .resource(RESOURCE)
                    .requestType(CreateNumberRequest.class)
                    .build())
            .to(Arrays.asList(
                    ChumpOperation.pair(CreateCallProduct2RequestProcessor.INSTANCE, AdempiereRoute.CREATE_CALL_PRODUCT_2.getUri()),
                    ChumpOperation.pair(CreateDidProductRequestProcessor.INSTANCE, AdempiereRoute.CREATE_DID_PRODUCT.getUri()).excludable("didProduct")))
            .build();

    private static final class CreateCallProduct2RequestProcessor implements Processor {

        public static final Processor INSTANCE = new CreateCallProduct2RequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            CreateNumberRequest request = exchange.getProperty(CreateNumberRequest.class.getName(), CreateNumberRequest.class);

            CreateCallProductRequest callProductRequest = new CreateCallProductRequest();
            callProductRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_CALL_PRODUCT_2));
            callProductRequest.setDomain(request.getDomain());
            callProductRequest.setNumber(request.getNumber());
            callProductRequest.setPricelistVersionId(request.getPriceListVersionId());

            exchange.getIn().setBody(callProductRequest);
        }
    }

    private static final class CreateDidProductRequestProcessor implements Processor {

        public static final Processor INSTANCE = new CreateDidProductRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            CreateNumberRequest request = exchange.getProperty(CreateNumberRequest.class.getName(), CreateNumberRequest.class);

            CreateDIDProductRequest didProductRequest = new CreateDIDProductRequest();
            didProductRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_DID_PRODUCT));
            didProductRequest.setNumber(request.getNumber());
            didProductRequest.setCountryId(request.getCountryId());
            didProductRequest.setCountryCode(request.getCountryCode());
            didProductRequest.setAreaCode(request.getAreaCode());
            didProductRequest.setAreaCodeDescription(request.getAreaCodeDescription());
            didProductRequest.setFreeMinutes(request.getFreeMinutes());
            didProductRequest.setPerMinuteCharge(request.getPerMinuteCharge());
            didProductRequest.setBusinessPartnerId(request.getBusinessPartnerId());
            didProductRequest.setSetupCost(request.getSetupCost());
            didProductRequest.setMonthlyCharge(request.getMonthlyCharge());
            didProductRequest.setCurrencyId(request.getCurrencyId());
            didProductRequest.setPricelistVersionId(request.getPriceListVersionId());

            exchange.getIn().setBody(didProductRequest);
        }
    }

    /** Subscribe a number */
    public static final ChumpOperation SUBSCRIBE = ChumpOperation.builder()

            // POST /v2/numbers/{number}/subscribe
            .rest(RestOperation.builder()
                    .method(POST)
                    .resource(RESOURCE)
                    .path("/{number}/subscribe")
                    .requestType(NumberRequest.class)
                    .build())
            .preProcessors(Collections.singletonList(NumberRequestProcessor.INSTANCE))
            .to(Arrays.asList(
                    ChumpOperation.pair(CreateCallSubscription2RequestProcessor.INSTANCE, AdempiereRoute.CREATE_CALL_SUBSCRIPTION_2.getUri()),
                    ChumpOperation.pair(CreateDidSubscriptionRequestProcessor.INSTANCE, AdempiereRoute.CREATE_DID_SUBSCRIPTION.getUri()).excludable("didSubscription")))
            .build();

    /** Provision number by creating subscriptions and user preferences */
    public static final ChumpOperation PROVISION = ChumpOperation.builder()

            // POST /v2/numbers/{number}/provision
            .rest(RestOperation.builder()
                    .method(POST)
                    .resource(RESOURCE)
                    .path("/{number}/provision")
                    .requestType(NumberRequest.class)
                    .build())
            .preProcessors(Collections.singletonList(NumberRequestProcessor.INSTANCE))
            .to(Arrays.asList(
                    // TODO: Use SUBSCRIBE.getUri() once implemented joining of trx across top level ChumpOperations
                    ChumpOperation.pair(CreateCallSubscription2RequestProcessor.INSTANCE, AdempiereRoute.CREATE_CALL_SUBSCRIPTION_2.getUri()),
                    ChumpOperation.pair(CreateDidSubscriptionRequestProcessor.INSTANCE, AdempiereRoute.CREATE_DID_SUBSCRIPTION.getUri()).excludable("didSubscription"),
                    ChumpOperation.pair(UpdateDIDProductRequestProcessor.INSTANCE, AdempiereRoute.UPDATE_DID_PRODUCT.getUri()),
                    ChumpOperation.pair(InboundDestinationUserPreference.INSTANCE, UserPreferenceRoute.INSERT.getUri()),
                    ChumpOperation.pair(CallerIdv2UserPreference.INSTANCE, UserPreferenceRoute.INSERT.getUri()),
                    ChumpOperation.pair(AuthorisedCallerIdUserPreference.INSTANCE, UserPreferenceRoute.INSERT.getUri())))
            .build();

    private static final class NumberRequestProcessor implements Processor {

        public static final Processor INSTANCE = new NumberRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            // TODO: How get into request POJO automatically? Generic processor?
            NumberRequest request = exchange.getProperty(NumberRequest.class.getName(), NumberRequest.class);
            if (request != null && request.getNumber() == null)
                request.setNumber((String) exchange.getIn().getHeader("number"));
        }
    }

    private static final class CreateCallSubscription2RequestProcessor implements Processor {

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

    private static final class CreateDidSubscriptionRequestProcessor implements Processor {

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

    private static final class InboundDestinationUserPreference implements Processor {

        public static final Processor INSTANCE = new InboundDestinationUserPreference();

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

    private static final class CallerIdv2UserPreference implements Processor {

        public static final Processor INSTANCE = new CallerIdv2UserPreference();

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

    private static final class AuthorisedCallerIdUserPreference implements Processor {

        public static final Processor INSTANCE = new AuthorisedCallerIdUserPreference();

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
