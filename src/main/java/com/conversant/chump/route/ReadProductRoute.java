package com.conversant.chump.route;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.ReadBPPriceRequest;
import com.conversant.chump.model.ReadProductByCategoryRequest;
import com.conversant.chump.model.ReadProductByIdRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.chump.util.AdempiereHelper;
import com.conversant.chump.util.Constants;
import com.conversant.webservice.ReadProductBPPriceRequest;
import com.conversant.webservice.ReadProductRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.conversant.chump.common.RestOperation.HttpMethod.GET;

/**
 * Created by Saren Currie on 2015-03-11.
 */
@Component
public class ReadProductRoute implements ChumpRoute {
    private static final String RESOURCE = "/v1/product";

    public static final ChumpOperation READ_BY_CATEGORY = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .method(GET)
                    .requestType(ReadProductByCategoryRequest.class)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadProductByCategoryProcessor.INSTANCE, AdempiereRoute.READ_PRODUCT.getUri())))
            .postProcessors(Arrays.asList(
                    new StandardResponseRemover("product"), ApiResponseProcessor.INSTANCE))
            .build();

    public static final ChumpOperation READ_BY_ID = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .resource(RESOURCE)
                    .path("/{productId}")
                    .method(GET)
                    .requestType(ReadProductByIdRequest.class)
                    .build())
            .trx(false)
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadProductByIdProcessor.INSTANCE, AdempiereRoute.READ_PRODUCT.getUri())))
            .postProcessors(Arrays.asList(
                    new StandardResponseRemover("product"), ApiResponseProcessor.INSTANCE))
            .build();

    public static final ChumpOperation READ_BP_PRICE = ChumpOperation.builder()
            .trx(false)
            .rest(RestOperation.builder()
                    .method(GET)
                    .path("/{productId}/price")
                    .resource(RESOURCE)
                    .requestType(ReadBPPriceRequest.class)
                    .build())
            .to(Arrays.asList(
                    ChumpOperation.pair(ReadBPPriceProcessor.INSTANCE, AdempiereRoute.READ_PRODUCT_BP_PRICE.getUri())
            ))
            .postProcessors(Arrays.asList(
                    new StandardResponseRemover("productPrice"), ApiResponseProcessor.INSTANCE
            ))
            .build();

    private static final class ReadBPPriceProcessor implements Processor {

        public static final Processor INSTANCE = new ReadBPPriceProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            ReadProductBPPriceRequest request = new ReadProductBPPriceRequest();

            request.setLoginRequest(AdempiereHelper.createLoginRequest(exchange, Constants.TYPE_READ_PRODUCT_BP_PRICE, Constants.ADEMPIERE_USER_INTALIO));
            request.setProductId(Integer.parseInt((String) exchange.getIn().getHeader("productId")));
            request.setBpSearchKey((String) exchange.getIn().getHeader("businessPartnerSearchKey"));

            exchange.getIn().setBody(request);
        }
    }

    private static class ReadProductByCategoryProcessor implements Processor {
        public static final Processor INSTANCE = new ReadProductByCategoryProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {
            ReadProductRequest request = new ReadProductRequest();

            request.setLoginRequest(AdempiereHelper.createLoginRequest(exchange, Constants.TYPE_READ_PRODUCT, Constants.ADEMPIERE_USER_INTALIO));
            request.setProductCategoryId(Integer.parseInt((String) exchange.getIn().getHeader("productCategoryId")));

            exchange.getIn().setBody(request);
        }
    }

    private static class ReadProductByIdProcessor implements Processor {
        public static final Processor INSTANCE = new ReadProductByIdProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {
            ReadProductRequest request = new ReadProductRequest();

            request.setLoginRequest(AdempiereHelper.createLoginRequest(exchange, Constants.TYPE_READ_PRODUCT, Constants.ADEMPIERE_USER_INTALIO));
            request.setProductId(Integer.parseInt((String) exchange.getIn().getHeader("productId")));

            exchange.getIn().setBody(request);
        }
    }
}
