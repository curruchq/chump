package com.conversant.chump.route.v1.orders;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.OrderRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.CreateOrderRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.TYPE_CREATE_ORDER;

/**
 * Created by jhill on 20/07/15.
 */
@Component
public class CreateOrdersRoute extends AbstractOrdersRoute {

    /**
     * Create an order
     */
    public static final ChumpOperation CREATE = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .method(POST)
                    .resource(RESOURCE)
                    .requestType(OrderRequest.class)
                    .build())
            .trx(false)
            .to(Collections.singletonList(ChumpOperation.pair(CreateOrderRequestProcessor.INSTANCE, AdempiereRoute.CREATE_ORDER.getUri())))
            .postProcessors(Collections.singletonList(ApiResponseProcessor.INSTANCE))
            .build();

    private static final class CreateOrderRequestProcessor implements Processor {

        public static final Processor INSTANCE = new CreateOrderRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            OrderRequest orderRequest = exchange.getIn().getBody(OrderRequest.class);

            CreateOrderRequest createOrderRequest = new CreateOrderRequest();
            createOrderRequest.setLoginRequest(createLoginRequest(exchange, TYPE_CREATE_ORDER));
            createOrderRequest.setBusinessPartnerId(orderRequest.getBusinessPartnerId());
            createOrderRequest.setBusinessPartnerLocationId(orderRequest.getBusinessPartnerLocationId());
            createOrderRequest.setPricelistId(orderRequest.getPricelistId());
            createOrderRequest.setWarehouseId(orderRequest.getPricelistId());
            createOrderRequest.setDatePromised(orderRequest.getDatePromised());
            createOrderRequest.setDateOrdered(orderRequest.getDateOrdered());
            createOrderRequest.setOrgId(orderRequest.getOrgId());

            exchange.getIn().setBody(createOrderRequest);
        }
    }
}
