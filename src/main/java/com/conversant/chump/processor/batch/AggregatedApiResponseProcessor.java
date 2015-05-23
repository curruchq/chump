package com.conversant.chump.processor.batch;

import com.conversant.chump.model.ApiResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.List;

import static com.conversant.chump.util.Constants.PROPERTY_API_RESPONSE;

/**
 * Created by jhill on 23/05/15.
 */
public class AggregatedApiResponseProcessor implements Processor {

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

        // Flatten responses if single
        if (response.getResponses().size() == 1) {
            response = response.getResponses().get(0);
        }

        // Set as exchange property to be picked up by ApiResponseProcessor
        exchange.setProperty(PROPERTY_API_RESPONSE, response);
    }
}