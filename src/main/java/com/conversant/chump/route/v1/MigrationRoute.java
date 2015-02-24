package com.conversant.chump.route.v1;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.ApiResponse;
import com.conversant.chump.model.InsertUserPreferenceRequest;
import com.conversant.chump.model.MigrateNumberRequest;
import com.conversant.chump.model.MigrationRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.*;

/**
 * Chump route for migration from the old to new system.
 * <p/>
 * Creates new call products and new subscriptions for these products in Adempiere and creates 20301 for the customers numbers
 * and a 37501 entry for their main number.
 * <p/>
 * Supersedes CreateProductRoute, CreateSubscriptionsRoute and CreateNumberSqlEntryRoute.
 * <p/>
 * Problems: There's a fair amount of duplicate code in here.
 * <p/>
 * Created by Saren Currie on 15-02-10.
 */
@Component
public class MigrationRoute implements ChumpRoute {

    /** Base resource */
    private static final String RESOURCE = "/v1/migration";


    private static final String MIGRATION_SPLIT_ROUTE = "direct://migrationSplitRoute";

    /** Migration of multiple numbers */
    public static final ChumpOperation MIGRATION = ChumpOperation.builder()

            // POST /v1/migration
            .rest(RestOperation.builder()
                    .method(POST)
                    .resource(RESOURCE)
                    .requestType(MigrationRequest.class)
                    .method(RestOperation.HttpMethod.POST)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(CallerIdUserPreference.INSTANCE, UserPreferenceRoute.INSERT.getUri()),
                    ChumpOperation.pair(ReadBusinessPartnerRequestProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER.getUri()),
                    ChumpOperation.pair(MigrationSplitRequestProcessor.INSTANCE, MIGRATION_SPLIT_ROUTE)
            ))
            .build();

    private static final class CallerIdUserPreference implements Processor {

        public static final Processor INSTANCE = new CallerIdUserPreference();

        @Override
        public void process(Exchange exchange) throws Exception {

            MigrationRequest migrationRequest = exchange.getProperty(MigrationRequest.class.getName(), MigrationRequest.class);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.YEAR, 20);

            InsertUserPreferenceRequest request = new InsertUserPreferenceRequest();
            request.setUuid("");
            request.setUsername("");
            request.setDomain(migrationRequest.getRealm());
            request.setAttribute("37501");
            request.setValue("sip:+" + migrationRequest.getMainNumber() + "@" + migrationRequest.getRealm());
            request.setType("2");
            request.setModified(new Date());
            request.setDateStart(new Date());
            request.setDateEnd(calendar.getTime());
            request.setSubscriberId("0");

            exchange.getIn().setBody(request);
        }
    }

    private static final class ReadBusinessPartnerRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadBusinessPartnerRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            MigrationRequest migrationRequest = exchange.getProperty(MigrationRequest.class.getName(), MigrationRequest.class);

            ReadBusinessPartnerBySearchKeyRequest request = new ReadBusinessPartnerBySearchKeyRequest();
            request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_BUSINESS_PARTNER, ADEMPIERE_USER_DRUPAL));
            request.setSearchKey(migrationRequest.getBusinessPartnerSearchKey());

            exchange.getIn().setBody(request);
        }
    }

    private static class MigrationSplitRequestProcessor implements Processor {

        public static final Processor INSTANCE = new MigrationSplitRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            MigrationRequest migrationRequest = exchange.getProperty(MigrationRequest.class.getName(), MigrationRequest.class);
            ReadBusinessPartnerResponse bp = exchange.getIn().getBody(ReadBusinessPartnerResponse.class);

            List<MigrateNumberRequest> requests = Arrays.stream(migrationRequest.getNumbers())
                    .map(number -> {

                        MigrateNumberRequest request = new MigrateNumberRequest();

                        request.setBusinessPartnerId(bp.getBusinessPartner().getBusinessPartnerId());
                        request.setBusinessPartnerLocationId(migrationRequest.getBusinessPartnerLocationId());
                        request.setNumber(number);
                        request.setPriceListVersionId(migrationRequest.getPriceListVersionId());
                        request.setRealm(migrationRequest.getRealm());

                        return request;
                    })
                    .collect(Collectors.toList());

            exchange.getIn().setBody(requests);
        }
    }

    @Component
    private static final class MigrationRouteBuilder extends RouteBuilder {
        @Override
        public void configure() throws Exception {

            // Custom route for fancy split/aggregate logic
            from(MIGRATION_SPLIT_ROUTE)

                    // Split on the body, individual MigrateNumberRequest, and use custom aggregation strategy
                    // which groups individual ApiResponse's into a list
                    .split(body(), MigrateNumberAggregationStrategy.INSTANCE)

                    // Call migrate number for each split request
                    .to(MIGRATE_NUMBER.getUri()).end()

                    // Process final result of custom aggregation strategy into a single ApiResponse
                    .process(MigrateNumberCustomResponseProcessor.INSTANCE);
        }
    }

    /** Migrate single number */
    public static final ChumpOperation MIGRATE_NUMBER = ChumpOperation.builder()
            .uri("direct://migrateNumber")
            .to(Arrays.asList(
                    ChumpOperation.pair(CreateNewCallProductsProcessor.INSTANCE, AdempiereRoute.CREATE_CALL_PRODUCT_2.getUri()),
                    ChumpOperation.pair(CreateNewSubscriptionsProcessor.INSTANCE, AdempiereRoute.CREATE_CALL_SUBSCRIPTION_2.getUri()),
                    ChumpOperation.pair(AuthorisedCallerIdUserPreference.INSTANCE, UserPreferenceRoute.INSERT.getUri())
            ))
            .postProcessors(Arrays.asList(
                    ApiResponseProcessor.INSTANCE
            ))
            .build();

    private static final class CreateNewCallProductsProcessor implements Processor {

        public static final Processor INSTANCE = new CreateNewCallProductsProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            MigrateNumberRequest request = exchange.getProperty(MigrateNumberRequest.class.getName(), MigrateNumberRequest.class);

            CreateCallProductRequest callProductRequest = new CreateCallProductRequest();
            callProductRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_CALL_PRODUCT_2));
            callProductRequest.setDomain(request.getRealm());
            callProductRequest.setNumber(request.getNumber());
            callProductRequest.setPricelistVersionId(request.getPriceListVersionId());

            exchange.getIn().setBody(callProductRequest);
        }
    }

    private static final class CreateNewSubscriptionsProcessor implements Processor {

        public static final Processor INSTANCE = new CreateNewSubscriptionsProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            MigrateNumberRequest request = exchange.getProperty(MigrateNumberRequest.class.getName(), MigrateNumberRequest.class);

            CreateCallSubscription2Request callSubscriptionRequest = new CreateCallSubscription2Request();
            callSubscriptionRequest.setDomain(request.getRealm());
            callSubscriptionRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_CALL_SUBSCRIPTION_2));
            callSubscriptionRequest.setBusinessPartnerId(request.getBusinessPartnerId());
            callSubscriptionRequest.setBusinessPartnerLocationId(request.getBusinessPartnerLocationId());
            callSubscriptionRequest.setNumber(request.getNumber());
            callSubscriptionRequest.setStartDate(new Date());

            exchange.getIn().setBody(callSubscriptionRequest);
        }
    }

    //TODO: These classes are very similar to ones used in number route, refactor this at some point.
    private static final class AuthorisedCallerIdUserPreference implements Processor {

        public static final Processor INSTANCE = new AuthorisedCallerIdUserPreference();

        @Override
        public void process(Exchange exchange) throws Exception {

            MigrateNumberRequest migrationRequest = exchange.getProperty(MigrateNumberRequest.class.getName(), MigrateNumberRequest.class);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.YEAR, 20);

            InsertUserPreferenceRequest request = new InsertUserPreferenceRequest();
            request.setUuid("");
            request.setUsername(migrationRequest.getNumber());
            request.setDomain(migrationRequest.getRealm());
            request.setAttribute("20301");
            request.setValue("sip:+" + migrationRequest.getNumber() + "@" + migrationRequest.getRealm());
            request.setType("2");
            request.setModified(new Date());
            request.setDateStart(new Date());
            request.setDateEnd(calendar.getTime());
            request.setSubscriberId("0");

            exchange.getIn().setBody(request);
        }
    }

    private static class MigrateNumberCustomResponseProcessor implements Processor {

        public static final Processor INSTANCE = new MigrateNumberCustomResponseProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            // Check if any ApiResponse's failed
            boolean failure = exchange.getIn().getBody(List.class).stream().anyMatch(o -> o instanceof ApiResponse && ((ApiResponse) o).getCode() != 200);

            ApiResponse response = failure ? ApiResponse.badRequest() : ApiResponse.success();
            response.setResponses(exchange.getIn().getBody(List.class));

            // Set as exchange property to be picked up by ApiResponseProcessor
            exchange.setProperty(PROPERTY_API_RESPONSE, response);
        }
    }

    private static final class MigrateNumberAggregationStrategy implements AggregationStrategy {

        public static final AggregationStrategy INSTANCE = new MigrateNumberAggregationStrategy();

        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

            StandardResponse response = newExchange.getIn().getBody(StandardResponse.class);

            if (response.isSuccess()) {
                MigrateNumberRequest request = newExchange.getProperty(MigrateNumberRequest.class.getName(), MigrateNumberRequest.class);
                response.setMessage("Migrated " + request.getNumber());
            }

            // First invocation
            if (oldExchange == null) {

                List<StandardResponse> responses = new ArrayList<>();
                responses.add(response);

                newExchange.getIn().setBody(responses);

                return newExchange;
            }

            oldExchange.getIn().getBody(List.class).add(response);

            return oldExchange;
        }
    }
}
