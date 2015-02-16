package com.conversant.chump.processor;

import com.conversant.chump.model.ApiResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import static com.conversant.chump.util.Constants.PROPERTY_API_RESPONSE;

/**
 * Processor for generic API responses.
 * <p/>
 * Created by Saren Currie on 15-01-20.
 */

public final class ApiResponseProcessor implements Processor {

	public static final Processor INSTANCE = new ApiResponseProcessor();

	@Override
	public void process(Exchange exchange) throws Exception {

		ApiResponse response = ApiResponse.success();
		response.setData(exchange.getIn().getBody());

		exchange.setProperty(PROPERTY_API_RESPONSE, response);
	}
}
