package com.conversant.chump.route;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.ApiResponse;
import com.conversant.chump.model.ProvisionNumberRequest;
import com.conversant.chump.model.ProvisionOrderRequest;
import com.conversant.chump.route.v1.NumberRoute;
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
@Component
public class OrderRoute implements ChumpRoute {

    private static final String RESOURCE = "/v1/orders";
    private static final String PROVISION_ORDER_CUSTOM = "direct://provisionOrderCustom";
    private static final String ORDER = "order";

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
                    ChumpOperation.pair(ProvisionNumberSplitRequestProcessor.INSTANCE, PROVISION_ORDER_CUSTOM)))
            .build();

    @Component
    private static final class OrderRouteBuilder extends RouteBuilder {
        @Override
        public void configure() throws Exception {

            // Custom route for fancy split/aggregate logic
            from(PROVISION_ORDER_CUSTOM)

                    // Split on the body, individual ProvisionNumberRequest, and use custom aggregation strategy
                    // which groups individual ApiResponse's into a list
                    .split(body(), ProvisionNumberAggregationStrategy.INSTANCE)

                            // Call provision number for each split request
                    .to(NumberRoute.PROVISION.getUri()).end()

                    // Process final result of custom aggregation strategy into a single ApiResponse
                    .process(ProvisionOrderCustomResponseProcessor.INSTANCE);
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

            List<ProvisionNumberRequest> requests = exchange.getIn().getBody(ReadOrderDIDsResponse.class).getDids().stream()
                    .map(did -> {

                        ProvisionNumberRequest request = new ProvisionNumberRequest();
                        request.setNumber(did);
                        request.setRealm(provisionOrderRequest.getRealm());
                        request.setProxy(provisionOrderRequest.getProxy());
                        request.setBusinessPartnerId(order.getBusinessPartnerId());
                        request.setBusinessPartnerLocationId(order.getBusinessPartnerLocationId());
                        request.setStartDate(order.getDatePromised());
                        request.setPaidUntilDate(order.getDatePromised());

                        return request;
                    })
                    .collect(Collectors.toList());

            // Set body as list of requests for use with split by body
            exchange.getIn().setBody(requests);
        }
    }

    private static final class ProvisionOrderCustomResponseProcessor implements Processor {

        public static final Processor INSTANCE = new ProvisionOrderCustomResponseProcessor();

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

    private static final class ProvisionNumberAggregationStrategy implements AggregationStrategy {

        public static final AggregationStrategy INSTANCE = new ProvisionNumberAggregationStrategy();

        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

            ApiResponse response = newExchange.getIn().getBody(ApiResponse.class);

            if (response.getCode() == ApiResponse.SUCCESS) {
                ProvisionNumberRequest request = newExchange.getProperty(ProvisionNumberRequest.class.getName(), ProvisionNumberRequest.class);
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
}
