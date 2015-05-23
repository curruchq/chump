package com.conversant.chump.processor.batch;

import com.conversant.chump.model.ApiResponse;
import com.conversant.webservice.StandardResponse;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.conversant.chump.util.Constants.PROPERTY_API_RESPONSE;

/**
 * Created by jhill on 23/05/15.
 */
public class BatchAggregationStrategy implements AggregationStrategy {

    private Function<Exchange, String> getResponseMessage;

    public BatchAggregationStrategy(Function<Exchange, String> getResponseMessage) {
        this.getResponseMessage = getResponseMessage;
    }

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        // If was wrapped in trx and an error occurred the ApiResponse in exchange contains the error (the in.body StandardResponse here is success from rolling back trx)
        ApiResponse response = newExchange.getProperty(PROPERTY_API_RESPONSE, ApiResponse.class);

        // Else if not set yet process in.body
        if (response == null) {
            if (newExchange.getIn().getBody() instanceof StandardResponse) {
                response = newExchange.getIn().getBody(StandardResponse.class).isSuccess() ? ApiResponse.success() : ApiResponse.error();
                response.setMessage(getResponseMessage.apply(newExchange));
            }
            else if (newExchange.getIn().getBody() instanceof ApiResponse) {
                response = newExchange.getIn().getBody(ApiResponse.class);
                response.setMessage(getResponseMessage.apply(newExchange));
            }
            else {
                throw new RuntimeException("Unhandled response type");
            }
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
