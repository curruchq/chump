package com.conversant.chump.route.v2;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.ApiResponse;
import com.conversant.chump.model.NumberRequest;
import com.conversant.chump.model.ProvisionOrderRequest;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.chump.route.v2.numbers.ProvisionNumbersRoute;
import com.conversant.webservice.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.*;

/**
 * Created by jhill on 31/12/14.
 */
@Component(value = "orderRoute-v2")
public class OrderRoute implements ChumpRoute {

    /** Base resource */
    private static final String RESOURCE = "/v2/orders";

    private static final String PROVISION_ORDER_CUSTOM = "direct://provisionOrder2Custom";
    private static final String PROVISION_NUMBER_PORTS_CUSTOM = "direct://provisionNumberPorts2Custom";
    private static final String ORDER = "order";

    /** Provision all numbers and number ports on an order */
    public static final ChumpOperation PROVISION = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .method(POST)
                    .resource(RESOURCE)
                    .path("/{orderNo}/provision")
                    .requestType(ProvisionOrderRequest.class)
                    .build())
            .trx(false)
            // TODO: Can remove once fix header and path param
            .preProcessors(Arrays.asList(ProvisionOrderRequestProcessor.INSTANCE))
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadOrderRequestProcessor.INSTANCE, AdempiereRoute.READ_ORDER.getUri()),
                    ChumpOperation.pair(ReadOrderDIDsRequestProcessor.INSTANCE, AdempiereRoute.READ_ORDER_DIDS.getUri()),
                    ChumpOperation.pair(ProvisionNumberSplitRequestProcessor.INSTANCE, PROVISION_ORDER_CUSTOM),
                    ChumpOperation.pair(ReadOrderNumberPortsRequestProcessor.INSTANCE, AdempiereRoute.READ_ORDER_NUMBER_PORTS.getUri()),
                    ChumpOperation.pair(OrderNumberPortsSplitRequestProcessor.INSTANCE, PROVISION_NUMBER_PORTS_CUSTOM)))
            .build();

    @Component(value = "orderRouteCustom-v2")
    private static final class OrderRouteBuilder extends RouteBuilder {
        @Override
        public void configure() throws Exception {

            // Custom route for fancy split/aggregate logic
            from(PROVISION_ORDER_CUSTOM)

                    // Split on the body, individual ProvisionNumberRequest, and use custom aggregation strategy
                    // which groups individual ApiResponse's into a list
                    .split(body(), ProvisionNumberAggregationStrategy.INSTANCE)

                    // Call provision number for each split request
                    .to(ProvisionNumbersRoute.PROVISION.getUri()).end()

                    // Process final result of custom aggregation strategy into a single ApiResponse
                    .process(AggregatedApiResponseProcessor.INSTANCE);

            // Custom route for fancy split/aggregate logic
            from(PROVISION_NUMBER_PORTS_CUSTOM)

                    // Split on the body, individual CreateNumberPortSubscriptionRequest, and use custom aggregation strategy
                    // which groups individual ApiResponse's into a list
                    .split(body(), ProvisionNumberPortsAggregationStrategy.INSTANCE)

                    // Call provision number for each split request
                    .to(AdempiereRoute.CREATE_NUMBER_PORT_SUBSCRIPTION.getUri()).end()

                    // Process final result of custom aggregation strategy into a single ApiResponse
                    .process(AggregatedApiResponseProcessor.INSTANCE);
        }
    }

    private static final class ProvisionOrderRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ProvisionOrderRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            // TODO: How get into request POJO automatically? Generic processor?
            ProvisionOrderRequest request = exchange.getIn().getBody(ProvisionOrderRequest.class);
            if (request.getOrderNo() == null)
                request.setOrderNo((String) exchange.getIn().getHeader("orderNo"));
        }
    }

    private static final class ReadOrderRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadOrderRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ProvisionOrderRequest request = exchange.getProperty(ProvisionOrderRequest.class.getName(), ProvisionOrderRequest.class);

            ReadOrderRequest readOrderRequest = new ReadOrderRequest();
            readOrderRequest.setLoginRequest(createLoginRequest(exchange, TYPE_READ_ORDER));
            readOrderRequest.setDocumentNo(request.getOrderNo());

            exchange.getIn().setBody(readOrderRequest);
        }
    }

    private static final class ReadOrderDIDsRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadOrderDIDsRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ReadOrderResponse response = exchange.getIn().getBody(ReadOrderResponse.class);

            ReadOrderDIDsRequest readOrderDIDsRequest = new ReadOrderDIDsRequest();
            readOrderDIDsRequest.setLoginRequest(createLoginRequest(exchange, TYPE_READ_ORDER_DIDS));
            readOrderDIDsRequest.setOrderId(response.getOrder().getOrderId());

            exchange.getIn().setBody(readOrderDIDsRequest);

            exchange.setProperty(ORDER, response.getOrder());
        }
    }

    private static final class ProvisionNumberSplitRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ProvisionNumberSplitRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ProvisionOrderRequest provisionOrderRequest = exchange.getProperty(ProvisionOrderRequest.class.getName(), ProvisionOrderRequest.class);
            Order order = exchange.getProperty(ORDER, Order.class);

            List<NumberRequest> requests = exchange.getIn().getBody(ReadOrderDIDsResponse.class).getDids().stream()
                    .map(did -> {

                        NumberRequest request = new NumberRequest();
                        request.setNumber(did);
                        request.setRealm(provisionOrderRequest.getRealm());
                        request.setProxy(provisionOrderRequest.getProxy());
                        request.setBusinessPartnerId(order.getBusinessPartnerId());
                        request.setBusinessPartnerLocationId(order.getBusinessPartnerLocationId());
                        request.setStartDate(order.getDatePromised());
                        request.setPaidUntilDate(order.getDatePromised());
                        request.setOrgId(order.getOrgId());

                        return request;
                    })
                    .collect(Collectors.toList());

            // Set body as list of requests for use with split by body
            exchange.getIn().setBody(requests);
        }
    }

    private static final class AggregatedApiResponseProcessor implements Processor {

        public static final Processor INSTANCE = new AggregatedApiResponseProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            // Check if existing ApiResponse set
            ApiResponse existing = exchange.getProperty(PROPERTY_API_RESPONSE, ApiResponse.class);

            // Check if any ApiResponse's failed
            boolean failure = exchange.getIn().getBody(List.class).stream().anyMatch(o -> o instanceof ApiResponse && ((ApiResponse) o).getCode() != 200);

            // If found existing and it contains a failure then fail this response
            if (existing != null && existing.getCode() != ApiResponse.SUCCESS) {
                failure = true;
            }

            // Create ApiResponse and all list of responses
            ApiResponse response = failure ? ApiResponse.badRequest() : ApiResponse.success();
            response.setResponses(exchange.getIn().getBody(List.class));

            // If found existing then add responses
            if (existing != null && existing.getResponses() != null) {
                response.getResponses().addAll(existing.getResponses());
            }

            // Set as exchange property to be picked up by ApiResponseProcessor
            exchange.setProperty(PROPERTY_API_RESPONSE, response);
        }
    }

    private static final class ProvisionNumberAggregationStrategy implements AggregationStrategy {

        public static final AggregationStrategy INSTANCE = new ProvisionNumberAggregationStrategy();

        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

            ApiResponse response = newExchange.getIn().getBody(ApiResponse.class);

            if (response.getCode() == ApiResponse.SUCCESS) {
                NumberRequest request = newExchange.getProperty(NumberRequest.class.getName(), NumberRequest.class);
                response.setMessage("Provisioned " + request.getNumber());
            }

            // First invocation
            if (oldExchange == null) {

                List<ApiResponse> responses = new ArrayList<>();
                responses.add(response);

                newExchange.getIn().setBody(responses);

                return newExchange;
            }

            oldExchange.getIn().getBody(List.class).add(response);

            return oldExchange;
        }
    }

    private static final class ReadOrderNumberPortsRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadOrderNumberPortsRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            Order order = exchange.getProperty(ORDER, Order.class);

            ReadOrderNumberPortsRequest readOrderNumberPortsRequest = new ReadOrderNumberPortsRequest();
            readOrderNumberPortsRequest.setLoginRequest(createLoginRequest(exchange, TYPE_READ_ORDER_NUMBER_PORTS));
            readOrderNumberPortsRequest.setOrderId(order.getOrderId());

            exchange.getIn().setBody(readOrderNumberPortsRequest);
        }
    }

    private static final class OrderNumberPortsSplitRequestProcessor implements Processor {

        public static final Processor INSTANCE = new OrderNumberPortsSplitRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            Order order = exchange.getProperty(ORDER, Order.class);

            List<CreateNumberPortSubscriptionRequest> requests = exchange.getIn().getBody(ReadOrderNumberPortsResponse.class).getNumbers().stream()
                    .map(did -> {

                        CreateNumberPortSubscriptionRequest request = new CreateNumberPortSubscriptionRequest();
                        request.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_NUMBER_PORT_SUBSCRIPTION));
                        request.setNumber(did);
                        request.setBusinessPartnerId(order.getBusinessPartnerId());
                        request.setBusinessPartnerLocationId(order.getBusinessPartnerLocationId());
                        request.setOrgId(order.getOrgId());

                        return request;
                    })
                    .collect(Collectors.toList());

            // Set body as list of requests for use with split by body
            exchange.getIn().setBody(requests);
        }
    }

    private static final class ProvisionNumberPortsAggregationStrategy implements AggregationStrategy {

        public static final AggregationStrategy INSTANCE = new ProvisionNumberPortsAggregationStrategy();

        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

            StandardResponse response = newExchange.getIn().getBody(StandardResponse.class);
            CreateNumberPortSubscriptionRequest request = newExchange.getProperty(CreateNumberPortSubscriptionRequest.class.getName(), CreateNumberPortSubscriptionRequest.class);

            ApiResponse apiResponse;
            if (response.isSuccess()) {
                apiResponse = ApiResponse.success();
                apiResponse.setMessage("Created number port subscription for " + request.getNumber());
            }
            else {
                apiResponse = ApiResponse.error();
                apiResponse.setMessage("Failed to create number port subscription for " + request.getNumber());
            }

            // First invocation
            if (oldExchange == null) {

                List<ApiResponse> responses = new ArrayList<>();
                responses.add(apiResponse);

                newExchange.getIn().setBody(responses);

                return newExchange;
            }

            oldExchange.getIn().getBody(List.class).add(apiResponse);

            return oldExchange;
        }
    }
}
