package com.conversant.chump.route.v1.numbers;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.EndDateUserPreferenceRequest;
import com.conversant.chump.model.NumberRequest;
import com.conversant.chump.route.v1.UserPreferenceRoute;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

import static com.conversant.chump.common.RestOperation.HttpMethod.PUT;

/**
 * Created by jhill on 6/07/15.
 */
@Component(value = "userPreferenceNumbersRoute-v1")
public class UserPreferenceNumbersRoute extends AbstractNumbersRoute {

    /**
     * Create a batch of calling and did products
     */
    public static final ChumpOperation CALLER_ID = ChumpOperation.builder()
            .rest(RestOperation.builder()
                    .method(PUT)
                    .resource(RESOURCE)
                    .path("/{number}/callerId")
                    .requestType(NumberRequest.class)
                    .build())
            .trx(false)
            .preProcessors(Collections.singletonList(NumberRequestProcessor.INSTANCE))
            .to(Arrays.asList(
                    ChumpOperation.pair(EndDateCallerIdv1UserPreferenceRequestProcessor.INSTANCE, UserPreferenceRoute.END_DATE.getUri()),
                    ChumpOperation.pair(ProvisionNumbersRoute.CallerIdv1UserPreferenceRequestProcessor.INSTANCE, UserPreferenceRoute.INSERT_IF_NOT_EXISTS.getUri())))
            .build();

    /**
     * End date user preference request processor
     */
    private static final class EndDateCallerIdv1UserPreferenceRequestProcessor implements Processor {

        public static final Processor INSTANCE = new EndDateCallerIdv1UserPreferenceRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            NumberRequest numberRequest = exchange.getProperty(NumberRequest.class.getName(), NumberRequest.class);

            EndDateUserPreferenceRequest request = new EndDateUserPreferenceRequest();
            request.setUuid(String.valueOf(numberRequest.getBusinessPartnerId()));
            request.setAttribute("37501");
            request.setDateEnd(numberRequest.getStartDate());

            exchange.getIn().setBody(request);
        }
    }

    /**
     * End date user preference request processor
     */
    private static final class EndDateUserPreferenceRequestProcessor implements Processor {

        public static final Processor INSTANCE = new EndDateUserPreferenceRequestProcessor();

        @Override
        public void process(Exchange exchange) throws Exception {

            NumberRequest numberRequest = exchange.getProperty(NumberRequest.class.getName(), NumberRequest.class);

            EndDateUserPreferenceRequest request = new EndDateUserPreferenceRequest();
            request.setUuid(String.valueOf(numberRequest.getBusinessPartnerId()));
            request.setAttribute("20301");
            request.setDateEnd(numberRequest.getStartDate());

            exchange.getIn().setBody(request);
        }
    }
}

