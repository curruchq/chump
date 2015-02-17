package com.conversant.chump.route;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.ReadSubscriptionRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.GetBusinessPartnerProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.webservice.BusinessPartner;
import com.conversant.webservice.ReadBusinessPartnerResponse;
import com.conversant.webservice.ReadSubscriptionsRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.conversant.chump.common.RestOperation.HttpMethod.GET;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.*;

/**
 * Defines a rest endpoint to get subscriptions by business partner from Adempiere.
 *
 * Created by Saren Currie on 15-02-10.
 */
@Component
public final class SubscriptionRoute implements ChumpRoute {
	private static final String RESOURCE = "/v1/subscriptions";

	public static final ChumpOperation READ = ChumpOperation.builder()
			.trx(false)
			.rest(RestOperation.builder()
					.resource(RESOURCE)
					.path("/{businessPartnerSearchKey}")
					.method(GET)
					.requestType(ReadSubscriptionRequest.class)
					.build())
			.preProcessors(Arrays.asList(
					SubscriptionsRequestProcessor.INSTANCE
			))
			.to(Arrays.asList(
					ChumpOperation.pair(GetBusinessPartnerProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER.getUri()),
					ChumpOperation.pair(GetSubscriptionsProcessor.INSTANCE, AdempiereRoute.READ_SUBSCRIPTIONS.getUri())))
			.postProcessors(
					Arrays.asList(new StandardResponseRemover("subscription"), ApiResponseProcessor.INSTANCE))
			.build();

	private static final class SubscriptionsRequestProcessor implements Processor {

		public static final Processor INSTANCE = new SubscriptionsRequestProcessor();

		@Override
		public void process(Exchange exchange) throws Exception {

			ReadSubscriptionRequest request = exchange.getIn().getBody(ReadSubscriptionRequest.class);

			// TODO: Deserialize query and path parameters automatically
			if (request == null) {
				request = new ReadSubscriptionRequest();
				request.setBusinessPartnerSearchKey((String) exchange.getIn().getHeader("businessPartnerSearchKey"));
			}
		}
	}

	private static final class GetSubscriptionsProcessor implements Processor{

		public static final Processor INSTANCE = new GetSubscriptionsProcessor();

		@Override
		public void process(Exchange exchange) throws Exception {
			BusinessPartner bp = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();

			ReadSubscriptionsRequest request = new ReadSubscriptionsRequest();
			request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_SUBSCRIPTIONS, ADEMPIERE_USER_DRUPAL));
			request.setBusinessPartnerId(bp.getBusinessPartnerId());

			exchange.getIn().setBody(request);
		}
	}
}
