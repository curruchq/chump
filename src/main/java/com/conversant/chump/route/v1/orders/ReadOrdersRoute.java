package com.conversant.chump.route.v1.orders;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.ReadOrderLinesRequest;
import com.conversant.webservice.ReadOrderRequest;
import com.conversant.webservice.ReadOrderResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.GET;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.ADEMPIERE_USER_INTALIO;
import static com.conversant.chump.util.Constants.TYPE_READ_ORDER;
import static com.conversant.chump.util.Constants.TYPE_READ_ORDER_LINES;

/**
 * Created by jhill on 20/07/15.
 */
@Component
public class ReadOrdersRoute extends AbstractOrdersRoute {

    /**
     * Read order
     */
    public static final ChumpOperation READ = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .method(GET)
                    .resource(RESOURCE)
                    .path("/{orderNo}")
                    .build())
            .trx(false)
            .to(Collections.singletonList(ChumpOperation.pair(ReadOrderRequestProcessor.INSTANCE, AdempiereRoute.READ_ORDER.getUri())))
            .postProcessors(Arrays.asList(new StandardResponseRemover("order"), ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Read order lines
     */
    public static final ChumpOperation LINES = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .method(GET)
                    .resource(RESOURCE)
                    .path("/{orderNo}/lines")
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadOrderRequestProcessor.INSTANCE, AdempiereRoute.READ_ORDER.getUri()),
                    ChumpOperation.pair(ReadOrderLinesRequestProcessor.INSTANCE, AdempiereRoute.READ_ORDER_LINES.getUri())))
            .postProcessors(Arrays.asList(
                    new StandardResponseRemover("orderLine"), ApiResponseProcessor.INSTANCE))
            .build();

    /**
     * Read order request processor
     */
    private static final class ReadOrderRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadOrderRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ReadOrderRequest readOrderRequest = new ReadOrderRequest();
            readOrderRequest.setLoginRequest(createLoginRequest(exchange, TYPE_READ_ORDER, ADEMPIERE_USER_INTALIO));
            readOrderRequest.setDocumentNo((String) exchange.getIn().getHeader("orderNo"));

            if (exchange.getIn().getHeader("productId") != null) {
                exchange.setProperty("productId", Integer.parseInt((String) exchange.getIn().getHeader("productId")));
            }

            if (exchange.getIn().getHeader("productCategoryId") != null) {
                exchange.setProperty("productCategoryId", Integer.parseInt((String) exchange.getIn().getHeader("productCategoryId")));
            }

            exchange.getIn().setBody(readOrderRequest);
        }
    }

    /**
     * Read order lines request processor
     */
    private static class ReadOrderLinesRequestProcessor implements Processor {

        public static final Processor INSTANCE = new ReadOrderLinesRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ReadOrderResponse response = exchange.getIn().getBody(ReadOrderResponse.class);

            ReadOrderLinesRequest readOrderLinesRequest = new ReadOrderLinesRequest();
            readOrderLinesRequest.setLoginRequest(createLoginRequest(exchange, TYPE_READ_ORDER_LINES, ADEMPIERE_USER_INTALIO));
            readOrderLinesRequest.setOrderId(response.getOrder().getOrderId());

            Integer productId = exchange.getProperty("productId", Integer.class);
            if (productId != null) {
                readOrderLinesRequest.setProductId(productId);
            }

            Integer productCategoryId = exchange.getProperty("productCategoryId", Integer.class);
            if (productCategoryId != null) {
                readOrderLinesRequest.setProductCategoryId(productCategoryId);
            }

            exchange.getIn().setBody(readOrderLinesRequest);
        }
    }
}
