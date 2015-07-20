package com.conversant.chump.route.v1.numbers;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.CreateNumberRequest;
import com.conversant.chump.processor.batch.BatchAggregationStrategy;
import com.conversant.chump.route.AdempiereRoute;
import com.conversant.webservice.CreateCallProductRequest;
import com.conversant.webservice.CreateDIDProductRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.POST;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.TYPE_CREATE_CALL_PRODUCT;
import static com.conversant.chump.util.Constants.TYPE_CREATE_DID_PRODUCT;

/**
 * Created by jhill on 23/05/15.
 */
@Component(value = "createNumberRoute-v1")
public class CreateNumbersRoute extends AbstractNumbersRoute {

    /**
     * Create calling and did products
     */
    public static final ChumpOperation CREATE_SINGLE = ChumpOperation.builder()
            .uri("direct://createProduct-v1")
            .to(Arrays.asList(
                    ChumpOperation.pair(CreateCallProductRequestProcessor.INSTANCE, AdempiereRoute.CREATE_CALL_PRODUCT.getUri()),
                    ChumpOperation.pair(CreateDidProductRequestProcessor.INSTANCE, AdempiereRoute.CREATE_DID_PRODUCT.getUri()).excludable("did")))
            .build();

    /**
     * Create a batch of calling and did products
     */
    public static final ChumpOperation CREATE_REST = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .method(POST)
                    .resource(RESOURCE)
                    .requestType(CreateNumberRequest.class)
                    .build())
            .trx(false)
            .preProcessors(Collections.singletonList(CreateNumberRequestProcessor.INSTANCE))
            .to(Collections.singletonList(
                    ChumpOperation.single(CREATE_SINGLE.getUri())
                            .split(new BatchAggregationStrategy(e ->
                                    "Create " + e.getProperty(CreateNumberRequest.class.getName(), CreateNumberRequest.class).getNumber()))))
            .build();

    /**
     * Create call subscription request processor
     */
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
            callProductRequest.setOrgId(request.getOrgId());

            exchange.getIn().setBody(callProductRequest);
        }
    }

    /**
     * Create call subscription request processor
     */
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
            didProductRequest.setOrgId(request.getOrgId());

            exchange.getIn().setBody(didProductRequest);
        }
    }
}
