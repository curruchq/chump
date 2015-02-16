package com.conversant.chump.route;

import com.conversant.chump.common.ChumpOperation;
import com.conversant.chump.common.ChumpRoute;
import com.conversant.chump.common.RestOperation;
import com.conversant.chump.model.ReadInvoiceLinesByIdRequest;
import com.conversant.chump.model.ReadInvoiceRequest;
import com.conversant.chump.processor.ApiResponseProcessor;
import com.conversant.chump.processor.GetBusinessPartnerProcessor;
import com.conversant.chump.processor.StandardResponseRemover;
import com.conversant.webservice.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.conversant.chump.common.RestOperation.HttpMethod.GET;
import static com.conversant.chump.util.AdempiereHelper.createLoginRequest;
import static com.conversant.chump.util.Constants.*;

/**
 * Created by Saren Currie on 15-01-16.
 */
@Component
public class InvoiceRoute implements ChumpRoute {
	private static final String RESOURCE = "/v1/invoice";

	public static final ChumpOperation READ = ChumpOperation.builder()
			.trx(false)
			.rest(RestOperation.builder()
					.path("/invoices/{businessPartnerSearchKey}") //Search key
					.resource(RESOURCE)
					.method(GET)
					.requestType(ReadInvoiceRequest.class)
					.build())
			.preProcessors(
					Arrays.asList(InvoiceRequestProcessor.INSTANCE))
			.to(Arrays.asList(
					ChumpOperation.pair(GetBusinessPartnerProcessor.INSTANCE, AdempiereRoute.READ_BUSINESS_PARTNER.getUri()),
					ChumpOperation.pair(GetInvoicesProcessor.INSTANCE, AdempiereRoute.READ_INVOICE.getUri())))
			.postProcessors(
					Arrays.asList(new StandardResponseRemover("invoice"), ApiResponseProcessor.INSTANCE))
			.build();

	public static final ChumpOperation GET_LINES = ChumpOperation.builder()
			.trx(false)
			.rest(RestOperation.builder()
						.path("/invoicelines/{invoiceId}")
						.resource(RESOURCE)
						.method(GET)
						.requestType(ReadInvoiceLinesByIdRequest.class)
						.build()
			).preProcessors(
					Arrays.asList(InvoiceLinesRequestProcessor.INSTANCE)
			).to(Arrays.asList(
					ChumpOperation.pair(GetInvoiceLinesProcessor.INSTANCE, AdempiereRoute.READ_INVOICE_LINES.getUri())))
			.postProcessors(
					Arrays.asList(new StandardResponseRemover("invoiceLine"), ApiResponseProcessor.INSTANCE))
			.build();

	/**
	 * Processes
	 */
	private static final class InvoiceRequestProcessor implements Processor {

		public static final Processor INSTANCE = new InvoiceRequestProcessor();

		@Override
		public void process(Exchange exchange) throws Exception {

			ReadInvoiceRequest request = exchange.getIn().getBody(ReadInvoiceRequest.class);

			// TODO: Deserialize query and path parameters automatically
			if (request == null) {
				request = new ReadInvoiceRequest();
				request.setBusinessPartner((String) exchange.getIn().getHeader("businessPartnerSearchKey"));
			}
		}
	}

	private static final class InvoiceLinesRequestProcessor implements Processor {

		public static final Processor INSTANCE = new InvoiceLinesRequestProcessor();

		@Override
		public void process(Exchange exchange) throws Exception {

			ReadInvoiceLinesByIdRequest request = exchange.getIn().getBody(ReadInvoiceLinesByIdRequest.class);

			// TODO: Deserialize query and path parameters automatically
			if (request == null) {
				request = new ReadInvoiceLinesByIdRequest();
				request.setInvoiceId(Integer.parseInt((String) exchange.getIn().getHeader("invoiceId")));
			}
		}
	}

	private static final class GetInvoicesProcessor implements Processor {

		public static final Processor INSTANCE = new GetInvoicesProcessor();

		@Override
		public void process(Exchange exchange) throws Exception {
			BusinessPartner bp = exchange.getIn().getBody(ReadBusinessPartnerResponse.class).getBusinessPartner();

			ReadInvoicesByBusinessPartnerRequest request = new ReadInvoicesByBusinessPartnerRequest();
			request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_INVOICES, ADEMPIERE_USER_DRUPAL));
			request.setBusinessPartnerId(bp.getBusinessPartnerId());

			exchange.getIn().setBody(request);

		}
	}

	private static final class GetInvoiceLinesProcessor implements Processor {
		public static final Processor INSTANCE = new GetInvoiceLinesProcessor();

		@Override
		public void process(Exchange exchange) throws Exception {

			//ReadInvoiceLinesByIdRequest apiRequest = exchange.getProperty(ReadInvoiceLinesByIdRequest.class.getName(), ReadInvoiceLinesByIdRequest.class);

			ReadInvoiceLinesRequest request = new ReadInvoiceLinesRequest();
			request.setLoginRequest(createLoginRequest(exchange, TYPE_READ_INVOICE_LINES, ADEMPIERE_USER_INTALIO));
			request.setInvoiceId(Integer.parseInt((String)exchange.getIn().getHeader("invoiceId")));

			exchange.getIn().setBody(request);
		}
	}
}
