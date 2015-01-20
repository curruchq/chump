package com.conversant.chump.route;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.CreateNumberRequest;
import com.conversant.chump.model.InsertUserPreferenceRequest;
import com.conversant.chump.model.ProvisionNumberRequest;
import com.conversant.webservice.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Calendar;

import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.*;

/**
 * Created by jhill on 31/12/14.
 */
@Component
public class NumberRoute implements ChumpRoute {

    private static final String RESOURCE = "/v1/numbers";

    public static final ChumpOperation CREATE = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .method(POST)
                    .resource(RESOURCE)
                    .requestType(CreateNumberRequest.class)
                    .build())
            .to(Arrays.asList(
                    ChumpOperation.pair(CreateCallProductRequestProcessor.INSTANCE, AdempiereRoute.CREATE_CALL_PRODUCT.getUri()),
                    ChumpOperation.pair(CreateDidProductRequestProcessor.INSTANCE, AdempiereRoute.CREATE_DID_PRODUCT.getUri())))
            .build();

    public static final ChumpOperation PROVISION = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .method(POST)
                    .resource(RESOURCE)
                    .path("/{number}/provision")
                    .requestType(ProvisionNumberRequest.class)
                    .build())
            .preProcessors(Arrays.asList(
                    // TODO: Can remove once fix header vs path param
                    ProvisionNumberRequestProcessor.INSTANCE))
            .to(Arrays.asList(
                    ChumpOperation.pair(CreateCallSubscriptionRequestProcessor.INSTANCE, AdempiereRoute.CREATE_CALL_SUBSCRIPTION.getUri()),
                    ChumpOperation.pair(CreateDidSubscriptionRequestProcessor.INSTANCE, AdempiereRoute.CREATE_DID_SUBSCRIPTION.getUri()),
                    ChumpOperation.pair(CreateNumberPortSubscriptionRequestProcessor.INSTANCE, AdempiereRoute.CREATE_NUMBER_PORT_SUBSCRIPTION.getUri()),
                    ChumpOperation.pair(UpdateDIDProductRequestProcessor.INSTANCE, AdempiereRoute.UPDATE_DID_PRODUCT.getUri()),
                    ChumpOperation.pair(InboundDestinationUserPreference.INSTANCE, UserPreferenceRoute.INSERT.getUri()),
                    ChumpOperation.pair(CallerIdv1UserPreference.INSTANCE, UserPreferenceRoute.INSERT.getUri()),
                    ChumpOperation.pair(CallerIdv2UserPreference.INSTANCE, UserPreferenceRoute.INSERT.getUri()),
                    ChumpOperation.pair(AuthorisedCallerIdUserPreference.INSTANCE, UserPreferenceRoute.INSERT.getUri())))
            .build();

    private static final class CreateCallProductRequestProcessor implements Processor {

        public static final Processor INSTANCE = new CreateCallProductRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            CreateNumberRequest request = exchange.getProperty(CreateNumberRequest.class.getName(), CreateNumberRequest.class);

            CreateCallProductRequest callProductRequest = new CreateCallProductRequest();
            callProductRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_CALL_PRODUCT));
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

    private static final class ProvisionNumberRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ProvisionNumberRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            // TODO: How get into request POJO automatically? Generic processor?
            ProvisionNumberRequest request = exchange.getIn().getBody(ProvisionNumberRequest.class);
            if (request.getNumber() == null)
                request.setNumber((String) exchange.getIn().getHeader("number"));
        }
    }

    private static final class CreateCallSubscriptionRequestProcessor implements Processor {

        public static final Processor INSTANCE = new CreateCallSubscriptionRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ProvisionNumberRequest request = exchange.getProperty(ProvisionNumberRequest.class.getName(), ProvisionNumberRequest.class);

            CreateCallSubscriptionRequest callSubscriptionRequest = new CreateCallSubscriptionRequest();
            callSubscriptionRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_CALL_SUBSCRIPTION));
            callSubscriptionRequest.setNumber(request.getNumber());
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

            ProvisionNumberRequest request = exchange.getProperty(ProvisionNumberRequest.class.getName(), ProvisionNumberRequest.class);

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

    private static final class CreateNumberPortSubscriptionRequestProcessor implements Processor {

        public static final Processor INSTANCE = new CreateNumberPortSubscriptionRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ProvisionNumberRequest request = exchange.getProperty(ProvisionNumberRequest.class.getName(), ProvisionNumberRequest.class);

            CreateNumberPortSubscriptionRequest numberPortSubscriptionRequest = new CreateNumberPortSubscriptionRequest();
            numberPortSubscriptionRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_NUMBER_PORT_SUBSCRIPTION));
            numberPortSubscriptionRequest.setNumber(request.getNumber());
            numberPortSubscriptionRequest.setBusinessPartnerId(request.getBusinessPartnerId());
            numberPortSubscriptionRequest.setBusinessPartnerLocationId(request.getBusinessPartnerLocationId());

            exchange.getIn().setBody(numberPortSubscriptionRequest);
        }
    }

    private static final class UpdateDIDProductRequestProcessor implements Processor {

        public static final Processor INSTANCE = new UpdateDIDProductRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ProvisionNumberRequest request = exchange.getProperty(ProvisionNumberRequest.class.getName(), ProvisionNumberRequest.class);

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

            ProvisionNumberRequest provisionNumberRequest = exchange.getProperty(ProvisionNumberRequest.class.getName(), ProvisionNumberRequest.class);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(provisionNumberRequest.getStartDate().getTime());
            calendar.add(Calendar.YEAR, 20);

            InsertUserPreferenceRequest request = new InsertUserPreferenceRequest();
            request.setUuid(String.valueOf(provisionNumberRequest.getBusinessPartnerId()));
            request.setUsername(provisionNumberRequest.getNumber());
            request.setDomain("conversant.co.nz");
            request.setAttribute("20116");
            request.setValue("sip:" + provisionNumberRequest.getNumber() + "@" + provisionNumberRequest.getProxy());
            request.setType("2"); // TODO: Constant, numeric type
            request.setModified(provisionNumberRequest.getStartDate());
            request.setDateStart(provisionNumberRequest.getStartDate());
            request.setDateEnd(calendar.getTime());
            request.setSubscriberId("999"); // TODO: Constant? Default?

            exchange.getIn().setBody(request);
        }
    }

    private static final class CallerIdv1UserPreference implements Processor {

        public static final Processor INSTANCE = new CallerIdv1UserPreference();

        @Override
        public void process(Exchange exchange) throws Exception {

            ProvisionNumberRequest provisionNumberRequest = exchange.getProperty(ProvisionNumberRequest.class.getName(), ProvisionNumberRequest.class);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(provisionNumberRequest.getStartDate().getTime());
            calendar.add(Calendar.YEAR, 20);

            InsertUserPreferenceRequest request = new InsertUserPreferenceRequest();
            request.setUuid(String.valueOf(provisionNumberRequest.getBusinessPartnerId()));
            request.setUsername(provisionNumberRequest.getNumber());
            request.setDomain("conversant.co.nz");
            request.setAttribute("37501");
            request.setValue("sip:" + provisionNumberRequest.getNumber() + "@conversant.co.nz");
            request.setType("2"); // TODO: Constant, numeric type
            request.setModified(provisionNumberRequest.getStartDate());
            request.setDateStart(provisionNumberRequest.getStartDate());
            request.setDateEnd(calendar.getTime());
            request.setSubscriberId("999"); // TODO: Constant? Default?

            exchange.getIn().setBody(request);
        }
    }

    private static final class CallerIdv2UserPreference implements Processor {

        public static final Processor INSTANCE = new CallerIdv2UserPreference();

        @Override
        public void process(Exchange exchange) throws Exception {

            ProvisionNumberRequest provisionNumberRequest = exchange.getProperty(ProvisionNumberRequest.class.getName(), ProvisionNumberRequest.class);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(provisionNumberRequest.getStartDate().getTime());
            calendar.add(Calendar.YEAR, 20);

            InsertUserPreferenceRequest request = new InsertUserPreferenceRequest();
            request.setUuid(String.valueOf(provisionNumberRequest.getBusinessPartnerId()));
            request.setUsername("0"); // TODO: Cameron asked for null but table doesn't allow
            request.setDomain(provisionNumberRequest.getRealm());
            request.setAttribute("37501");
            request.setValue("sip:+" + provisionNumberRequest.getNumber() + "@" + provisionNumberRequest.getRealm());
            request.setType("2"); // TODO: Constant, numeric type
            request.setModified(provisionNumberRequest.getStartDate());
            request.setDateStart(provisionNumberRequest.getStartDate());
            request.setDateEnd(calendar.getTime());
            request.setSubscriberId("999"); // TODO: Constant? Default?

            exchange.getIn().setBody(request);
        }
    }

    private static final class AuthorisedCallerIdUserPreference implements Processor {

        public static final Processor INSTANCE = new AuthorisedCallerIdUserPreference();

        @Override
        public void process(Exchange exchange) throws Exception {

            ProvisionNumberRequest provisionNumberRequest = exchange.getProperty(ProvisionNumberRequest.class.getName(), ProvisionNumberRequest.class);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(provisionNumberRequest.getStartDate().getTime());
            calendar.add(Calendar.YEAR, 20);

            InsertUserPreferenceRequest request = new InsertUserPreferenceRequest();
            request.setUuid(String.valueOf(provisionNumberRequest.getBusinessPartnerId()));
            request.setUsername(provisionNumberRequest.getNumber());
            request.setDomain(provisionNumberRequest.getRealm());
            request.setAttribute("20301");
            request.setValue("sip:+" + provisionNumberRequest.getNumber() + "@" + provisionNumberRequest.getRealm());
            request.setType("2"); // TODO: Constant, numeric type
            request.setModified(provisionNumberRequest.getStartDate());
            request.setDateStart(provisionNumberRequest.getStartDate());
            request.setDateEnd(calendar.getTime());
            request.setSubscriberId("999"); // TODO: Constant? Default?

            exchange.getIn().setBody(request);
        }
    }
}
