package com.conversant.chump.route.v1.orders;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.OrderRequest;
import com.conversant.chump.model.ProvisionOrderRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.Order;
import com.conversant.webservice.ReadOrderRequest;
import com.conversant.webservice.ReadOrderResponse;
import com.conversant.webservice.UpdateOrderRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.PUT;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.TYPE_UPDATE_ORDER;
import static com.conversant.chump.util.Constants.TYPE_READ_ORDER;

/**
 * Created by jhill on 20/07/15.
 */
@Component
public class UpdateOrdersRoute extends AbstractOrdersRoute {

    /**
     * Update an order
     */
    public static final ChumpOperation UPDATE = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .method(PUT)
                    .resource(RESOURCE)
                    .path("/{orderNo}")
                    .requestType(OrderRequest.class)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadOrderRequestProcessor.INSTANCE, AdempiereRoute.READ_ORDER.getUri()),
                    ChumpOperation.pair(UpdateOrderRequestProcessor.INSTANCE, AdempiereRoute.UPDATE_ORDER.getUri())))
            .postProcessors(Collections.singletonList(ApiResponseProcessor.INSTANCE))
            .build();

    private static final class ReadOrderRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadOrderRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ReadOrderRequest readOrderRequest = new ReadOrderRequest();
            readOrderRequest.setLoginRequest(createLoginRequest(exchange, TYPE_READ_ORDER));
            readOrderRequest.setDocumentNo((String) exchange.getIn().getHeader("orderNo"));

            exchange.getIn().setBody(readOrderRequest);
        }
    }

    private static final class UpdateOrderRequestProcessor implements Processor {

        public static final Processor INSTANCE = new UpdateOrderRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            Order order = exchange.getIn().getBody(ReadOrderResponse.class).getOrder();
            OrderRequest orderRequest = exchange.getProperty(OrderRequest.class.getName(), OrderRequest.class);

            UpdateOrderRequest updateOrderRequest = new UpdateOrderRequest();
            updateOrderRequest.setLoginRequest(createLoginRequest(exchange, TYPE_UPDATE_ORDER));
            updateOrderRequest.setOrderId(order.getOrderId());
            updateOrderRequest.setBusinessPartnerId(orderRequest.getBusinessPartnerId());
            updateOrderRequest.setBusinessPartnerLocationId(orderRequest.getBusinessPartnerLocationId());
            updateOrderRequest.setPricelistId(orderRequest.getPricelistId());
            updateOrderRequest.setWarehouseId(orderRequest.getPricelistId());
            updateOrderRequest.setDatePromised(orderRequest.getDatePromised());
            updateOrderRequest.setDateOrdered(orderRequest.getDateOrdered());
            updateOrderRequest.setOrgId(orderRequest.getOrgId());

            exchange.getIn().setBody(updateOrderRequest);
        }
    }
}
